package gitlet;

import java.io.File;
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

    public static String HEAD;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

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
            File REFS_DIR = join(GITLET_DIR, "refs");
            REFS_DIR.mkdir();
            File OBJECTS_DIR = join(GITLET_DIR, "objects");
            OBJECTS_DIR.mkdir();

        }
    }
    public static void init(){
        setUpPersistence();
        HEAD = "master";
        Branch master = new Branch(HEAD); //create a new master branch
        Commit initial = new Commit("initial commit", null);
        initial.saveCommit();


        // master is a file that contains SHA-1 identifer of head commit.
        String commitID = Utils.sha1(Utils.serialize(initial));
        // Utils.writeContents(master, commitID);
        //File HEAD = join(GITLET_DIR, "HEAD");
        //Utils.writeContents(HEAD,".gitlet/refs/" + HEAD);

            //create a refs directory that contains empty heads directory
            //create a HEAD file that contains path (refs/heads/master)

    }

    public void commit() {
        //Read from my computer the head commit object and the staging area
        //Clone the HEAD commit
        //Modify its message and timestamp according to the user input
        //Use the staging area in order to modify the files tracked by the new commit

        //Write back any new objects made or any objects modified
    }
}
