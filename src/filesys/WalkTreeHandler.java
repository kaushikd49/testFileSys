package filesys;

import metadata.FileMetadata;

public interface WalkTreeHandler {
    public void handle(FileMetadata fileMetadata);

    public boolean shouldStopWalk();
}
