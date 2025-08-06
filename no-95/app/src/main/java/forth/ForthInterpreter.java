package forth;

import java.util.*;

public class ForthInterpreter implements ForthInterpreterOperationsAll {
    private final Deque<Integer> stack;
    private final Map<String, Object> words;
    private final Deque<LoopFrame> loopStack = new ArrayDeque<>();

    private final ForthParser parser;
    private StringBuilder outputBuilder;
    private int pc;

    public interface Instruction {
        void execute(ForthInterpreterOperationsAll context);
    }

    public ForthInterpreter() {
        this.stack = new ArrayDeque<>();
        this.words = new HashMap<>();
        this.parser = new ForthParser();
        this.outputBuilder = new StringBuilder();
        this.pc = 0;
        this.initializeBuiltIn();
    }

    public void addBuiltInWord(String word, Runnable action) {
        this.words.put(word,  action);
    }
    public void addDynamicWord(String word, List<ForthInterpreter.Instruction> instructions) {
        this.words.put(word, instructions);
    }

    private void initializeBuiltIn() {
        this.addBuiltInWord("+",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 + n2);
        });
        this.addBuiltInWord("-",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 - n2);
        });
        this.addBuiltInWord("*",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 * n2);
        });
        this.addBuiltInWord("/",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 / n2);
        });
        this.addBuiltInWord("mod",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 % n2);
        });
        this.addBuiltInWord("swap",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n2);
            stack.push(n1);
        });
        this.addBuiltInWord("dup",  () -> stack.push(stack.peek()));
        this.addBuiltInWord("over",  () -> {
            var n2 = stack.pop();
            var n1 = stack.peek();
            stack.push(n2);
            stack.push(n1);
        });
        this.addBuiltInWord("rot",  () -> {
            var n3 = stack.pop();
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n2);
            stack.push(n3);
            stack.push(n1);
        });
        this.addBuiltInWord("drop", stack::pop);
        this.addBuiltInWord(".",  () -> {
            var n1 = stack.pop();
            outputBuilder.append(n1);
        });
        this.addBuiltInWord("emit",  () -> {
            var n1 = stack.pop();
            outputBuilder.append((char)n1.intValue());
        });
        this.addBuiltInWord("cr",  () -> outputBuilder.append("\n"));
        this.addBuiltInWord(".*",  () -> {
            var n1 = stack.pop();
            outputBuilder.append((char)n1.intValue());
        });
        this.addBuiltInWord("<",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 < n2 ? -1 : 0);
        });
        this.addBuiltInWord(">",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 > n2 ? -1 : 0);
        });
        this.addBuiltInWord("=",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(Objects.equals(n1, n2) ? -1 : 0);
        });
        this.addBuiltInWord("<>",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(Objects.equals(n1, n2) ? 0 : -1);
        });
        this.addBuiltInWord("and",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1.equals(-1) && n2.equals(-1) ? -1 : 0);
        });
        this.addBuiltInWord("or",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1.equals(-1) || n2.equals(-1) ? -1 : 0);
        });
        this.addBuiltInWord("invert",  () -> {
            var n1 = stack.pop();
            stack.push(n1.equals(-1) ? 0 : -1);
        });
    }

    public void run(String line) {
        this.run(parser.parse(line));
    }
    public void run(List<ForthInterpreter.Instruction> instructions) {
        this.pc = 0;
        while (this.pc < instructions.size()) {
            var instruction = instructions.get(this.pc);
            instruction.execute(this);
            this.pc++;
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

    @Override
    public void push(Integer token) {
        this.stack.push(token);
    }
    @Override
    public void executeWord(String word) {
        var wordRunner = this.words.get(word);
        if (wordRunner != null) {
            if (wordRunner instanceof Runnable runnable) {
                runnable.run();
            } else if (wordRunner instanceof List<?> list) {
                //noinspection unchecked
                var wordInstructions = (List<ForthInterpreter.Instruction>) list;
                this.run(wordInstructions);
            }
        } else if (word.equalsIgnoreCase("i")) {
            if (loopStack.isEmpty()) {
                throw new RuntimeException("No active loop for 'i'");
            }
            stack.push(loopStack.peek().index);
        } else {
            System.out.println(word + " ?");
        }
    }
    @Override
    public void executePrint(String string) {
        outputBuilder.append(string);
    }

    @Override
    public void define(String word, List<ForthInterpreter.Instruction> instructions) {
        this.addDynamicWord(word, instructions);
    }

    @Override
    public void jumpTo(int i) {
        this.pc = i - 1; // later we will increase it again in the loop after instruction
    }
    @Override
    public Integer pop() {
        return this.stack.pop();
    }

    @Override
    public void pushLoop(int start, int limit) {
        loopStack.push(new LoopFrame(start, limit));
    }

    @Override
    public boolean incrementLoop() {
        LoopFrame top = loopStack.peek();
        top.index++;
        return top.index < top.limit;
    }

    @Override
    public void popLoop() {
        loopStack.pop();
    }

    private static class LoopFrame {
        int index;
        int limit;

        LoopFrame(int index, int limit) {
            this.index = index;
            this.limit = limit;
        }
    }

}
