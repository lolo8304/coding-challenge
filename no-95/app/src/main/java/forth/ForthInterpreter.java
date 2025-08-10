package forth;

import java.util.*;
import java.util.function.Consumer;

public class ForthInterpreter implements ForthInterpreterOperationsAll {
    private final Deque<Integer> stack;
    private final Map<String, Object> words;
    private final Deque<LoopFrame> loopStack = new ArrayDeque<>();
    private final byte[] memory = new byte[4 * 1024];

    private final ForthParser parser;
    private StringBuilder outputBuilder;
    private int pc = 0;
    private int dsp = 0; // data stack pointer to next free data stack slot

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
        this.wordsPut(word,  action);
    }
    public void addBuiltInWord(String word, Consumer<ForthInterpreter> action) {
        this.wordsPut(word,  action);
    }
    public void addDynamicWord(String word, List<ForthInterpreter.Instruction> instructions) {
        this.wordsPut(word, instructions);
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
        this.addBuiltInWord("2swap",  () -> {
            var n4 = stack.pop();
            var n3 = stack.pop();
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n3);
            stack.push(n4);
            stack.push(n1);
            stack.push(n2);
        });
        this.addBuiltInWord("dup",  () -> stack.push(stack.peek()));
        this.addBuiltInWord("2dup",  () -> {
            var n2 = stack.pop();
            var n1 = stack.peek();
            stack.push(n2);
            stack.push(n1);
            stack.push(n2);
        });
        this.addBuiltInWord("over",  () -> {
            var n2 = stack.pop();
            var n1 = stack.peek();
            stack.push(n2);
            stack.push(n1);
        });
        this.addBuiltInWord("2over",  () -> {
            var n4 = stack.pop();
            var n3 = stack.pop();
            var n2 = stack.peek();
            var n1 = stack.peek();
            stack.push(n1);
            stack.push(n2);
            stack.push(n3);
            stack.push(n4);
            stack.push(n1);
            stack.push(n2);
        });
        this.addBuiltInWord("rot",  () -> {
            var n3 = stack.pop();
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n2);
            stack.push(n3);
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
        this.addBuiltInWord("-rot",  () -> {
            var n3 = stack.pop();
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n2);
            stack.push(n1);
            stack.push(n3);
        });

        this.addBuiltInWord("drop", stack::pop);
        this.addBuiltInWord("2drop", () -> {
            stack.pop();
            stack.pop();
        });
        this.addBuiltInWord(".",  () -> {
            var n1 = stack.pop();
            outputBuilder.append(n1);
        });
        this.addBuiltInWord("emit",  () -> {
            var n1 = stack.pop();
            outputBuilder.append((char)n1.intValue());
        });
        this.addBuiltInWord("cr",  () -> outputBuilder.append("\n"));
        this.addBuiltInWord("<",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 < n2 ? -1 : 0);
        });
        this.addBuiltInWord("<=",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 <= n2 ? -1 : 0);
        });
        this.addBuiltInWord(">",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 > n2 ? -1 : 0);
        });
        this.addBuiltInWord(">=",  () -> {
            var n2 = stack.pop();
            var n1 = stack.pop();
            stack.push(n1 >= n2 ? -1 : 0);
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

        this.addBuiltInWord("depth", () -> stack.push(stack.size()));
        this.addBuiltInWord("clear", stack::clear);
        this.addBuiltInWord("bye",  () -> {
            System.out.println("bye");
            System.exit(0);
        });
        this.addBuiltInWord(".s",  () -> outputBuilder.append(stackToString()));

        this.addBuiltInWord("allot", (ForthInterpreter interpreter) -> {
            var length = interpreter.pop();
            interpreter.charAllot(length);
        });

        this.addBuiltInWord("here", (ForthInterpreter interpreter) -> {
            interpreter.push(this.dsp);
        });

        this.addBuiltInWord("cells", (ForthInterpreter interpreter) -> {
            interpreter.push(interpreter.pop() * 4);
        });

        this.addBuiltInWord("cell+", (ForthInterpreter interpreter) -> {
            var address = interpreter.pop();
            interpreter.push(address + 4);
        });

        this.addBuiltInWord("cell-", (ForthInterpreter interpreter) -> {
            var address = interpreter.pop();
            interpreter.push(address - 4);
        });

        this.addBuiltInWord("c@", (ForthInterpreter interpreter) -> {
            var address = interpreter.pop();
            int i = interpreter.getCharMemory(address);
            interpreter.push(i);
        });

        this.addBuiltInWord("c!", (ForthInterpreter interpreter) -> {
            var value = interpreter.pop();
            var address = interpreter.pop();
            interpreter.setCharMemory(address, (char)value.intValue());
        });

        this.addBuiltInWord("!", (ForthInterpreter interpreter) -> {
            var address = interpreter.pop();
            var value = interpreter.pop();
            interpreter.setCell(address, value);
        });

        this.addBuiltInWord("+!", (ForthInterpreter interpreter) -> {
            var address = interpreter.pop();
            var value = interpreter.pop();
            var current = interpreter.getCell(address);
            interpreter.setCell(address, current + value);
        });

        this.addBuiltInWord("@", (ForthInterpreter interpreter) -> {
            var address = interpreter.pop();
            var value = interpreter.getCell(address);
            interpreter.push(value);
        });

        this.addBuiltInWord("move", (ForthInterpreter interpreter) -> {
            // (fromAddr toAddr len -- )
            var length = interpreter.pop();
            var destAddress = interpreter.pop();
            var sourceAddress = interpreter.pop();
            if (length <= 0) {
                throw new RuntimeException("Move length must be positive");
            }
            if (sourceAddress.equals(destAddress)) {
                return; // No need to move if source and destination are the same
            }
            for (int i = 0; i < length; i++) {
                var value = interpreter.getCell(sourceAddress + i * 4);
                interpreter.setCell(destAddress + i * 4, value);
            }
        });
        this.addBuiltInWord("type", (ForthInterpreter interpreter) -> {
            var length = interpreter.pop();
            var address = interpreter.pop();
            var s = readString(address, length);
            interpreter.executePrint(s);
        });
        this.addBuiltInWord("nip", (ForthInterpreter interpreter) -> {
            var n2 = interpreter.pop();
            interpreter.pop();
            interpreter.push(n2);
        });
        this.addBuiltInWord("tuck", (ForthInterpreter interpreter) -> {
            var n2 = interpreter.pop();
            var n1 = interpreter.pop();
            interpreter.push(n2);
            interpreter.push(n1);
            interpreter.push(n2);
        });
        this.addBuiltInWord("words", (ForthInterpreter interpreter) -> {
            var wordList = new ArrayList<>(interpreter.words.keySet());
            Collections.sort(wordList);
            for (var w : wordList) {
                interpreter.executePrint(w + " ");
            }
        });
        this.addBuiltInWord("key", (ForthInterpreter interpreter) -> {
            try {
                int ch = System.in.read();
                if (ch == -1) {
                    throw new RuntimeException("End of input reached");
                }
                interpreter.push(ch);
            } catch (Exception e) {
                throw new RuntimeException("Error reading key input: " + e.getMessage());
            }
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
        builder.append("<").append(s).append("> ");
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
    public Integer pop() {
        return this.stack.pop();
    }

    @Override
    public Integer peek() {
        return this.stack.peek();
    }

    private Object wordsGet(String word) {
        return this.words.get(word.toLowerCase());
    }
    private void wordsPut(String word, Object value) {
        this.words.put(word.toLowerCase(), value);
    }

    @Override
    public void executeWord(String word) {
        var wordRunner = word != null && !word.isBlank() ? this.wordsGet(word) : null;
        if (wordRunner != null) {
            switch (wordRunner) {
                case Runnable runnable -> runnable.run();
                case Consumer<?> consumer -> {
                    //noinspection unchecked
                    var consumerAction = (Consumer<ForthInterpreter>) consumer;
                    consumerAction.accept(this);
                }
                case List<?> list -> {
                    //noinspection unchecked
                    var wordInstructions = (List<Instruction>) list;
                    this.run(wordInstructions);
                }
                case Constant constant -> {
                    this.stack.push(getCell(constant.getAddress()));
                }
                case Variable variable -> {
                    this.stack.push(variable.getAddress());
                }
                default -> {
                }
            }
        } else if (word != null && word.equalsIgnoreCase("i")) {
            if (loopStack.isEmpty()) {
                throw new RuntimeException("No active loop for 'i'");
            }
            stack.push(loopStack.peek().index);
        } else {
            this.executePrint(word + " ?");
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
    public void pushLoop(int start, int limit) {
        loopStack.push(new LoopFrame(start, limit));
    }

    @Override
    public boolean incrementLoop() {
        LoopFrame top = loopStack.peek();
        if (top == null) {
            throw new RuntimeException("No active loop to increment");
        }
        top.index++;
        return top.index < top.limit;
    }

    @Override
    public void popLoop() {
        loopStack.pop();
    }

    /* variables */

    private Integer addressToIndex(Integer address) {
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid memory address: " + address);
        }
        return address - 1073741824; // 2^30
    }

    private Integer indexToAddress(Integer index) {
        if (index < 0 || index >= memory.length) {
            throw new RuntimeException("Invalid memory index: " + index);
        }
        return index + 1073741824; // 2^30
    }

    @Override
    public Variable defineVariable(String name, Integer length) {
        var address = this.dsp;
        if (this.words.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' already defined");
        }
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid memory address for variable '" + name + "'");
        }
        if (length < 0) {
            throw new RuntimeException("Variable '" + name + "' length must be positive");
        }
        if (length > 0) {
            this.cellAllot(length);
        }
        var var = new Variable(address, length);
        this.wordsPut(name, var);
        return var;
    }


    @Override
    public Variable defineVariable(String name) {
        return this.defineVariable(name, 1);
    }


    @Override
    public Variable getVariable(String name) {
        if (!this.words.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' not defined");
        }
        var word = this.wordsGet(name);
        if (word instanceof Variable variable && !(word instanceof Constant)) {
            return variable;
        } else {
            throw new RuntimeException("Word '" + name + "' is not a variable");
        }
    }
    private String readString(int address, int length) {
        if (address < 0 || address + length > memory.length) {
            throw new RuntimeException("Invalid memory address or length for string: " + address + ", " + length);
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) memory[address + i]);
        }
        return sb.toString();
    }

    private void writeString(int address, String value) {
        if (address < 0 || address + value.length() > memory.length) {
            throw new RuntimeException("Invalid memory address or length for string: " + address + ", " + value.length());
        }
        for (int i = 0; i < value.length(); i++) {
            memory[address + i] = (byte) value.charAt(i);
        }
    }

    private char readChar(int address) {
        return (char) memory[address];
    }
    private Integer readCell(int address) {
        return ((memory[address] & 0xFF) << 24) |
               ((memory[address + 1] & 0xFF) << 16) |
               ((memory[address + 2] & 0xFF) << 8) |
               ((memory[address + 3] & 0xFF));
    }
    private void writeChar(int address, char value) {
        memory[address] = (byte) value;
    }
    private void writeCell(int address, Integer cell) {
        memory[address] = (byte) (cell >> 24);
        memory[address + 1] = (byte) (cell >> 16);
        memory[address + 2] = (byte) (cell >> 8);
        memory[address + 3] = (byte) (cell & 0xFF);
    }

    @Override
    public void setStringMemory(Integer address, String value) {
    if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid memory address: " + address);
        }
        if (value == null) {
            throw new RuntimeException("Value to set cannot be null");
        }
        if (value.length() + address > memory.length) {
            throw new RuntimeException("String length exceeds memory size at address: " + address);
        }
        this.writeString(address, value);
    }

    @Override
    public boolean hasVariable(String name) {
        return (!this.hasConstant(name)) && this.words.containsKey(name) && this.wordsGet(name) instanceof Variable;
    }

    @Override
    public boolean hasConstant(String name) {
        return this.words.containsKey(name) && this.wordsGet(name) instanceof Constant;
    }

    @Override
    public String getStringMemory(Integer address, Integer length) {
        return this.readString(address, length);
    }

    @Override
    public Integer getCell(Integer address) {
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid memory address: " + address);
        }
        return readCell(address);
    }

    @Override
    public void setCell(Integer address, Integer value) {
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid memory address: " + address);
        }
        if (value == null) {
            throw new RuntimeException("Value to set cannot be null");
        }
        writeCell(address, value);
    }

    @Override
    public Integer cellAllot(Integer length) {
        if (length == null) {
            throw new RuntimeException("Allot length cannot be null");
        }
        if (length <= 0) {
            throw new RuntimeException("Allot length must be positive");
        }
        if (length * 4 > memory.length) {
            throw new RuntimeException("Allot length exceeds memory size");
        }
        if (this.dsp + length * 4 > memory.length) {
            throw new RuntimeException("Not enough memory to allot " + length + " cells");
        }
        int address = this.dsp;
        // Initialize the memory for the allotted space
        for (int i = 0; i < length; i++) {
            writeCell(this.dsp + i * 4, 0);
        }
        this.dsp += length * 4; // Move the data stack pointer
        return address;
    }

    /* constants */

    @Override
    public Constant defineConstant(String name, Integer length) {
        var address = this.dsp;
        if (this.words.containsKey(name)) {
            throw new RuntimeException("Constant '" + name + "' already defined");
        }
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid memory address for constant '" + name + "'");
        }
        if (length <= 0) {
            throw new RuntimeException("Constant '" + name + "' length must be positive");
        }
        this.cellAllot(length);
        var constant = new Constant(address, length);
        this.wordsPut(name, constant);
        return constant;
    }

    @Override
    public Constant defineConstant(String name) {
        return this.defineConstant(name, 1);
    }


    @Override
    public Constant getConstant(String name) {
        if (!this.words.containsKey(name)) {
            throw new RuntimeException("Constant '" + name + "' not defined");
        }
        var word = this.wordsGet(name);
        if (word instanceof Constant constant) {
            return constant;
        } else {
            throw new RuntimeException("Word '" + name + "' is not a constant");
        }
    }

    @Override
    public char getCharMemory(Integer address) {
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid constant memory address: " + address);
        }
        return readChar(address);
    }

    @Override
    public void setCharMemory(Integer address, char value) {
        if (address < 0 || address >= memory.length) {
            throw new RuntimeException("Invalid constant memory address: " + address);
        }
        writeChar(address, value);
    }

    @Override
    public Integer charAllot(Integer length) {
        if (length == null) {
            throw new RuntimeException("Constant allot length cannot be null");
        }
        if (length <= 0) {
            throw new RuntimeException("Constant allot length must be positive");
        }
        if (length > memory.length) {
            throw new RuntimeException("Constant allot length exceeds constant memory size");
        }
        if (this.dsp + length > memory.length) {
            throw new RuntimeException("Not enough constant memory to allot " + length + " characters");
        }
        int address = this.dsp;
        // Initialize the constant memory for the allotted space
        for (int i = 0; i < length; i++) {
            memory[this.dsp + i] = (byte)(' ');
        }
        this.dsp += length;
        return address;
    }

    @Override
    public Integer stringAllot(String value) {
        if (value == null) {
            throw new RuntimeException("String allot value cannot be null");
        }
        var length = value.length();
        if (length == 0) {
            return this.dsp;
        }
        var address = this.charAllot(length);
        for (int i = 0; i < length; i++) {
            memory[address + i] = (byte)value.charAt(i);
        }
        return address;
    }

    private Integer rndInt() {
        return (int) (Math.random() * Integer.MAX_VALUE);
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
