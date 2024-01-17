package utils;

import lombok.AllArgsConstructor;
import lombok.Setter;
import metadata.DirectoryFileMetadata;
import metadata.FileMetadata;

public class FileSystemUtils {
    public static void checkArgument(boolean result, String msg) {
        if (!result) {
            throw new RuntimeException(msg);
        }
    }

    public static FileMetadata navigateToPointedEntryFromRoot(FileMetadata rootDir, String pointsToPath) {
        FileMetadata tmp = rootDir;
        for (String p : pointsToPath.split("/")) {
            if (p.isEmpty()) {
                continue;
            }
            tmp = tmp.getChild(p);
            FileSystemUtils.checkArgument(tmp != null, String.format("%s does not exist", pointsToPath));
        }
        return tmp;
    }

    public static FileMetadata getParent(String path, FileMetadata rootDir) {
        String parentPath = path;
        if (path.contains("/")) {
            parentPath = path.substring(0, path.lastIndexOf("/"));
        }
        FileMetadata parent = navigateToPointedEntryFromRoot(rootDir, parentPath);
        return parent;
    }


}
