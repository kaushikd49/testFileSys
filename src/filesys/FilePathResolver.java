package filesys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import metadata.DirectoryFileMetadata;
import metadata.FileMetadata;
import org.apache.commons.io.FilenameUtils;
import utils.FileSystemUtils;

public class FilePathResolver {
    /**
     * Resolves the filepath and returns the parent dir and the file name corresponding
     * to the filePath.
     * <p>
     * The filepath could correspond to one where intermediate dirs are not present
     * If in create or move of files mode it should create the intermediate dirs else error out.
     * This is controlled by the argument createIntermediateDirsIfMissing.
     * <p>
     * Cases: ../, ., ./a, ./a/b
     *
     * @return Parent FileMetadata
     */
    public static DirectoryFilenameTuple resolveFilePath(FileMetadata currDir, String filepath, boolean createIntermediateDirsIfMissing) {
//        filepath = FilenameUtils.normalize(filepath);
        DirectoryFilenameTupleStr dirAndEntryStrs = getDirAndEntryStrs(filepath);
        String dirPath = dirAndEntryStrs.getDir();
        String filename = dirAndEntryStrs.getEntryName();

        FileMetadata tmp = currDir;
        if (filepath.startsWith("/")) {
            while (tmp.getParent() != null) {
                tmp = tmp.getParent();
            }
        }

        // The last entry in the path is the file or dir name, so do not end up creating it
        // as that will be handled by the appropriate filesystem API. Hence, we are iterating till split.length - 1.
        String[] split = dirPath.split("/");
        for (int i = 0; i < split.length; i++) {
            String p = split[i];
            if (p.isEmpty() || p.equals(".")) {
                // implies first/last char is '/' or a '.' encountered
                continue;
            }
            if (p.equals("..")) {
                tmp = tmp.getParent();
            } else if (tmp.hasChild(p)) {
                tmp = tmp.getChild(p);
            } else if (createIntermediateDirsIfMissing) {
                tmp = tmp.createChild(p, FileMetadata.FileType.DIRECTORY);
            } else {
                throw new RuntimeException(String.format("Not a valid path %s", filepath));
            }
            FileSystemUtils.checkArgument(tmp != null, String.format("Not a valid path %s", filepath));
        }
        FileSystemUtils.checkArgument(tmp != null, String.format("Not a valid path %s", filepath));
        FileSystemUtils.checkArgument(tmp.getFileType().equals(FileMetadata.FileType.DIRECTORY), String.format("Not a valid dir %s", tmp.getName()));
        return new DirectoryFilenameTuple((DirectoryFileMetadata) tmp, filename);
    }

    /**
     * Returns the absolute full path
     *
     * @param curDir
     * @param path
     * @return
     */
    static String getAbsolutePath(FileMetadata curDir, String path) {
        String filepath = path;
        if (!path.startsWith("/")) {
            // then it is a relative path
            filepath = FilenameUtils.concat(curDir.getFullPath(), path);
        }
        DirectoryFilenameTupleStr normalizedDirFilename = getDirAndEntryStrs(filepath);
        String res = FilenameUtils.concat(normalizedDirFilename.getDir(), normalizedDirFilename.getEntryName());
        if (filepath.startsWith("/")) {
            return res;
        }
        // else, use current dir to determine the absolute path
        return FilenameUtils.concat(curDir.getFullPath(), res);
    }

    /**
     * Preserves the relative path, so that the caller can take advantage of its current dir position to minimize the traversal
     *
     * @param filepath
     * @return
     */
    static DirectoryFilenameTupleStr getDirAndEntryStrs(String filepath) {
        filepath = FilenameUtils.normalize(filepath);
        if (filepath == null) {
            throw new RuntimeException("Improper filepath");
        }
        String dirPath = FilenameUtils.getFullPath(filepath);
        if (filepath.endsWith("/")) {
            filepath = FilenameUtils.getFullPathNoEndSeparator(filepath);
            dirPath = FilenameUtils.getFullPath(filepath);
        }
        String filename = FilenameUtils.getName(filepath);

        return new DirectoryFilenameTupleStr(dirPath, filename);
    }

    private static String pathEscape(String filepath) {
        String escaped = filepath.replaceAll("/+", "/");
        if (escaped.equals("/")) {
            // root dir
            return escaped;
        }
        // escape trailing '/'
        return escaped.replaceAll("/$", "");
    }


    @AllArgsConstructor
    @Getter
    public static class DirectoryFilenameTuple {
        private DirectoryFileMetadata dir;
        private String entryName;
    }

    @AllArgsConstructor
    @Getter
    public static class DirectoryFilenameTupleStr {
        private String dir;
        private String entryName;
    }
}
