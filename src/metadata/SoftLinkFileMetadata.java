package metadata;

import filesys.FileContents;
import lombok.Getter;
import utils.FileSystemUtils;

import java.util.Collection;

@Getter
public class SoftLinkFileMetadata extends FileMetadata {
    private FileMetadata rootDir;
    private String pointsToPath;

    public SoftLinkFileMetadata(String pointsToPath, String filename, FileMetadata parentDir, FileMetadata rootDir) {
        super(filename, parentDir, FileType.SOFT_LINK);
        FileSystemUtils.checkArgument(!pointsToPath.isEmpty(), "pointsToPath cannot be empty");
        this.pointsToPath = pointsToPath;
        this.rootDir = rootDir;
        ((DirectoryFileMetadata) parentDir).addChild(this);
    }

    @Override
    public void write(FileContents c) {
        FileMetadata target = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        if (target != null) {
            target.write(c);
        } else {
            throw new RuntimeException(String.format("target file %s not found", pointsToPath));
        }
    }

    @Override
    public FileMetadata createChild(String path, FileType fileType) {
        FileMetadata pointingTo = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        return pointingTo.createChild(path, fileType);
    }

    @Override
    public FileContents getFileContents() {
        FileMetadata target = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        if (target != null) {
            return target.getFileContents();
        }
        throw new RuntimeException(String.format("target file %s not found", pointsToPath));
    }

    public FileMetadata getChild(String path) {
        FileMetadata pointingTo = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        return pointingTo.getChild(path);
    }

    public boolean hasChild(String path) {
        FileMetadata pointingTo = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        return pointingTo.hasChild(path);
    }

    @Override
    public boolean isDir() {
        return FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath).isDir();
    }

    public Collection<FileMetadata> listDir() {
        FileMetadata pointingTo = FileSystemUtils.navigateToPointedEntryFromRoot(rootDir, pointsToPath);
        return pointingTo.listDir();
    }
}
