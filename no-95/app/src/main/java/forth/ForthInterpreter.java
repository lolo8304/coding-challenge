package forth;

import java.util.*;

public class ForthInterpreter {
    private final Deque<Integer> stack;
    private final Map<String, Runnable> words;
    private final ForthParser parser;
    private StringBuilder outputBuilder;

    public interface Instruction {
        void execute(ForthInterpreter context);
    }

    public ForthInterpreter() {
        this.stack = new ArrayDeque<>();
        this.words = new HashMap<>();
        this.parser = new ForthParser();
        this.outputBuilder = new StringBuilder();
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
        this.words.put("swap",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n2);
            stack.push(n1);
        });
        this.words.put("dup",  () -> stack.push(stack.peek()));
        this.words.put("over",  () -> {
            var n2 = stack.pop();
            var n1 = stack.peek();
            stack.push(n2);
            stack.push(n1);
        });
        this.words.put("rot",  () -> {
            var n3 = stack.pop();
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n2);
            stack.push(n3);
            stack.push(n1);
        });
        this.words.put("drop", stack::pop);
        this.words.put(".",  () -> {
            var n1 = stack.pop();
            outputBuilder.append(n1);
        });
    }

    public void run(String line) {
        var instructions = parser.parse(line);
        for (Instruction instruction : instructions) {
            instruction.execute(this);
        }
    }

    public String outputToPrint() {
        var result = this.outputBuilder.toString();
        if (!result.isEmpty()) this.outputBuilder = new StringBuilder();
        return result;
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
