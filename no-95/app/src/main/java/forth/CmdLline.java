package forth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CmdLline {
    final ForthInterpreter forth;
    public CmdLline() {
        this.forth = new ForthInterpreter();
    }

    private String readAll() throws IOException {
        var in = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        var sb = new StringBuilder();

        int ch = in.read();
        sb.append((char) ch);
        while (in.ready() && (ch = in.read()) != -1) { // -1 = EOF (^D on Unix, ^Z on Windows)
            sb.append((char) ch);
        }
        return sb.toString().trim();
    }

    public void run() throws IOException {
        System.out.println("Welcome to the Forth interpreter!");
        do {
            System.out.print(this.forth.stackToString());
            System.out.print("ok> ");
            var line = this.readAll();
            if (line.equals("bye") || line.equals("^D")) {
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

    private boolean isValidCommand(String line) {
        if (line == null) {
            return false;
        }
        if (line.isEmpty()) {
            return false;
        }
        if (line.startsWith("\\")) {
            return false;
        }
        if (line.length() == 1 && (int)line.charAt(0) == 65535) {
            System.exit(0);
        }
        return true;
    }

    public void run(String cmd) {
        if (!this.isValidCommand(cmd)) {
            return;
        }
        if (Repl.verbose()) {
            if (cmd.length() == 1) {
                System.out.println("Executing command: " + cmd.charAt(0) + "(" + (int) cmd.charAt(0) + ")");
            } else {
                System.out.println("Executing command: " + cmd);
            }
        }
        this.forth.run(cmd);
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
