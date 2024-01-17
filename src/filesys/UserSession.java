package filesys;

import metadata.DirectoryFileMetadata;
import metadata.FileMetadata;
import utils.FileSystemUtils;

import java.util.Collection;
import java.util.List;

public class UserSession {
    private final FileSystemImpl fileSystem;
    private final DirectoryFileMetadata rootDir;
    private FileMetadata currentDir;
    private String username;

    public UserSession(FileSystemImpl fileSystem) {
        this.rootDir = fileSystem.getRootDir();
        this.currentDir = rootDir;
        this.fileSystem = fileSystem;
    }

    public void changeDir(String path) {
        FileMetadata fileMetadata = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, path);
        if (fileMetadata.isDir()) {
            this.currentDir = fileMetadata;
        } else {
            throw new RuntimeException(String.format("%s not a directory", fileMetadata.getFullPath()));
        }
    }

    public metadata.FileMetadata presentWorkingDir() {
        return currentDir;
    }

    public FileMetadata touch(String filename) {
        return fileSystem.touch(currentDir, filename);
    }

    public FileMetadata makeDir(String dirName) {
        return fileSystem.makeDir(currentDir, dirName);
    }

    public Collection<FileMetadata> listDir(String dirPath) {
        if (dirPath.isEmpty()) {
            return currentDir.listDir();
        }
        return fileSystem.listDir(currentDir, dirPath);
    }

    public void remove(String fileOrDirName, boolean isRecursive) {
        fileSystem.remove(currentDir, fileOrDirName, isRecursive);
    }

    public FileContents get(String filename) {
        return fileSystem.get(currentDir, filename);
    }

    public void write(String filename, FileContents c) {
        fileSystem.write(currentDir, filename, c);
    }

    public void move(String path1, String path2) {
        fileSystem.move(currentDir, path1, path2);
    }

    public List<FileMetadata> find(String fileOrDirName) {
        return fileSystem.find(currentDir, fileOrDirName);
    }

    public FileMetadata symLink(String pointToPath, String filename) {
        return fileSystem.symLink(pointToPath, filename, currentDir);
    }

    public FileMetadata hardLink(String pointToPath, String filename) {
        return fileSystem.hardLink(pointToPath, filename, currentDir);
    }

    public void walkTree(String path, WalkTreeHandler walkTreeHandler) {
        fileSystem.walkTree(currentDir, path, walkTreeHandler);
    }
}
