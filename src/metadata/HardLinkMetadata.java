package metadata;

import filesys.FileContents;
import utils.FileSystemUtils;

public class HardLinkMetadata extends FileMetadata {
    private FileContents fileContents;

    public HardLinkMetadata(String filename, FileMetadata parent, FileType fileType) {
        super(filename, parent, fileType);
    }

    @Override
    public void write(FileContents c) {
        fileContents.set(c);
    }

    @Override
    public boolean isDir() {
        return false;
    }

    @Override
    public FileContents getFileContents() {
        return fileContents;
    }

    public HardLinkMetadata(String pointsToPath, String filename, FileMetadata parentDir, FileMetadata rootDir) {
        super(filename, parentDir, FileType.HARD_LINK);
        FileSystemUtils.checkArgument(!pointsToPath.isEmpty(), "pointsToPath cannot be empty");
        FileMetadata pointsTo = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        fileContents = pointsTo.getFileContents();
        ((DirectoryFileMetadata) parentDir).addChild(this);
    }
}
