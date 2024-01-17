package metadata;

import filesys.FileContents;
import lombok.Getter;

@Getter
public class RegularFileMetadata extends FileMetadata {
    private FileContents fileContents;

    public RegularFileMetadata(String filename, FileMetadata parent, FileType fileType) {
        super(filename, parent, fileType);
        fileContents = new FileContents();
    }

    @Override
    public void write(FileContents c) {
        fileContents = c;
    }

    @Override
    public boolean isDir() {
        return false;
    }
}
