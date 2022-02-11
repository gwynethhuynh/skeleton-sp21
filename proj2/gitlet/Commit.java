package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

import static gitlet.Utils.join;

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
    // something to keep track files this commit is tracking
    private final String parent;
    private String commitID;
    static final File COMMITS_DIR = Utils.join(".gitlet/objects", "commits");


    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent) {
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
        File commit = join(COMMITS_DIR, commitID);
        Utils.writeObject(commit, this);
    }
}
