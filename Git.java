
/**
 * I have neither given nor received unauthorized aid on this assignment.
 * Thank you to .
 * Michael Barr
*/
import java.util.*;
import java.io.File;
import java.io.IOException;

public class Git {
    static void initRepo() {
        File gitDir = new File("git");
        File objectsDir = new File(gitDir, "objects");
        File indexFile = new File(gitDir, "index");
        final var doesEverythingExist = gitDir.exists() && objectsDir.exists() && indexFile.exists();

        if (!gitDir.exists()) {
            gitDir.mkdir();
        }

        if (!objectsDir.exists()) {
            objectsDir.mkdir();
        }

        if (!indexFile.exists()) {
            try {
                indexFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!doesEverythingExist) {
        }
        // Initialized empty Git repository
        else
            System.out.println("Git Repository already exists");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        initRepo();
        assertEverythingCreated();
        deleteEverything();
    }

    public static void deleteDirectory(File file) {
        // store all the paths of files and folders present
        // inside directory
        for (File subfile : file.listFiles()) {

            // if it is a subfolder,e.g Rohan and Ritik,
            // recursively call function to empty subfolder
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }

            // delete files and empty subfolders
            subfile.delete();
        }
    }

    static void assertEverythingCreated() {
        File gitDir = new File("git");
        File objectsDir = new File(gitDir, "objects");
        File indexFile = new File(gitDir, "index");
        final var exists = gitDir.exists() && objectsDir.exists() && indexFile.exists();
        if (!exists) {
            throw new RuntimeException("Git repository not properly initialized");
        }
    }

    static void deleteEverything() {
        deleteDirectory(new File("git"));
        new File("git").delete();
    }
}