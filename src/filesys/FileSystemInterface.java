package filesys;

import metadata.FileMetadata;

import java.util.Collection;
import java.util.List;

public interface FileSystemInterface {
//    public void changeDir(String path);
//    public metadata.FileMetadata presentWorkingDir(String path);

    public FileMetadata rootDir();

    public FileMetadata touch(FileMetadata currentDir, String filepath);

    public FileMetadata makeDir(FileMetadata currentDir, String dirPath);

    public Collection<FileMetadata> listDir(FileMetadata currentDir, String dirPath);

    public void remove(FileMetadata currentDir, String path, boolean isRecursive);

    public FileContents get(FileMetadata currentDir, String filePath);

    public void write(FileMetadata currentDir, String filePath, FileContents c);

    public void move(FileMetadata currentDir, String path1, String path2);

    public List<FileMetadata> find(FileMetadata currentDir, String fileOrDirName);

    public FileMetadata symLink(String pointToPath, String filepath, FileMetadata currentDir);

    public FileMetadata hardLink(String pointToPath, String filepath, FileMetadata currentDir);

    public void walkTree(FileMetadata currentDir, String path, WalkTreeHandler walkTreeHandler);
}
