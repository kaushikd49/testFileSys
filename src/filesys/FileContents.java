package filesys;

public class FileContents {
    private String contents;

    public void set(FileContents c) {
        this.contents = c.getContents();
    }

    public String getContents() {
        return contents;
    }

    public FileContents setContents(String c) {
        contents = c;
        return this;
    }

    public String toString() {
        return contents;
    }
}
