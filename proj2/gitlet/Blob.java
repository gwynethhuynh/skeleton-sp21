package gitlet;

import java.io.File;

public class Blob {
    private final String parent;

    public Blob(String parent, File contents) {
        this.parent = parent;

    }
}
