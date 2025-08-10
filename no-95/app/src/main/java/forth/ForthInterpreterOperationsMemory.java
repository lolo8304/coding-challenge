package forth;

import java.util.List;

public interface ForthInterpreterOperationsMemory {
    boolean hasVariable(String name);
    Variable defineVariable(String name);
    Variable defineVariable(String name, Integer length);
    Variable getVariable(String name);

    boolean hasConstant(String name);
    Constant defineConstant(String name);
    Constant defineConstant(String name, Integer length);
    Constant getConstant(String name);

    Integer getCell(Integer address);
    void setCell(Integer address, Integer value);
    Integer cellAllot(Integer length);

    char getCharMemory(Integer address);
    void setCharMemory(Integer address, char value);
    void setStringMemory(Integer address, String value);
    String getStringMemory(Integer address, Integer length);
    Integer charAllot(Integer length);
    Integer stringAllot(String value);

}

