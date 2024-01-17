package filesys;

import metadata.DirectoryFileMetadata;
import metadata.FileMetadata;
import metadata.HardLinkMetadata;
import metadata.SoftLinkFileMetadata;
import utils.FileSystemUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class FileSystemImpl implements FileSystemInterface {
    private DirectoryFileMetadata rootDir = new DirectoryFileMetadata("", null, FileMetadata.FileType.DIRECTORY);
    private static FileSystemImpl singleInstance = null;

    @Override
    public FileMetadata rootDir() {
        return rootDir;
    }

    @Override
    public FileMetadata touch(FileMetadata currentDir, String filepath) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, filepath, true);
        return tuple.getDir().createChild(tuple.getEntryName(), FileMetadata.FileType.REGULAR_FILE);
    }

    @Override
    public FileMetadata makeDir(FileMetadata currentDir, String dirPath) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, dirPath, true);
        return tuple.getDir().createChild(tuple.getEntryName(), FileMetadata.FileType.DIRECTORY);
    }

    @Override
    public Collection<FileMetadata> listDir(FileMetadata currentDir, String dirPath) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, dirPath, false);
        FileMetadata item = tuple.getDir();
        if (!tuple.getEntryName().isEmpty()) {
            // in case of a dir path of '/', entry name will be empty
            item = tuple.getDir().getChild(tuple.getEntryName());
        }
        return item.listDir();
    }

    @Override
    public void remove(FileMetadata currentDir, String path, boolean isRecursive) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, path, false);
        tuple.getDir().remove(tuple.getEntryName(), isRecursive);
    }

    @Override
    public FileContents get(FileMetadata currentDir, String filePath) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, filePath, false);
        return tuple.getDir().getChild(tuple.getEntryName()).getFileContents();
    }

    @Override
    public void write(FileMetadata currentDir, String filePath, FileContents c) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, filePath, false);
        tuple.getDir().getChild(tuple.getEntryName()).write(c);
    }

    @Override
    public void move(FileMetadata currentDir, String srcPath, String targetPath) {
        FilePathResolver.DirectoryFilenameTuple sourceTuple = FilePathResolver.resolveFilePath(currentDir, srcPath, false);
        if (!sourceTuple.getDir().hasChild(sourceTuple.getEntryName())) {
            throw new RuntimeException(String.format("source %s does not exist", srcPath));
        }
        FilePathResolver.DirectoryFilenameTuple targetTuple = FilePathResolver.resolveFilePath(currentDir, targetPath, true);
        FileMetadata sourceItem = sourceTuple.getDir().getChild(sourceTuple.getEntryName());

        // If the targetPath has a suffix of '/' and target entry is a dir, the source item goes under the target entry
        // Else, the source item goes under targetDir.
        DirectoryFileMetadata targetDir = targetTuple.getDir();

        boolean isTargetDirectory = isDirectory(targetTuple);
        boolean isSourceDirectory = isDirectory(sourceTuple);
        if (isSourceDirectory && !isTargetDirectory) {
            throw new RuntimeException("cannot rename a directory to a regular file");
        }
        if (isTargetDirectory && (!isSourceDirectory || targetPath.endsWith("/"))) {
            targetDir = (DirectoryFileMetadata) targetTuple.getDir().getChild(targetTuple.getEntryName());
        } else {
            // now delete the original target non-dir item if it exists
            targetTuple.getDir().remove(targetTuple.getEntryName(), true);
            sourceItem.resetName(sourceTuple.getEntryName());
        }
        sourceTuple.getDir().remove(sourceTuple.getEntryName(), true);
        targetDir.addChild(sourceItem);
    }

    private boolean isDirectory(FilePathResolver.DirectoryFilenameTuple tuple) {
        return !tuple.getEntryName().isEmpty() &&
                tuple.getDir().hasChild(tuple.getEntryName()) &&
                tuple.getDir().getChild(tuple.getEntryName()).getFileType() == FileMetadata.FileType.DIRECTORY;
    }

    @Override
    public List<FileMetadata> find(FileMetadata currentDir, String fileOrDirName) {
        FileSystemUtils.checkArgument((currentDir != null && currentDir instanceof DirectoryFileMetadata), String.format("%s is either not a dir or not present", currentDir.getFullPath()));
        return ((DirectoryFileMetadata) currentDir).find(fileOrDirName);
    }

    @Override
    public FileMetadata symLink(String pointToPath, String filepath, FileMetadata currentDir) {
        String absolutePathToPointTo = FilePathResolver.getAbsolutePath(currentDir, pointToPath);
        FilePathResolver.DirectoryFilenameTuple sourceTuple = FilePathResolver.resolveFilePath(currentDir, filepath, true);
        return new SoftLinkFileMetadata(absolutePathToPointTo, sourceTuple.getEntryName(), sourceTuple.getDir(), rootDir);
    }

    @Override
    public FileMetadata hardLink(String pointToPath, String filepath, FileMetadata currentDir) {
        FilePathResolver.DirectoryFilenameTuple targetTuple = FilePathResolver.resolveFilePath(currentDir, pointToPath, false);
        FilePathResolver.DirectoryFilenameTuple sourceTuple = FilePathResolver.resolveFilePath(currentDir, filepath, true);
        FileMetadata target = targetTuple.getDir().getChild(targetTuple.getEntryName());
        return new HardLinkMetadata(target.getFullPath(), sourceTuple.getEntryName(), sourceTuple.getDir(), rootDir);
    }

    @Override
    public void walkTree(FileMetadata currentDir, String path, WalkTreeHandler walkTreeHandler) {
        FilePathResolver.DirectoryFilenameTuple tuple = FilePathResolver.resolveFilePath(currentDir, path, false);
        FileMetadata item = tuple.getDir();
        if (!tuple.getEntryName().isEmpty()) {
            item = tuple.getDir().getChild(tuple.getEntryName());
        }
        FileSystemUtils.checkArgument(item.getFileType().equals(FileMetadata.FileType.DIRECTORY), String.format("Cannot walk tree on a non-directory item %s", path));
        ((DirectoryFileMetadata) item).walkTree(walkTreeHandler);
    }

    private void dfs(FileMetadata fileMetadata, WalkTreeHandler walkTreeHandler, Set<FileMetadata> visited) {
        if (visited.contains(fileMetadata)) {
            return;
        }
        walkTreeHandler.handle(fileMetadata);

        for (FileMetadata f : ((DirectoryFileMetadata) fileMetadata).getChildrenMap().values()) {
            walkTreeHandler.handle(f);
        }
    }

    private FileSystemImpl() {
    }

    public static synchronized FileSystemImpl getInstance() {
        if (singleInstance == null)
            singleInstance = new FileSystemImpl();

        return singleInstance;
    }

    public DirectoryFileMetadata getRootDir() {
        return rootDir;
    }
}
