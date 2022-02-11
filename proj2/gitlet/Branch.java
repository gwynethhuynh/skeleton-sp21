package gitlet;

import java.io.File;

import static gitlet.Utils.join;

public class Branch {

    private File BRANCH_HEADS_DIR = join(".gitlet/refs", "branch_heads");
    private String name;
    private File branch_head;

    public Branch(String name) {
        this.name = name;
        if (!BRANCH_HEADS_DIR.exists()) {
            BRANCH_HEADS_DIR.mkdir();
        }
        branch_head = join(BRANCH_HEADS_DIR, name); //create_branch()?
        Utils.writeContents(branch_head, "");
        File HEAD_File = join(".gitlet", "HEAD"); //contains file path branch or commit ID
        Utils.writeContents(HEAD_File, "refs/branch_heads/" + name);

    }

    public String getName() {
        return this.name;
    }

    public void updateBranch(String commitID) {
        Utils.writeContents(branch_head, commitID); //change contents of branch head file
    }


}
