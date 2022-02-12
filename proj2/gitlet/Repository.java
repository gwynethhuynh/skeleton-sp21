package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */



    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    //head
    public static File HEAD = Utils.join(GITLET_DIR, "HEAD");

    public static File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");

    public static File STAGING_DIR = Utils.join(GITLET_DIR, "Staging");


    /* TODO: fill in the rest of this class. */

    /**
     * Makes a .gitlet directory if it does not exist in current directory.
     * Creates an initial commit object.
     * It ill have a single branch: master, which initially points to this initial commit
     * initial commit timestamp = 00:00:00 UTC, Thursday, 1 January 1970
     * All initial commits should have the same UID b/c they will all have the same content
     * Failure case: if .gitlet directory already exists, it should abort and print
     * "A Gitlet version-control system already exists in the current directory."
     */

    /** Creates .gitlet directory and refs and objects subdirectories */
    public static void setUpPersistence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            File OBJECTS_DIR = join(GITLET_DIR, "objects");
            OBJECTS_DIR.mkdir();
            STAGING_DIR.mkdir();

        }
    }
    public static void init(){
        if (GITLET_DIR.exists()) {
            throw new FileSystemAlreadyExistsException("A Gitlet version-control system already exists in the current directory.");
        }
        setUpPersistence();
        Utils.writeContents(HEAD, "branches/master");
        //Branch master = new Branch(HEAD); //create a new master branch
        Commit initial = new Commit("initial commit", null);
        initial.saveCommit();
        String commitID = initial.getCommitID();
        createBranch("master", commitID);


    }

    public static void add(String file_name) {
        //check if file exists in the cwd
        File plain = join(CWD, file_name);
        if (!plain.exists()) {
            return;
        }
        //create blobID
        String blobID = Utils.sha1(Utils.serialize(plain));

        //check if file is the same in commit (do not add it)
        String head = Utils.readContentsAsString(HEAD); // heads/branch
        File branch_head = join(GITLET_DIR, head);
        String currCommitID = Utils.readContentsAsString(branch_head);
        File BlobID = join(".gitlet/objects/commits" + currCommitID, file_name);
        String currBlobID = Utils.readContentsAsString(BlobID);
        if (currBlobID == blobID) {
            return;
        }

        //create ADD_DIR
        File ADD_DIR = join(STAGING_DIR, "Add");
        if (!ADD_DIR.exists()) {
            ADD_DIR.mkdir();
        }

        //check if it is already in STAGING_DIR
        File blobStagedAdd = join("Staging/Add", file_name);
        Utils.restrictedDelete(blobStagedAdd); //deletes blob file if it already exists

        //add it to staging
        File blobName = join(ADD_DIR, file_name);
        String text = Utils.readContentsAsString(plain); //Reads the contents of blob and saves as String
        Utils.writeContents(blobName,text); //creates blob file in Add folder

        //check if it is staged for removal
        File rm = join("Staging/Rm", file_name);
        Utils.restrictedDelete(rm);

    }


    private static void checkBlob() {
        //check if staged
        //check if in current commit already

    }

    private static void createBlobMap() {
        //need blobMap to persist --> how should I do this? Hash Table?

    }


    public static void commit() {
        //Read from my computer the head commit object and the staging area
        //Clone the HEAD commit
        //Modify its message and timestamp according to the user input
        //Use the staging area in order to modify the files tracked by the new commit

        //Write back any new objects made or any objects modified
    }

    public static String getHEADCommitID() {
        return "";
    }

    public static void createBranch(String branch_name, String commitID) {
        if(!BRANCHES_DIR.exists()) {
            BRANCHES_DIR.mkdir();
        }
        File branch = Utils.join(BRANCHES_DIR, branch_name);
        Utils.writeContents(branch, commitID);
    }

    public static void updateBranch(String commitID) {

    }
}
