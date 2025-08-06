package forth;

import java.util.List;

public interface ForthInterpreterOperationsStandard {
    public void popLoop();
    public void push(Integer token);
    public void executeWord(String word);
    public void executePrint(String string);
    public void define(String word, List<ForthInterpreter.Instruction> instructions);
}

