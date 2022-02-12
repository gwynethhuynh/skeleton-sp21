# Gitlet Design Document

**Name**: Gwyneth Huynh

## Classes and Data Structures

### Main

#### Description

This is the entry point to our program. 
It takes in arguments from the command line
and based on the command (the first element
of the `args` array) calls the corresponding command
in `Repository` which will actually execute 
the logic of the command. It also validates the arguments
based on the command to ensure that enough arguments
are passed in.

It will also be responsible for setting up all persistence
within gitlet. This includes creating the `.gitlet`
folder as well as the folder and file where we store 
all `Commit` and `Blob` objects. 

This class defers all `Commit` and `Blob` specific 
logic to the `Comit` and `Blob` classes respectively. 
The `Repository` class will not handle `Commit` and `Blob`
serialization and deserialization, but rather their
respective classes will contain logic for that.

#### Fields

This class has no fields and hence no associated state:
it simply validates arguments and defers the execution to 
the `Repository` class.

### Repository

#### Description
This is where the main logic of our program will live. 
This file will handle all of the gitlet commands by 
reading/writing from/to the correct file, setting up
persistence, and additional error checking. 


#### Fields
1. `static final File CWD = new File(System.getProperty("user.dir"))`
The Current Working Directory. Since it has the 
package-private access modifier (i.e. no access modifier),
other classes in the package may use this filed. It 
is useful for the other `File` objects we need to use.


2. `static final File GITLET_FOLDER = Utils.join(CWD, ".gitlet")`
The hidden `.gitlet` directory. This is where all of the
state of the `Repository` will be stored, including
additional things like the `Commit` and `Blob` objects.
It is also package private as other classes will
use it to store their state.


3. `public static void init()` Creates a new Gitlet 
version control system (`.gitlet` directory) in the CWD.
Creates and starts with one initial commit that contains
no files and has the commit message "initial commit".
It will have a single branch `master`, which initially points
to this initial commit. 
   1. **Runtime**: constant
   2. **Failure cases**: if there is already a Gitlet
   version control in the CWD, it should abort. It
   should NOT override existing system with a new one.
   Print error message: `A Gitlet version-control system
   already exists in the current directory.`


4. `public static void add()` Adds a copy of the file as it 
currently exists to the staging area. Staging an already
staged file overwrites the previous entry in the staging
area with the new contents. The staging area should be
somewhere in `.gitlet`. If the current working version
of the file is identical to th e version in the current
commit, do not stage it to be added, and remove it
from the staging area if it is already there. The file
will no longer be staged for removal if it was at the
time of the command.
   1. **Runtime**: In worst case, should run in linear 
   time relative to the size of the file being added
   and ***log N***, for ***N*** the number of files in
   the commit.
   2. **Failure cases**: If the file does not exist, 
   print the error message `File does not exist.` and
   exist without changing anything.


5. `public static void commit()` Creates a new commit
object with files of current commit and staging area.
By default, each commit's snapshot of files will be the
same as the parents' files. If there are files staged
for addition, it will update the contents of those files
at the time of the commit. Files tracked in the current
commit may be untracked in the new commit if it is 
staged for removal by the `rm` command. After a commit
the staging area is cleared. This new commit
becomes the "current" commit, and the `HEAD` pointer
now points to it. The previous head commit is this 
commit's parent commit. 
   1. **Runtime**: constant with respect to any measure
   of number of commits. Must be no worse than linear with
   respect to the total size of files the commit is tracking
   committing must increase the size of the `.gitlet` directory
   by no more than the total size of the files staged
   for additional at the time of commit (don't store)
   redundant copies of versions of files.
   2. **Failure cases**: If no files have been staged,
   abort. Print `No changes added to the commit.` Every
   commit must have a non-blank message. If it doesn't,
   print `Please enter a commit message.` 


6. `public static void rm()` Unstage the file if it is 
currently staged for addition. If the file is tracked in 
current commit, stage it for removal and remove the file
from the CWD if the user has not already done so. (do
not remove it unless it is tracked in the current commit).
   1. **Runtime**: constant time relative to any significant
   measure.
   2. **Failure cases**: If the file is neither staged
   nor tracked by the head commit, print 
   `No reason to remove the file.`
   

7. `public static void log()` Starting at current head
commit, display information about each commit backwards 
along the commit tree until the initial commit. Ignore
any second parents found in merge commits. Prints
commit id, the time of the commit, and the commit
message. For merge commits, add a line just below the first
where the two numerals consist of the first 7 digits
of the first and second parents' commit ids in that order. 
The first parent is the branch you were on when you did
the merge and the second is that of the merged-in branch.
   1. **Runtime**: Linear with respect to number of nodes in
    head's history.
   
8. `public static void globalLog()` prints information about 
all commits every made. The order of commits does not
matter
   1. **Runtime**: Linear with respect to number of 
   commits ever made. 

### Blob

#### Fields

1. `private final String parent` The SHA-1 identifier
of the parent blob. If we are creating a new file,
the parent blob is `null`.


2.'private final String blobID` the SHA-1 identifier
of the current blob. 


3.`public void saveBlob()` 


### Commit

#### Fields

1. `private String message` contains the message 
of this commit.


2. `private String timestamp` the time at 
which this commit was created.


3. `private String parent` the SAH-1 identifier of 
parent commit of this commit object.


4. `(private or public) String[] blobs` A list of the commit's SHA-1
identifiers.


5. `private final String commitID` the SHA-1 
identifier of the current commit object.


6. `public void saveCommit()` 


7. `public void getCommit()`

8. `public void addCommit()`


### My Git

#### Fields

myGitTree myGitTree (getcommit, dfsCommit, addCommit)
treemap myHeads
treemap myBlobs

*logs --> go to HEAD and use previous pointers and print out 


1. Field 1
2. Field 2


## Algorithms

## Persistence

```
├── CWD
     └── .gitlet
             ├── HEAD     <==File containing path to Head
             ├── refs
                   ├── heads
                         ├── branch 1   <==File: contains SH-1 code
                         ├── branch 2
                         └── branch 3
                   ├── tags  <== nothing?
             ├── objects
                    ├── 01
                         ├── 57408da4ea7bfcfb4120502a6fa52f0bc0c8da
                    ├── 06
                    ├── 1d
                    ├── c3
                    └── 5d
                        ├── 897ebe49df358f3f578eb7706197f661eddb10
                        ├── 8b64750c48ce363aa40d6495472dfe897c9172
            ├── logs
                  ├── HEAD  <==File: contains all commit metadata
                  └── refs
                        └── heads
                              ├── branch 1 <==File: all commit metadata
                              ├── branch 2
```

