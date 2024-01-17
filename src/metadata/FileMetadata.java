package metadata;

import filesys.FileContents;
import org.apache.commons.io.FilenameUtils;

import java.util.Collection;
import java.util.Date;

public abstract class FileMetadata {
    private String name;
    private String fullPath = "/"; // will be overridden if not a root dir
    private FileMetadata parent;
    private Date createdAt;
    private Date updatedAt;
    private DirectoryFileMetadata.FileType fileType;
    private byte[] access = new byte[3]; // to model r-w-x permissions

    public FileMetadata(String filename, FileMetadata parent, FileType fileType) {
        this.name = filename;
        updateParent(parent);
        this.fileType = fileType;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        // ignore access for now
    }

    public void eraseParent() {
        parent = null;
    }

    public abstract void write(FileContents c);

    public void updateParent(FileMetadata parent) {
        if (parent != null) {
            this.fullPath = FilenameUtils.concat(parent.getFullPath(), name);
        }
        this.parent = parent;
    }

    public void resetName(String entryName) {
        this.name = entryName;
    }

    public static enum FileType {
        DIRECTORY("d:"), REGULAR_FILE("r:"), SOFT_LINK("s:"), HARD_LINK("h:");

        private final String shortName;

        FileType(String s) {
            shortName = s;
        }
    }

    public FileMetadata createChild(String path, FileType fileType) {
        throw new RuntimeException(String.format("Entity %s is not a directory, cannot create a child entry", getFullPath()));
    }

    public FileMetadata getChild(String path) {
        throw new RuntimeException(String.format("Entity %s is not a directory, cannot have a child entry", getFullPath()));
    }

    public boolean hasChild(String path) {
        throw new RuntimeException(String.format("Entity %s is not a directory, cannot have a child entry", getFullPath()));
    }

    public abstract boolean isDir();

    public Collection<FileMetadata> listDir() {
        throw new RuntimeException(String.format("Entity %s is not a directory, so cannot be listed", getFullPath()));
    }

    public abstract FileContents getFileContents();

    public String getName() {
        return name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public FileMetadata getParent() {
        return parent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public FileType getFileType() {
        return fileType;
    }

    public byte[] getAccess() {
        return access;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fileType.shortName + " " + name + " " + fullPath);
        return sb.toString();
    }
}
