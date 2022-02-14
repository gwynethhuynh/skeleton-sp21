package gitlet;

// TODO: any imports you need here

//import org.antlr.v4.runtime.tree.Tree;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
        this.blobs = makeBlobMap(addedBlobs, removedBlobs);
        Formatter formatter = new Formatter();
        Date date = new Date();
        if (parent == "") {
            date = new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime();
        }
        this.timestamp = formatter.format("Date: %1ta %1tb %1te %1tT %1tY %1tz" ,
                date, date, date, date, date, date).toString();
        this.commitID = Utils.sha1(Utils.serialize(this));
    }

    private HashMap<String, String> makeBlobMap(List<String> addedBlobs, List<String> removedBlobs) {
        //obtain parent's hashmap of blobs
        File parentFile = join(COMMITS_DIR , parent);
        HashMap<String, String> parentBlobs;
        if (parent == "") {
            parentBlobs = new HashMap<>();
        } else {
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            parentBlobs = parentCommit.getBlobs();
        }
        //clone HashMap.
        this.blobs = (HashMap<String, String>) parentBlobs.clone();

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
            //remove file from HashMap
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
        File commit = join(COMMITS_DIR, commitID);
        Utils.writeObject(commit, this);
    }

    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    public Boolean containsFile(String file_name) {
        HashMap<String, String> blobs = getBlobs();
        return blobs.containsKey(file_name);
    }
}
