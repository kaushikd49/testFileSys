package filesys;

import org.apache.commons.cli.*;

import java.util.Scanner;

public class MyFileSystem {

    public static void main(String[] args) {

        FileSystemImpl fileSystem = FileSystemImpl.getInstance();
        UserSession session = new UserSession(fileSystem);
        UserSessionPrintWrapper wrapper = new UserSessionPrintWrapper(session);
        Options options = getOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("myFileSystem", options);
        CommandLineParser parser = new DefaultParser();

        boolean keepRunning = true;
        while (keepRunning) {
            System.out.print("kshell> ");
            Scanner scanner = new Scanner(System.in);
            String nextLine = scanner.nextLine();
            try {
                String[] shellArgs = splitLine(nextLine);
                CommandLine opt = parser.parse(options, shellArgs);
                if (shellArgs.length > 0) {
                    String operation = shellArgs[0];
                    String optionValues[] = opt.getOptionValues(operation);
                    switch (operation) {
                        case "-cd":
                            wrapper.changeDir(optionValues[0]);
                            break;
                        case "-pwd":
                            wrapper.presentWorkingDir();
                            break;
                        case "-touch":
                            wrapper.touch(optionValues[0]);
                            break;
                        case "-mkdir":
                            wrapper.makeDir(optionValues[0]);
                            break;
                        case "-ls":
                            wrapper.listDir(optionValues);
                            break;
                        case "-rm":
                            wrapper.remove(optionValues[optionValues.length - 1], optionValues.length > 1 && optionValues[0].equals("-r"));
                            break;
                        case "-write":
                            wrapper.write(optionValues[0], optionValues[1]);
                            break;
                        case "-cat":
                            wrapper.get(optionValues[0]);
                            break;
                        case "-mv":
                            wrapper.move(optionValues[0], optionValues[1]);
                            break;
                        case "-find":
                            wrapper.find(optionValues[0]);
                            break;
                        case "-ln":
                            switch (optionValues[0]) {
                                case "-s":
                                    wrapper.symLink(optionValues[1], optionValues[2]);
                                    break;
                                case "-h":
                                    wrapper.hardLink(optionValues[1], optionValues[2]);
                                    break;
                            }
                            break;
                        case "-walktree":
                            wrapper.walkTree(optionValues[0], optionValues[1]);
                            break;
                        case "-quit":
                            keepRunning = false;
                            break;
                    }
                }
            } catch (ParseException e) {
                formatter.printHelp("myFileSystem", options);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("cd", true, "Change current working directory");
        options.addOption("pwd", false, "Display present working directory");
        options.addOption("touch", true, "Create new file");
        options.addOption("mkdir", true, "Create new directory");
        options.addOption(Option.builder().option("ls").optionalArg(true).desc("Display entries of a directory, by default current directory").build());
        options.addOption(Option.builder().option("rm").hasArgs().desc("Remove a file. For directory recursively, use -r in addition").build());
        options.addOption(Option.builder().option("write").hasArgs().numberOfArgs(2).valueSeparator('\f').desc("Write contents to a file").build());
        options.addOption("cat", true, "Display contents of a file");
        options.addOption(Option.builder().option("mv").hasArgs().numberOfArgs(2).desc("Move file/directory from one place to another").build());
        options.addOption("find", true, "Find a file/directory within current working directory tree");
        options.addOption(Option.builder().option("ln").hasArgs().numberOfArgs(3).desc("Sym/hard linking a file. <src> <dest>. Use ln -s for soft link and ln -h for hard link").build());
        options.addOption(Option.builder().option("walktree").hasArgs().desc("Walk a tree and find the first appearing pattern").build());
        options.addOption("quit", false, "Quit the shell");
        return options;
    }

    /**
     * Splits the input line into words based on space, but before that, treates quoted words are treated as one string.
     * Asssumes that there is only one sequence of quoted words at max. This is to support quoted words in case of the
     * write api, wherein input can contain spaces.
     */
    public static String[] splitLine(String line) {
        // Split based on param boundary. In case of write command, one of the arguments can be quoted.
        // So handle accordingly. There can be escaped quotes as well inside
        if (line.contains("\"")) {
            int start = line.indexOf('"');
            int end = line.lastIndexOf('"');
            String[] a = line.substring(0, start).split(" ");
            String b = line.substring(start + 1, end);
            String[] res = new String[a.length + 1];
            int i = 0;
            for (; i < a.length; i++) {
                res[i] = a[i];
            }
            res[i] = b;
            return res;
        }
        return line.split(" ");
    }
}
