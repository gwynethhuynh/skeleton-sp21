package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                // TODO: handle the `add [filename]` command
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                validateNumArgs("commit", args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                Repository.status();
                break;

            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    Repository.checkoutFileHead(args[2]);
                } else if (args.length == 4) {
                    Repository.checkoutFileCommitID(args[1], args[3]);
                } else {
                    throw new RuntimeException(
                            String.format("Invalid number of arguments for: %s.", "checkout"));
                }
                break;

            case "branch":
                validateNumArgs("branch", args, 2);
                Repository.branch(args[1]);
                break;

            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                Repository.rmBranch(args[1]);
                break;

            case "reset":
                validateNumArgs("reset", args, 2);
                Repository.reset(args[1]);
                break;

            case "merge" :
                validateNumArgs("merge", args, 2);
                Repository.merge(args[1]);
                break;
        }
    }

    /** Don't do this. Main class should be short.
    public static void init() {
        //get the current working directory.
        File CWD = new File(System.getProperty("user.dir"));
        Commit initial = new Commit("initial commit", null);
        //branches? Here we need to initialize a master branch and have it point to initial commit.
    }
     */

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
