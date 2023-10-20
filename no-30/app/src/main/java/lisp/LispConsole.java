package lisp;

import lisp.parser.LispRuntime;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class LispConsole {

    private final LispRuntime runtime;

    public LispConsole(LispRuntime runtime) {
        this.runtime = runtime;
    }

    public void run() throws IOException {
        while (true) {
            var input = this.readFromConsole();
            if (input.isPresent() && !input.get().isBlank()) {
                var result = this.runtime.execute(input.get());
                System.out.println(result);
            }
        }
    }

    private Optional<String> readFromConsole() throws IOException {
        System.out.print("clisp>> ");
        Scanner scanner = new Scanner(System.in);
        // Read the string until the end of the line
        if (scanner.hasNextLine()) {
            return Optional.of(scanner.nextLine());
        } else {
            return Optional.empty();
        }
    }
}
