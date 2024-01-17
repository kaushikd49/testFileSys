package metadata;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileParams {
    private String path;
    private FileMetadata.FileType fileType;
    private String pointsToPath;
    private byte[] access = new byte[3]; // to model r-w-x permissions
}
