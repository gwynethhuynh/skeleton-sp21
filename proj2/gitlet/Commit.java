package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    //private final String timestamp;
    private HashMap<String, String> blobs; // The key is the text file name and the value the blob SHA-1 ID.
    private final String parent;
    private String commitID;
    static final File COMMITS_DIR = Utils.join(".gitlet/objects", "commits");


    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent, List<String> addedBlobs, List<String> removedBlobs) {
        if (!COMMITS_DIR.exists()) {
            COMMITS_DIR.mkdir();
        }
        this.message = message;
        this.parent = parent;
        //if (this.parent == null) {
            //this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        //} else {

        //}
        this.commitID = "";
        //obtain parent's hashmap of blobs
        File parentFile = join(".gitlet/objects/commits/" + parent, parent);
        Commit parentCommit = Utils.readObject(parentFile, Commit.class);
        HashMap<String, String> parentBlobs = parentCommit.getBlobs();

        //clone Hashmap
        this.blobs = (HashMap<String, String>) parentBlobs.clone();

        //make changes to  parent HashMap of Blobs
        for (String blob : addedBlobs) {
            File added_blob = join(".gitlet/Staging/Add", blob);
            String addedBlobID = sha1(added_blob);
            String addedBlobContents = Utils.readContentsAsString(added_blob);
            if (!blobs.containsKey(blob)) {
                blobs.put(blob, addedBlobID);
            } else {
                blobs.replace(blob, addedBlobID);
            }
            //created file for blob in objects/blobs directory
            File blobObj = join(".gitlet/objects/blobs", addedBlobID);
            Utils.writeContents(blobObj, addedBlobContents);

            //deleted blob in staging area after done
            Utils.restrictedDelete(added_blob);
        for (String rmBlob : removedBlobs) {
            File removed_blob = join(".gitlet/Staging/Rm", rmBlob);
            blobs.remove(removed_blob);
            Utils.restrictedDelete(removed_blob);
        }




            //read contents (obtain blobID) of blob file in staging area
            //create a hashmap to include these
        }


    }

    public String getCommitID() {return this.commitID; }

    public String getMessage() {
        return this.message;
    }

    //public String getTimestamp() {
        //return this.timestamp;
    //}

    public String getParent() {
        return this.parent;
    }

    public void saveCommit(){
        //save Commit Object
        this.commitID = Utils.sha1(Utils.serialize(this));
        File COMMIT_FOLDER = join(COMMITS_DIR, commitID);
        File commit = join(COMMIT_FOLDER, commitID);
        COMMIT_FOLDER.mkdir();
        Utils.writeObject(commit, this);
    }

    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }
}
