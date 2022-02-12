package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;



public class Blob implements Serializable {
    private final String parent;
    private String blobID;
    public final File BLOBS_DIR = Utils.join(".gitlet/objects", "blobs");
    public final String contents;

    public Blob(String parent, File contents) {
        this.parent = parent;
        this.blobID = "";
        this.contents = Utils.readContentsAsString(contents);
    }

    public void saveBlob() {
        this.blobID = Utils.sha1(Utils.serialize(this));
        File blob = join(BLOBS_DIR, blobID);
        Utils.writeContents(blob, contents);
    }
}
