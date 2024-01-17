package metadata;

import filesys.FileContents;
import filesys.WalkTreeHandler;
import lombok.Getter;

import java.util.*;

@Getter
public class DirectoryFileMetadata extends FileMetadata {
    private Map<String, FileMetadata> childrenMap = new HashMap<>();

    public DirectoryFileMetadata(String filename, FileMetadata parent, FileType fileType) {
        super(filename, parent, fileType);
    }

    @Override
    public void write(FileContents c) {
        throw new RuntimeException(String.format("%s is a dir and not a file so cannot write file contents to it", getName()));
    }

    @Override
    public FileMetadata createChild(String path, FileType fileType) {
        FileMetadata child = null;

        if (fileType.equals(FileType.REGULAR_FILE)) {
            child = new RegularFileMetadata(path, this, fileType);
        } else if (fileType.equals(FileType.DIRECTORY)) {
            child = new DirectoryFileMetadata(path, this, fileType);
        } else {
            throw new RuntimeException("can only create a child that is regular file or dir");
        }
        childrenMap.put(path, child);
        return child;
    }

    @Override
    public boolean hasChild(String path) {
        return childrenMap.containsKey(path);
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public FileContents getFileContents() {
        throw new RuntimeException(String.format("%s is a directory and not a file to list contents", getFullPath()));
    }

    @Override
    public Collection<FileMetadata> listDir() {
        return childrenMap.values();
    }

    public FileMetadata getChild(String childPath) {
        if (!hasChild(childPath)) {
            throw new RuntimeException(String.format("Child %s is not found in directory %s", childPath, getFullPath()));
        }
        return childrenMap.get(childPath);
    }

    public void remove(String fileOrDirName, boolean isRecursive) {
        if (childrenMap.containsKey(fileOrDirName)) {
            if (!isRecursive && childrenMap.get(fileOrDirName).getFileType() == FileType.DIRECTORY) {
                throw new RuntimeException("Deletion cannot be done on a directory without recursive option enabled");
            }
        }
        FileMetadata childRemoved = childrenMap.remove(fileOrDirName);
        if (childRemoved != null) {
            childRemoved.eraseParent();
        }
    }

    public List<FileMetadata> find(String fileOrDirName) {
        List<FileMetadata> res = new ArrayList<>();
        Queue<FileMetadata> bfsQueue = new LinkedList<>();
        bfsQueue.add(this);
        Set<FileMetadata> visited = new HashSet<>();
        while (!bfsQueue.isEmpty()) {
            FileMetadata item = bfsQueue.poll();
            if (visited.contains(item)) {
                continue;
            }
            visited.add(item);
            if (item.getName().equals(fileOrDirName)) {
                res.add(item);
            }
            if (item.getFileType() == FileType.DIRECTORY) {
                // search children as well
                for (FileMetadata child : ((DirectoryFileMetadata) item).getChildrenMap().values()) {
                    bfsQueue.add(child);
                }
            }
        }
        return res;
    }

    public void addChild(FileMetadata fileMetadata) {
        fileMetadata.updateParent(this);
        childrenMap.put(fileMetadata.getName(), fileMetadata);
    }

    public void walkTree(WalkTreeHandler walkTreeHandler) {
        Queue<FileMetadata> bfsQueue = new LinkedList<>();
        bfsQueue.add(this);
        Set<FileMetadata> visited = new HashSet<>();
        while (!bfsQueue.isEmpty() && !walkTreeHandler.shouldStopWalk()) {
            FileMetadata item = bfsQueue.poll();
            if (visited.contains(item)) {
                continue;
            }
            walkTreeHandler.handle(item);
            visited.add(item);
            if (item.getFileType() == FileType.DIRECTORY) {
                // search children as well
                for (FileMetadata child : ((DirectoryFileMetadata) item).getChildrenMap().values()) {
                    bfsQueue.add(child);
                }
            }
        }
    }
}

