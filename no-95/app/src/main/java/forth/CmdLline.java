package forth;

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
        this.forth.run(cmd);
        var output = this.forth.outputToPrint();
        if (!output.isEmpty()) System.out.println(output);
    }
}
