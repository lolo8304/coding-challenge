package forth;

import java.io.File;
import java.io.FileReader;
import java.util.NoSuchElementException;

public class CmdLline {
    final ForthInterpreter forth;
    public CmdLline() {
        this.forth = new ForthInterpreter();
    }

    public void run() {
        do {
            System.out.print(this.forth.stackToString());
            System.out.print("ok> ");
            String line = System.console().readLine();
            if (line == null || line.equals("bye") || line.equals("^D")) {
                break;
            }
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                this.run(line);
            } catch (NoSuchElementException e) {
                System.out.println("Error: stack empty");
            } catch (Error e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (true);
    }

    public void run(String cmd) {
        if (Repl.verbose()) {
            System.out.println("Executing command: " + cmd);
        }
        var lines = cmd.split("\n");
        for (var line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            this.forth.run(line);
        }
        var output = this.forth.outputToPrint();
        if (!output.isEmpty()) System.out.print(output);
    }

    public void run(File file) {
        if (Repl.verbose()) {
            System.out.println("Executing file: " + file.getAbsolutePath());
        }
        try (var reader = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            this.run(sb.toString());
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
