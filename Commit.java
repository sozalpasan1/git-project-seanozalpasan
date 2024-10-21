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

public class Commit {

    public static void main(String[] args) throws Exception {
        Commit newCommit = new Commit("me", "hello");
        stage("/Users/oliviakong/Desktop/everything basically/honors topics in cs 2024/git-project-seanozalpasan-forkedcode/testFolder");
        commit("olivia", "this is a test commit");
        commitToString();
    }

    private static String author;
    private static String commitMessage;
    private static String parent;
    private static String date;
    private static String treeHash;

    public Commit (String author, String commitMessage) throws Exception {
        File HEAD = new File ("git", "HEAD");
        File index = new File ("git", "index");

        // set parent hash
        if (HEAD.length() == 0) {
            parent = "";
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(HEAD))) {
                String hash = reader.readLine();
                Commit.parent = hash;
            } catch (IOException e) {
                e.printStackTrace();
                Commit.parent = "";
            }
        }

        // set current tree hash
        Commit.treeHash = getCurrentIndexHash();

        // set author, message, date
        Commit.author = author;
        Commit.commitMessage = commitMessage;

        LocalDate currentDate = LocalDate.now();
        Commit.date = currentDate.toString();
    }

    public static String makeCommitMessage() {
        String commitMessage = ("tree: " + getTreeHash()
                                + "\nparent: " + getParent()
                                + "\nauthor: " + getAuthor()
                                + "\ndate: " + getDate()
                                + "\nmessage: " + getMessage());
        
        return commitMessage;
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

    // gets the hash of the root directory
    private static String getCurrentIndexHash() throws Exception {

        // manually set rootDirectory
        File rootDir = new File ("/Users/oliviakong/Desktop/everything basically/honors topics in cs 2024/git-project-seanozalpasan-forkedcode/testFolder");

        // get tree hash
        return Blob.getHashForTree(rootDir);

    }

    // stages by clearing index and creating blobs for root dir
    public static void stage (String filePath) throws Exception {

        // clear index
        File index = new File ("git", "index");
        try {
            FileWriter writer = new FileWriter(index.getPath());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create blobs for root tree
        final var rootDir = new Blob("testFolder/");
        rootDir.blob();

    }

    // commits by creating new commit message, saving commit file to objects, updating HEAD
    public static String commit(String author, String message) throws Exception {
        
        // create the commit file message
        Commit newCommit = new Commit(author, message);
        String commitMessage = makeCommitMessage();

        // get hash of commit file
        String commitHash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] messageDigest = md.digest(commitMessage.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            commitHash = no.toString(16);
            while (commitHash.length() < 40) {
                commitHash = "0" + commitHash;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // save commit file to objects
        File file = new File("./git/objects/" + commitHash);
        if (!file.exists()) {
            file.createNewFile();
        }

        // update the HEAD file to be the hash of the most recent commit
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./git/HEAD"))) {
            writer.write(commitHash);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commitHash;
    }

}