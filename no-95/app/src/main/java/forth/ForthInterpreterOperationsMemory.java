package forth;

import forth.memory.Constant;
import forth.memory.Variable;

public interface ForthInterpreterOperationsMemory {
    boolean hasVariable(String name);
    Variable defineVariable(String name);
    Variable defineVariable(String name, Long length);
    Variable getVariable(String name);

    boolean hasConstant(String name);
    Constant defineConstant(String name);
    Constant defineConstant(String name, Long length);
    Constant getConstant(String name);

    Long getCell(Long address);
    void setCell(Long address, Long value);
    Long cellAllot(Long length);

    char getCharMemory(Long address);
    void setCharMemory(Long address, char value);
    void setStringMemory(Long address, String value);
    String getStringMemory(Long address, Long length);
    Long charAllot(Long length);
    Long stringAllot(String value);

}

