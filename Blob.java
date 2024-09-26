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

public class Blob {
    String fileName;

    public Blob(String fileName) {
        this.fileName = fileName;
    }

    public void blob() throws Exception{
        File file = new File(fileName);
        if(!file.exists()){
            throw new FileNotFoundException();
        }
        if(file.isFile()){
            saveFileInObjects();
        }
        if(file.isDirectory()){
            treeBlob(file);
        }
    }
    private void treeBlob(File directory) throws Exception{
        for(File childFile : directory.listFiles()){
            if(childFile.isDirectory()){
                if(!childFile.equals(directory)){
                    saveTreeInObjects();
                    treeBlob(childFile);
                }
            } else {
                Blob chFile = new Blob(childFile.getPath());
                chFile.saveFileInObjects();
            }
            
        }
        saveTreeInObjects();
    }

    private void saveTreeInObjects() throws Exception{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./git/index", true))) {
            File file = new File(fileName);
            writer.write("tree " + getHashForTree() + " " + file.getPath());
            writer.newLine();
            writer.close();
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File("./git/objects/" + getHashForTree());
        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(getAllFilesToHash());
            writer.newLine();
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getAllFilesToHash(){
        StringBuilder hashOfAllStringBuilder = new StringBuilder();
        File baseDir = new File(fileName);
        for(File childFile: baseDir.listFiles()){
            hashOfAllStringBuilder.append(childFile.getPath() + " ");
        }

        String hashOfAllStrings = hashOfAllStringBuilder.toString();
        return hashOfAllStrings;
    }

    public String getHashForTree(){
        String hashOfAllStrings = getAllFilesToHash();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] messageDigest = md.digest(hashOfAllStrings.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }
            //System.out.println(hashOfAllStrings);
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFileInObjects() throws Exception {
        File file = new File("./git/objects/" + getSha1());
        if (!file.exists()) {
            file.createNewFile();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./git/index", true))) {
            writer.write("blob " + getName());
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