package filesys;

import lombok.AllArgsConstructor;
import metadata.FileMetadata;

@AllArgsConstructor
public class UserSessionPrintWrapper {
    private UserSession userSession;


    public void changeDir(String path) {
        userSession.changeDir(path);
    }

    public void presentWorkingDir() {
        System.out.println(userSession.presentWorkingDir());
    }

    public void touch(String filename) {
        userSession.touch(filename);
    }

    public void makeDir(String dirName) {
        userSession.makeDir(dirName);
    }

    public void listDir(String[] arg) {
        String dirPath = "";
        if (arg != null && arg.length > 0) {
            dirPath = arg[0];
        }
        for (FileMetadata f : userSession.listDir(dirPath)) {
            System.out.println(f);
        }
    }

    public void remove(String fileOrDirName, boolean isRecursive) {
        userSession.remove(fileOrDirName, isRecursive);
    }

    public void get(String filename) {
        System.out.println(userSession.get(filename));
    }

    public void write(String filename, String contents) {
        userSession.write(filename, new FileContents().setContents(contents));
    }

    public void move(String path1, String path2) {
        userSession.move(path1, path2);
    }

    public void find(String fileOrDirName) {
        for (FileMetadata f : userSession.find(fileOrDirName)) {
            System.out.println(f);
        }
    }

    public void symLink(String pointToPath, String filename) {
        userSession.symLink(pointToPath, filename);
    }

    public void hardLink(String pointToPath, String filename) {
        userSession.hardLink(pointToPath, filename);
    }

    public void walkTree(String path, String pattern) {
        WalkTreeHandler walkTreeHandler = new WalkTreeHandler() {
            boolean found;

            @Override
            public void handle(FileMetadata fileMetadata) {
                if (fileMetadata.getName().equals(pattern)) {
                    System.out.println(String.format("found first occurrence of %s at %s", pattern, fileMetadata.getFullPath()));
                    found = true;
                }
            }

            @Override
            public boolean shouldStopWalk() {
                return found;
            }
        };
        userSession.walkTree(path, walkTreeHandler);
    }
}
