package forth;



import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "forth-repl", mixinStandardHelpOptions = true, version = "forth-repl 1.0", description = "This challenge is to build your own Forth interpreter")
public class Repl implements Callable<Result> {

    public static int _verbose = 0;

    @Option(names = "-v", description = "verbose model level 1")
    boolean verbose = false;
    @Option(names = "-vv", description = "verbose model level 2")
    boolean verbose2 = false;

    @Option(names = {"-c"}, description = "command to execute - default: interactive mode")
    String command;

    @Option(names = {"-f"}, description = "file to execute - default: interactive mode")
    String file;

    @Option(names = {"-l"}, description = "file to load - default: none")
    String fileToLoad;

    public static void main(String[] args) {
        var repl = new Repl();
        var cmd = new CommandLine(repl);
        var exitCode = cmd.execute(args);
        Result result = cmd.getExecutionResult();
        if (result != null && result.toString() != null) {
            System.exit(exitCode);
        }
    }

    public static boolean verbose() {
        return _verbose >= 1;
    }

    @SuppressWarnings("unused")
    public static boolean verbose2() {
        return _verbose >= 2;
    }

    @Override
    public Result call() throws IOException {
        if (this.verbose) _verbose = 1;
        if (this.verbose2) _verbose = 2;
        if (this.file != null && this.command != null) {
            throw new IllegalArgumentException("You cannot specify both -f and -c options");
        }
        var cmdLine = new CmdLline();
        if (this.command != null) {
            cmdLine.run(this.command);
            return new Result();
        }
        if (this.file != null) {
            cmdLine.run(new File(this.file));
            return new Result();
        }
        if (this.fileToLoad != null) {
            cmdLine.run(new File(this.fileToLoad));
        }
        cmdLine.run();
        return new Result();
    }

}