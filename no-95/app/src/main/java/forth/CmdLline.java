package forth;

public class CmdLline {
    public CmdLline() {
    }

    public void run() {
        do {
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
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (true);
    }

    public void run(String cmd) {
        if (Repl.verbose()) {
            System.out.println("Executing command: " + cmd);
        }
    }
}
