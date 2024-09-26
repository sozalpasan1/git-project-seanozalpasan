
/**
 * I have neither given nor received unauthorized aid on this assignment.
 * Thank you to .
 * Michael Barr
 */
import java.io.File;
import java.io.IOException;


//git reset --hard HEAD
//resets everything
public class Git {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        deleteEverything();
        initRepo();
        assertEverythingCreated();
        final var blob = new Blob("test.txt");
        blob.saveInObjects();
        
        // final var folder = new Blob("bomboclat/");
        // folder.saveInObjects();
        
        deleteEverything();
    }
    
    
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

        if (doesEverythingExist) {
            System.out.println("Git Repository already exists");
        }
        // Initialized empty Git repository
            
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
        //System.out.println(exists);
    }

    static void deleteEverything() {
        final var file = new File("git");
        if (!file.exists()) {
            return;
        }
        deleteDirectory(file);
        new File("git").delete();
    }
}