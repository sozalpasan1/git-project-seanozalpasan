import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

    public void saveInObjects() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./git/index", true))) {
            writer.write(getName());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File("./git/objects/" + getName());
        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(this.fileName));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        //sean added code below
        fis.close();
        //sean added code above
        final byte[] hash = digest.digest();
        return new BigInteger(1, hash).toString(16);
    }
}