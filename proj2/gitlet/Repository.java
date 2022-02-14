package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.*;

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

    public static File ADD_DIR = Utils.join(STAGING_DIR, "Add");

    public static File RM_DIR = Utils.join(STAGING_DIR, "Rm");


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
            ADD_DIR.mkdir();
            RM_DIR.mkdir();

        }
    }
    public static void init(){
        if (GITLET_DIR.exists()) {
            throw new FileSystemAlreadyExistsException("A Gitlet version-control system already exists in the current directory.");
        }
        setUpPersistence();
        Utils.writeContents(HEAD, "branches/master");
        //Branch master = new Branch(HEAD); //create a new master branch
        Commit initial = new Commit("initial commit", "", new ArrayList<>(), new ArrayList<>()); //What type of list?
        initial.saveCommit();
        String commitID = initial.getCommitID();
        createBranch("master", commitID);


    }

    public static void add(String file_name) {
        //check if file exists in the cwd
        File plain = join(CWD, file_name);
        if (!plain.exists()) {
            throw new InaccessibleObjectException("File does not exist.");
        }

        //create a new blob object
        Blob addedBlob = new Blob(file_name, plain);

        //check if we should add this blob to staging
        if (checkBlob(addedBlob)) {
            addToStaging(addedBlob);
            //check if it is staged for removal
            File rm = join(RM_DIR, file_name);
            if (rm.exists()) {
                Utils.restrictedDelete(rm);
            }
        }
    }

    /** Adds Blob to Add Directory within Staging Directory */
    private static void addToStaging(Blob add) {
        File blobName = join(ADD_DIR, add.getBlobName());
        String text = add.getBlobContents(); //Get contents of blob and saves as String
        Utils.writeContents(blobName,text); //creates blob file in Add folder
    }

    /** TODO: Fix so that addedBlob will be removed if I add the old version (matches commit)
     * Checks if blob is the same in the HEAD commit.
     * Checks if blob is already in the Add Directory within the Staging Directory.
     *
     * @param addedBlob
     */
    private static Boolean checkBlob(Blob addedBlob) {
        //check if file is the same in commit or if it exists in the commit;
        Commit currCommit = getHEADCommit();
        HashMap<String, String> blobMap = currCommit.getBlobs();
        File blobStagedAdd = join(ADD_DIR, addedBlob.getBlobName());
        if (blobMap.containsKey(addedBlob.getBlobName())) {
            String currBlobID = blobMap.get(addedBlob.getBlobName());
            if (currBlobID == addedBlob.getBlobID()) {
                if (blobStagedAdd.exists()) {
                    Utils.restrictedDelete(blobStagedAdd);
                }
                return false;
            }
        }
        return true;
    }

    public static void commit(String message) {
        if (message == "" || message == null) {
            throw new RuntimeException("Please enter a commit message");
        }
        //If no files have been staged, throw error and abort.
        List<String> addedBlobs = getStagedAdd();
        List<String> rmBlobs = getStagedRm();
        if (addedBlobs.isEmpty() && rmBlobs.isEmpty()) {
            throw new RuntimeException("No changes added to the commit.");
        }

        //Read from my computer the head commit object and the staging area
        String parentCommitID = getHEADCommitID();

        //Create new Commit Object.
        Commit current = new Commit(message, parentCommitID, addedBlobs, rmBlobs);
        current.saveCommit();

        //update HEAD
        updateHEAD(current.getCommitID());
    }

    public static void rm(String fileName) {
        //Check if it is staged for addition
        File addedBlob = join(ADD_DIR, fileName);
        Commit currCommit = getHEADCommit();
        if (addedBlob.exists()) {
            Utils.restrictedDelete(addedBlob);

        //Check if it is in the current commit. If it is, stage for removal and remove from CWD
        } else if (currCommit.containsFile(fileName)) {
            File removedBlob = join(RM_DIR, fileName);
            Utils.writeContents(removedBlob, "");
            File blob = join(CWD, fileName);
            if (blob.exists()) {
                Utils.restrictedDeleteCWD(blob);
            }
        } else {
            throw new RuntimeException("No reason to remove the file.");
        }

    }

    public static void log() {
        //display information about each commit backwards along commit tree until initial commit
        //follow first parent commits and ignore 2nd parents
        String currID = getHEADCommitID();
        Commit currCommit = getCommit(currID);
        while (currCommit.getParent() != "") {
            printLogs(currID, currCommit);
            currID = currCommit.getParent();
            currCommit = getCommit(currID);
        }
    }

    public static void globalLog() {
        //displays information about commits every made.
        List<String> commitIDs = Utils.plainFilenamesIn(Commit.COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit commit = getCommit(commitID);
            printLogs(commitID, commit);
        }
    }

    private static void printLogs(String ID, Commit commit) {
        System.out.println("===");
        System.out.println("commit " + ID);
        System.out.println("Date: " + commit.getTimestamp().toString());
        System.out.println(commit.getMessage());
        System.out.println();
    }



    /** Returns the current Commit ID of the HEAD as a string */
    public static String getHEADCommitID() {
        String head = Utils.readContentsAsString(HEAD); // heads/branch
        File branch_head = join(GITLET_DIR, head);
        String currCommitID = Utils.readContentsAsString(branch_head);
        return currCommitID;
    }

    /** Returns the current Commit Object of the Head */
    public static Commit getHEADCommit() {
        String commitID = getHEADCommitID();
        File commit = join(Commit.COMMITS_DIR, commitID);
        Commit currCommit = Utils.readObject(commit, Commit.class);
        return currCommit;
    }

    public static Commit getCommit(String commitID) {
        File commit = join(Commit.COMMITS_DIR, commitID);
        Commit currCommit = Utils.readObject(commit, Commit.class);
        return currCommit;
    }

    /** Returns a List of all of the files staged to be added. */
    private static List<String> getStagedAdd() {
        return Utils.plainFilenamesIn(".gitlet/Staging/Add");
    }

    /** Returns a List of all of the files staged to be removed. */
    private static List<String> getStagedRm() {
        return Utils.plainFilenamesIn(".gitlet/Staging/Rm");
    }

    /**Creates a new branch in the commit tree */
    public static void createBranch(String branch_name, String commitID) {
        if(!BRANCHES_DIR.exists()) {
            BRANCHES_DIR.mkdir();
        }
        File branch = Utils.join(BRANCHES_DIR, branch_name);
        Utils.writeContents(branch, commitID);
    }


    /** Updates branch_head file to contain current commit ID */
    public static void updateHEAD(String commitID) {
        String head = Utils.readContentsAsString(HEAD); // heads/branch
        File branch_head = join(GITLET_DIR, head);
        Utils.writeContents(branch_head, commitID);
    }

}
