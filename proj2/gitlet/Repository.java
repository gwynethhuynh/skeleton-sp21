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
        if (!checkBlobCommitted(addedBlob)) {
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
    private static Boolean checkBlobCommitted(Blob addedBlob) {
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
                return true;
            }
        }
        return false;
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
        //displays information about all commits ever made.
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

    public static void find(String message) {
        List<String> commitIDs = Utils.plainFilenamesIn(Commit.COMMITS_DIR);
        boolean commitExists = false;
        for (String commitID : commitIDs) {
            Commit commit = getCommit(commitID);
            if (commit.getMessage().equals(message)){
                System.out.println(commitID);
                commitExists = true;
            }
        }
        if (!commitExists) {
            throw new RuntimeException("Found no commit with that message.");
        }
    }

    public static void status() {
        //Display list of branches
        System.out.println("=== Branches ===");
        List<String> branches = Utils.plainFilenamesIn(BRANCHES_DIR);
        String head = Utils.readContentsAsString(HEAD); // heads/branch
        File branch_head = join(GITLET_DIR, head);
        for (String branch : branches) {
            File currBranch = join(BRANCHES_DIR, branch);
            if (branch_head.equals(currBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }

        //Display list of staged Files
        System.out.println("=== Staged Files ===");
        List<String> adds = Utils.plainFilenamesIn(ADD_DIR);
        for (String blob: adds) {
            System.out.println(blob);
        }
        //Display list of removed Files
        System.out.println("=== Removed Files ===");
        List<String> rms = Utils.plainFilenamesIn(RM_DIR);
        for (String blob: rms) {
            System.out.println(blob);
        }
    }
    public static void checkoutFileHead(String fileName) {
        //get Head commit
        Commit head = getHEADCommit();
        checkoutFile(fileName, head);
    }

    public static void checkoutFileCommitID(String commitID, String fileName) {
        Commit commit = getCommit(commitID);
        checkoutFile(fileName, commit);
    }

    public static void checkoutFile(String fileName, Commit commit) {

        //give fileName, find the corresponding blobID for that commit
        HashMap<String, String> headBlobs = commit.getBlobs();
        //make iterator hashmap of blobs
        Boolean fileExists = false;
        for (Map.Entry<String, String> blob : headBlobs.entrySet()) {
            String name = blob.getKey();
            String ID = blob.getValue();
            if (fileName.equals(name)) {
                String contents = getBlobContents(ID);
                File replacement = Utils.join(CWD, fileName);
                Utils.writeContents(replacement, contents);
                fileExists = true;
            }
        }
        if (!fileExists) {
            throw new RuntimeException("File does not exist in that commit.");
        }
    }

    /**Give a commitID, checks out all of the files tracked by the given commit.
     * Removes tracked files that are not present in the given commit
     * @param commitID
     */
    private static void updateCWD(String commitID) {
        Commit commit = getCommit(commitID);
        HashMap<String, String> blobs = commit.getBlobs();
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String fileName : cwdFiles) {
            if (blobs.containsKey(fileName)) {
                checkoutFile(fileName, commit); //checks out all files tracked by given commit
            } else {
                File cwdFile = join(CWD, fileName);
                Utils.restrictedDeleteCWD(cwdFile); //removes tracked files that are not present in that commit
            }
        }
    }

    private static void clearStaging() {
        List<String> filesStagedToAdd = Utils.plainFilenamesIn(ADD_DIR);
        List<String> filesStagedToRm = Utils.plainFilenamesIn(RM_DIR);
        for (String fileName : filesStagedToAdd) {
            File stagedFile = join(ADD_DIR, fileName);
            Utils.restrictedDelete(stagedFile);
        }
        for (String fileName : filesStagedToRm) {
            File stagedFile = join(RM_DIR, fileName);
            Utils.restrictedDelete(stagedFile);
        }
    }

    public static void reset(String commitID) {
        //updateCWD
        updateCWD(commitID);

        // clear staging area
        clearStaging();

        //move current branch's head to that commit node
        updateHEAD(commitID);


    }

    public static void checkoutBranch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            throw new RuntimeException("No such branch exists.");
        }

        //if branch is the current branch, "no need to checkout the current branch"
        String head = Utils.readContentsAsString(HEAD);
        File branch_head = join(GITLET_DIR, head);
        String checkoutBranchPath = branch.getAbsolutePath();
        String headBranchPath = branch_head.getAbsolutePath();
        if (checkoutBranchPath.equals(headBranchPath)) {
            throw new RuntimeException("No need to checkout the current branch");
        }


        //If a working file is untracked in the current branch and would be overwritten by the checkout,
        // print There is an untracked file in the way; delete it, or add and commit it first. and exit;
        // perform this check before doing anything else. Do not change the CWD.
        List<String> CWDBlobs = Utils.plainFilenamesIn(CWD);
        for (String blobName: CWDBlobs) {
            File CWDBlob = join(CWD, blobName);
            Blob blob = new Blob(blobName, CWDBlob);
            if (!checkBlobCommitted(blob)) {
                throw new RuntimeException("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
            }
        }
        //updates CWD to match the given branch
        String branchCommitID = getBranchCommitID(branchName);
        updateCWD(branchCommitID);

        //change head file to reflect new branch as head
        Utils.writeContents(HEAD, "branches/" + branchName);

    }

    public static void branch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        if (branch.exists()) {
            throw new RuntimeException("A branch with that name already exists.");
        } else {
            String currCommitID = getHEADCommitID();
            createBranch(branchName, currCommitID);
        }
    }

    public static void rmBranch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        //If branch does not exist, throw error.
        if (!branch.exists()) {
            throw new RuntimeException("A branch with that name does not exist.");
        }
        //If the branch you try ot remove is the branch you're currently on, abort.
        String currCommitID = getHEADCommitID();
        String branchCommitID = Utils.readContentsAsString(branch);
        if (currCommitID == branchCommitID) {
            throw new RuntimeException("Cannot remove the current branch.");
        }
        //Delete the branch pointer with the given name.
        Utils.restrictedDeleteBranch(branch);
    }


    /** Returns the current Commit ID of the HEAD as a string */
    public static String getHEADCommitID() {
        String head = Utils.readContentsAsString(HEAD); // branches/[branch head]
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

    public static String getBranchCommitID(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        String branchCommitID = Utils.readContentsAsString(branch);
        return branchCommitID;
    }

    public static Commit getCommit(String commitID) {
        File commit = join(Commit.COMMITS_DIR, commitID);
        if (!commit.exists()) {
            throw new RuntimeException("No commit with that id exists.");
        }
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

    /** Returns contents of Blob */
    private static String getBlobContents(String blobID) {
        System.out.println(blobID);
        File blob = join(Blob.BLOBS_DIR, blobID);
        String contents = Utils.readContentsAsString(blob);
        return contents;
    }
}
