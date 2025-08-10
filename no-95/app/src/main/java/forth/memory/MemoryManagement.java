package forth.memory;

import forth.memory.Memory;

public class MemoryManagement {
    private final Memory memory;
    private final Memory literalMemory;

    public MemoryManagement(int memorySize, int literalMemorySize) {
        this.memory = new Memory(Address.Segment.DATA, memorySize);
        this.literalMemory = new Memory(Address.Segment.LITERAL, literalMemorySize);
    }

    public MemoryManagement() {
        this(1024 * 1024, 1024 * 64); // Default sizes: 1MB for data, 64KB for literals
    }

    public long getHere() {
        return memory.getHere();
    }

}
