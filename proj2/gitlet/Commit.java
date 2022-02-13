package gitlet;

// TODO: any imports you need here

//import org.antlr.v4.runtime.tree.Tree;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

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
    private final String timestamp;
    private TreeMap<String, String> blobs; // The key is the text file name and the value the blob SHA-1 ID.
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
        this.commitID = "";
        this.blobs = makeBlobMap(addedBlobs, removedBlobs);
        if (parent != "") {
            this.timestamp = Instant.ofEpochSecond(0).toString();
        } else {
            long unixTime = Instant.now().getEpochSecond();
            this.timestamp = Instant.ofEpochSecond(unixTime).toString();
        }

    }

    private TreeMap<String, String> makeBlobMap(List<String> addedBlobs, List<String> removedBlobs) {
        //obtain parent's treemap of blobs
        File parentFile = join(".gitlet/objects/commits/" , parent);
        TreeMap<String, String> parentBlobs;
        if (parent == "") {
            parentBlobs = new TreeMap<>();
        } else {
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            parentBlobs = parentCommit.getBlobs();
        }
        //clone TreeMap.
        this.blobs = (TreeMap<String, String>) parentBlobs.clone();

        for (String blobName : addedBlobs) {
            File blob = join(".gitlet/Staging/Add", blobName);
            String addedBlobID = Utils.sha1(Utils.serialize(blob));
            if (!blobs.containsKey(blobName)) {
                blobs.put(blobName, addedBlobID);
            } else {
                blobs.replace(blobName, addedBlobID);
            }

            //Create instance of blob.
            Blob blobObj = new Blob(blobName, blob);

            //Save Blob in blobs directory
            blobObj.saveBlob();

            //deleted blob in staging area after done
            Utils.restrictedDelete(blob);


        }
        for (String rmBlob : removedBlobs) {
            //remove file from Treemap
            blobs.remove(rmBlob);
            //remove file from Staging Area
            File removed_blob = join(".gitlet/Staging/Rm", rmBlob);
            Utils.restrictedDelete(removed_blob);
        }

        return blobs;

    }

    public String getCommitID() {
        return this.commitID;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getParent() {
        return this.parent;
    }

    public void saveCommit(){
        //save Commit Object
        this.commitID = Utils.sha1(Utils.serialize(this));
        File commit = join(COMMITS_DIR, commitID);
        Utils.writeObject(commit, this);
    }

    public TreeMap<String, String> getBlobs() {
        return this.blobs;
    }
}
