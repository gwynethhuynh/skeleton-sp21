package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;



public class Blob implements Serializable {
    private String blobID;
    public static final File BLOBS_DIR = Utils.join(".gitlet/objects", "blobs");
    public final String contents;
    private final String name;

    public Blob(String name, File blobFile) {
        if (!BLOBS_DIR.exists()) {
            BLOBS_DIR.mkdir();
        }
        this.name = name;
        this.blobID = Utils.sha1(Utils.serialize(blobFile));
        this.contents = Utils.readContentsAsString(blobFile);
    }

    public void saveBlob() {
        File blob = join(BLOBS_DIR, blobID);
        Utils.writeContents(blob, contents);
    }

    public String getBlobContents() {
        return this.contents;
    }

    public String getBlobID() {
        return this.blobID;
    }

    public String getBlobName() {
        return this.name;
    }

    public void addBlob() {

    }
    public void removeBlob() {

    }

}
