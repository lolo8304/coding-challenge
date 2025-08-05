package forth;

import java.util.*;

public class ForthInterpreter {
    private final Deque<Integer> stack;
    private final Map<String, Runnable> words;
    private final ForthParser parser;

    public interface Instruction {
        void execute(ForthInterpreter context);
    }

    public ForthInterpreter() {
        this.stack = new ArrayDeque<>();
        this.words = new HashMap<>();
        this.parser = new ForthParser();
        this.initializeBuiltIn();
    }

    private void initializeBuiltIn() {
        this.words.put("+",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 + n2);
        });
        this.words.put("-",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 - n2);
        });
        this.words.put("*",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 * n2);
        });
        this.words.put("/",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 / n2);
        });
        this.words.put("mod",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 % n2);
        });
    }

    public void run(String line) {
        var instructions = parser.parse(line);
        for (int i = 0; i < instructions.size(); i++) {
            instructions.get(i).execute(this);
        }
    }

    public String stackToString() {
        var s = this.stack.size();
        if (s == 0) return "";
        var builder = new StringBuilder();
        var stackEntriesReversed = this.stack.toArray(Integer[]::new);
        for (int i = stackEntriesReversed.length - 1; i >= 0; i--) {
            builder.append(stackEntriesReversed[i]);
            builder.append(' ');
        }
        return builder.toString();
    }

    /* parser execution hooks */

    public void push(Integer token) {
        this.stack.push(token);
    }
    public void executeWord(String word) {
        var wordRunner = this.words.get(word);
        if (wordRunner != null) {
            wordRunner.run();
        } else {
            System.out.println(word + " ?");
        }
    }

}
