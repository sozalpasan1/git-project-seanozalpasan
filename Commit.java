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
import java.nio.file.*;
import java.time.LocalDate;
import Blob;

public class Commit {

    public static void main(String[] args) throws IOException {
        Commit newCommit = new Commit("me", "hello");
        commitToString();
    }

    private static String author;
    private static String commitMessage;
    private static String parent;
    private static String date;
    private static String treeHash;

    public Commit (String author, String commitMessage) throws IOException {
        File HEAD = new File ("git", "HEAD");
        File index = new File ("git", "index");
        // File currentIndexSnapshot = new File("");

        // set parent hash
        if (HEAD.length() == 0) {
            parent = "";
        } else {
            String hash = "";
            BufferedReader reader = new BufferedReader(new FileReader(HEAD));
            hash = reader.readLine();
            parent = hash;
        }

        // set current tree hash
        if (HEAD.length() == 0) {
            treeHash = Blob.getHashForTree(index);
        } else {
            treeHash = getCurrentIndexHash();
        }

        // set author, message, date
        Commit.author = author;
        Commit.commitMessage = commitMessage;

        LocalDate currentDate = LocalDate.now();
        Commit.date = currentDate.toString();
    }

    public static String getTreeHash() {
        return treeHash;
    }

    public static String getParent() {
        return parent;
    }

    public static String getAuthor() {
        return author;
    }

    public static String getDate() {
        return date;
    }

    public static String getMessage() {
        return commitMessage;
    }

    public static void commitToString() {
        System.out.println ("tree: " + getTreeHash() + "\nparent: " + getParent() + "\nauthor: " + getAuthor()
        + "\ndate: " + getDate() + "\nmessage: " + getMessage());
    }

    private static String getCurrentIndexHash() {
        // gets the hash of the current snapshot of index file
        // just add the previous to the index file and then hash
        // the index file bc ur gonna empty it anyway after creating this commit
        //hash of the folder representing the repository's state at the time of the commit)
        // -- get the hash of the previous commit's currentIndexState + the lines inside of the temp index file

        // manually set rootDirectory
        File rootDir = new File ("git", "testFolder");

        final var blob = new Blob(rootDir.getName());
        Blob.saveTreeInObjects(rootDir);

        return "";
    }

    public static void stage (String filePath) throws Exception {

        // clear index
        File index = new File ("git", "index");
        try {
            FileWriter writer = new FileWriter(index.getPath());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // recreate the entire root tree again --> take hash
        getCurrentIndexHash();
    }

    public static String commit(String author, String message) throws IOException {
        
        // create the commit file message with the commit object then write it into the commit file
        Commit newCommit = new Commit(author, message);

        // create a new file with the current index snapshot

        // update the HEAD file to be the hash of the most recent commit

        // the commit file is a blob

        // the commit file blob points to the hash of the root tree

        return "the SHA1 hash of the most recent commit";
    }

}
