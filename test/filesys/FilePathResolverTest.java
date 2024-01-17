package filesys;

import metadata.DirectoryFileMetadata;
import metadata.FileMetadata;
import org.junit.Assert;
import org.junit.Test;

public class FilePathResolverTest {

    @Test
    public void testGetAbsolutePath() {
        DirectoryFileMetadata root = new DirectoryFileMetadata("", null, FileMetadata.FileType.DIRECTORY);
        String b = FilePathResolver.getAbsolutePath(root, "b");
        Assert.assertEquals("/b", b);

        String c = FilePathResolver.getAbsolutePath(root, "./c");
        Assert.assertEquals("/c", c);

        UserSessionTest.assertException(() -> FilePathResolver.getAbsolutePath(root, "../d"), "Improper filepath");

        DirectoryFileMetadata d = new DirectoryFileMetadata("d", root, FileMetadata.FileType.DIRECTORY);
        String e = FilePathResolver.getAbsolutePath(d, "./e");
        Assert.assertEquals("/d/e", e);

        String f = FilePathResolver.getAbsolutePath(d, "../f");
        Assert.assertEquals("/f", f);
    }

    @Test
    public void testDirEntryResolution() {
        FilePathResolver.DirectoryFilenameTupleStr res = FilePathResolver.getDirAndEntryStrs("/a/b");
        Assert.assertEquals("/a/", res.getDir());
        Assert.assertEquals("b", res.getEntryName());

        res = FilePathResolver.getDirAndEntryStrs("/a");
        Assert.assertEquals("/", res.getDir());
        Assert.assertEquals("a", res.getEntryName());

        res = FilePathResolver.getDirAndEntryStrs("/a/../b");
        Assert.assertEquals("/", res.getDir());
        Assert.assertEquals("b", res.getEntryName());

        res = FilePathResolver.getDirAndEntryStrs("./b");
        Assert.assertEquals("", res.getDir());
        Assert.assertEquals("b", res.getEntryName());

        res = FilePathResolver.getDirAndEntryStrs("/a/./b");
        Assert.assertEquals("/a/", res.getDir());
        Assert.assertEquals("b", res.getEntryName());

        res = FilePathResolver.getDirAndEntryStrs("/a/b/c/d/e/././././../../g");
        Assert.assertEquals("/a/b/c/", res.getDir());
        Assert.assertEquals("g", res.getEntryName());

    }
}
