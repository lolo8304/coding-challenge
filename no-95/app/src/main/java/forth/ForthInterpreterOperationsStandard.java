package forth;

import java.util.List;

public interface ForthInterpreterOperationsStandard {
    void push(Long token);
    Long pop();
    Long peek();
    void executeWord(String word);
    void executePrint(String string);
    void define(String word, List<ForthInterpreter.Instruction> instructions);
}

