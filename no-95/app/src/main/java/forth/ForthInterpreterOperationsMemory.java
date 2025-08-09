package forth;

import java.util.List;

public interface ForthInterpreterOperationsMemory {
    Variable defineVariable(String name);
    Variable defineVariable(String name, Integer address, Integer length);
    Variable getVariable(String name);

    Constant defineConstant(String name);
    Constant defineConstant(String name, Integer address, Integer length);
    Constant getConstant(String name);

    Integer getCell(Integer address);
    void setCell(Integer address, Integer value);
    Integer cellAllot(Integer length);

    char getCharMemory(Integer address);
    void setCharMemory(Integer address, char value);
    Integer charAllot(Integer length);
    Integer stringAllot(String value);

}

