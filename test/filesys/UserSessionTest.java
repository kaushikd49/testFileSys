package filesys;

import metadata.FileMetadata;
import org.junit.Assert;
import org.junit.Test;

public class UserSessionTest {

    @Test
    public void testUserSession() {
        FileSystemImpl filesystem = FileSystemImpl.getInstance();
        UserSession session = new UserSession(filesystem);

        Assert.assertNull(session.presentWorkingDir().getParent());
        Assert.assertEquals("/", session.presentWorkingDir().getFullPath());
        prettyPrintLs(session, "");

        session.makeDir("foo");
        session.makeDir("roo/poo");
        prettyPrintLs(session, "");

        session.remove("/roo", true);
        prettyPrintLs(session, "");

        session.changeDir("foo");
        Assert.assertEquals(FileMetadata.FileType.REGULAR_FILE, session.touch("bar").getFileType());
        session.symLink("/foo/bar", "quox");
        session.symLink("car", "duox");
        prettyPrintLs(session, "");

        session.remove("duox", false);
        prettyPrintLs(session, "");
        Assert.assertEquals(FileMetadata.FileType.HARD_LINK, session.hardLink("bar", "euox").getFileType());
        prettyPrintLs(session, "");

        session.write("bar", new FileContents().setContents("hello"));
        System.out.println(session.get("bar").getContents());
        assertException(() -> session.get("quox").getContents(), "//foo/bar is a directory and not a file to list contents");


        session.hardLink("bar", "hardl");
        session.remove("bar", false);
        System.out.println(session.get("hardl").getContents());
//        System.out.println(session.get("quox").getContents());

        session.symLink("/foo/car", "euox");
        session.symLink("/", "/dog");
        session.changeDir("/dog");
        prettyPrintLs(session, "");

        session.changeDir("/dog/foo");
        prettyPrintLs(session, "");

        prettyPrintLs(session, "/");
        prettyPrintLs(session, "/foo");
        prettyPrintLs(session, "/dog");

        session.remove("/dog", false);
//        System.out.println("After..");
        prettyPrintLs(session, "/");

        session.touch("/foo/hoo/too");
        session.touch("/foo/loo/too");
        session.touch("/too");
        prettyPrintLs(session, "/foo/hoo");
        prettyPrintLs(session, "/foo/loo");

        session.move("/foo/loo/too", "/foo/boo/zoo");
        assertException(() -> session.get("/foo/loo/too").getContents(), "Child too is not found in directory /foo/loo");
        prettyPrintLs(session, "/foo/");
        prettyPrintLs(session, "/foo/boo");


        System.out.println("Find output");
        for (FileMetadata f : session.find("too")) {
            System.out.println(f);
        }

        session.changeDir("/");
        System.out.println("Find output");
        for (FileMetadata f : session.find("too")) {
            System.out.println(f);
        }

        System.out.println("Doing walk tree");
        session.walkTree("/", getWalkTreeHandler("too"));
        System.out.println("Doing walk tree");
        session.walkTree("/foo", getWalkTreeHandler("too"));
    }

    public static void assertException(Runnable run, String msg) {
        try {
            run.run();
        } catch (RuntimeException e) {
            Assert.assertEquals(msg, e.getMessage());
        }
    }

    private WalkTreeHandler getWalkTreeHandler(String findFirst) {
        WalkTreeHandler walkTreeHandler = new WalkTreeHandler() {
            boolean found;

            @Override
            public void handle(FileMetadata fileMetadata) {
                if (fileMetadata.getName().equals(findFirst)) {
                    System.out.println(String.format("found first occurance of %s at %s", findFirst, fileMetadata.getFullPath()));
                    found = true;
                }
            }

            @Override
            public boolean shouldStopWalk() {
                return found;
            }
        };
        return walkTreeHandler;
    }

    private void prettyPrintLs(UserSession session, String dirPath) {
        System.out.println("** ls **");
        for (FileMetadata f : session.listDir(dirPath)) {
            System.out.println(f.toString());
        }
        System.out.println();
    }
}
