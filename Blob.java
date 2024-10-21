import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.nio.file.*;

public class Blob {
    static String fileName;
    public Blob(String fileName) {
        this.fileName = fileName;
    }
    
    //IMPORTANT
    //for codes that commented in places we are writing, that was just because i was unsure of formatting
    //the commented code is the full relative path, and the current code is just the name

    //this is where we write the hashes of all subtrees and files when we blob a tree
    static File writeFile = new File("./writeFileOf" + fileName);

    public void blob() throws Exception{
        File file = new File(fileName);
        if(!file.exists()){
            throw new FileNotFoundException();
        }
        if(!file.canRead() && !file.canWrite()){
            System.out.println("dont have perms to read or write this file");
            throw new IllegalArgumentException();
        }
        if(file.isHidden()){
            System.out.println("this file is hidden. make it unhidden to save!");
            throw new IllegalArgumentException();
        }
        if(file.isFile()){
            saveFileInObjects();
        }
        if(file.isDirectory()){
            treeBlob(file);
        }
        writeFile.delete();
    }
    
    private void treeBlob(File directory) throws Exception{
        //this how we traverse through the directory
        for(File childFile : directory.listFiles()){
            if(childFile.isDirectory()){
                treeBlob(childFile);
            } else {
                Blob chFile = new Blob(childFile.getPath());
                chFile.saveFileInObjects();
            }
        }
        saveTreeInObjects(directory);
    }

    //this is where we write into index and create the hash file in objects for a tree
    public static void saveTreeInObjects(File dir) throws Exception{
        writeEverythingIntoWriteFile(dir);
        String treeHash = getHashForTree(writeFile); //scroll down to read about the first line
        //the 2 lines above carry
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./git/index", true))) {
            if(dir.getParentFile() != null){
                writer.write("tree " + treeHash + " " + dir.getParentFile().getName() + "/" + dir.getName());
            } else {
                writer.write("tree " + treeHash + " " + dir.getName());
            }
            //writer.write("tree " + treeHash + " " + dir.getPath());
            writer.newLine();
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        File treeHashFile = new File("./git/objects/" + treeHash);
        if (!treeHashFile.exists()) {
            treeHashFile.createNewFile();
        }

        //this is where we write all the contents of the file that has all the correctly formatted stuff of the tree
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(treeHashFile, true))) {
            Path bytes = writeFile.toPath();
            String theStuffWeWroteInTempFile = new String(Files.readAllBytes(bytes));
            writer.write(theStuffWeWroteInTempFile);
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        //clears all of writeFile for the next time it runs on a tree
        BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile, false));
        writer.write("");
        writer.close();

    }

    public static void writeEverythingIntoWriteFile(File dir) throws Exception{
        BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile, true));
        intoWriteFileHelper(dir, writer);
        writer.close();
    }
    /*
     * together they prepare the file that helps us find the hash of a tree. 
     * that means writing into a file all the hashes of the subtrees and files in that original tree.
     */
    private static void intoWriteFileHelper(File dir, BufferedWriter writer) throws Exception{
        for(File childFile : dir.listFiles()){
            if(childFile.isDirectory()){
                Blob chFile = new Blob(childFile.getPath());
                chFile.writeEverythingIntoWriteFile(childFile);
                //writer.write("tree " + chFile.getHashForTree(chFile.writeFile) + " " + childFile.getPath() + "\n");
                writer.write("tree " + chFile.getHashForTree(chFile.writeFile) + " " + childFile.getName() + "\n");
            } else {
                Blob chFile = new Blob(childFile.getPath());
                //writer.write("blob " + chFile.getSha1() + " " +chFile.fileName + "\n");
                writer.write("blob " + chFile.getSha1() + " " + childFile.getName() + "\n");
            }
        }
    }


    //its called getHashForTree because the parameter is a tree
    //other than that its standard hashing
    public static String getHashForTree(File hashThisFile){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] messageDigest = md.digest(Files.readAllBytes(hashThisFile.toPath()));
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //saves file in objects and writes it into index
    public void saveFileInObjects() throws Exception {
        File file = new File("./git/objects/" + getSha1());
        if (!file.exists()) {
            file.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./git/index", true))) {
            File makeItEasy = new File(fileName);
            if(makeItEasy.getParentFile() != null){
                writer.write("blob " + getSha1() + " " + makeItEasy.getParentFile().getName() + "/" + makeItEasy.getName());
            } else {
                writer.write("blob " + getSha1() + " " + makeItEasy.getName());
            }
            //writer.write("blob " + getName());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(this.fileName));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //returns the hash and filepath of the file
    public String getName() throws Exception {
        return getSha1() + " " + this.fileName;
    }

    public String getSha1() throws Exception {
        File file = new File(this.fileName);
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        fis.close();
        final byte[] hash = digest.digest();
        return new BigInteger(1, hash).toString(16);
    }
}