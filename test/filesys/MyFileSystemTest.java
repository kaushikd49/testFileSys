package filesys;

import org.junit.Assert;
import org.junit.Test;

public class MyFileSystemTest {
    @Test
    public void testArgSplit_doubleQuoteIsHandled() {
        String[] args = MyFileSystem.splitLine("-write abc \"degf my name is anthony gonsalves... mein dunamein \"heee\" hoon ;b 'a to door numner 420...\"");
        Assert.assertEquals(3, args.length);
        Assert.assertEquals("-write", args[0]);
        Assert.assertEquals("abc", args[1]);
        Assert.assertEquals("degf my name is anthony gonsalves... mein dunamein \"heee\" hoon ;b 'a to door numner 420...", args[2]);
    }

    @Test
    public void testArgSplit() {
        String[] args = MyFileSystem.splitLine("-cat hello");
        Assert.assertEquals(2, args.length);
        Assert.assertEquals("-cat", args[0]);
        Assert.assertEquals("hello", args[1]);
    }
}
