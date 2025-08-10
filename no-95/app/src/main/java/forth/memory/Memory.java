package forth.memory;

public class Memory {
    private final DataAllocator dataAllocator;
    private final byte[] memory;
    private final int align; // Default alignment

    public Memory(Address.Segment segment, int size) {
        this.align = segment == Address.Segment.DATA ? 4 : 1;
        this.dataAllocator = segment == Address.Segment.DATA ? new DataAllocator(segment, 0, this.align) : new DataAllocator(segment, 0);
        this.memory = new byte[size];
    }

    public int getLength() {
        return memory.length;
    }

    public void writeString(Long address, String value) {
        if (address < 0 || address + value.length() > memory.length) {
            throw new RuntimeException("Invalid memory address or length for string: " + address + ", " + value.length());
        }
        var addr = Address.fromLong(address,this.align);
        for (int i = 0; i < value.length(); i++) {
            memory[addr.index(i)] = (byte) value.charAt(i);
        }
    }

    public String readString(Long address, Long length) {
        if (address < 0 || address + length > memory.length) {
            throw new RuntimeException("Invalid memory address or length for string: " + address + ", " + length);
        }
        StringBuilder sb = new StringBuilder(length.intValue());
        var addr = Address.fromLong(address, this.align);
        for (int i = 0; i < length; i++) {
            sb.append((char) memory[addr.index(i)]);
        }
        return sb.toString();
    }

    public void writeChar(Long address, char value) {
        var addr = Address.fromLong(address, this.align);
        memory[addr.index()] = (byte) value;
    }

    public char readChar(Long address) {
        var addr = Address.fromLong(address, this.align);
        return (char) memory[addr.index()];
    }

    public void writeCell(Long address, Long value) {
        var addr = Address.fromLong(address, this.align);
        memory[addr.index()] = (byte) (value >> 24);
        memory[addr.index(1)] = (byte) (value >> 16);
        memory[addr.index(2)] = (byte) (value >> 8);
        memory[addr.index(4)] = (byte) (value & 0xFF);
    }

    public Long readCell(Long address) {
        var addr = Address.fromLong(address, this.align);
        if (addr.index() + 4 > memory.length) {
            throw new RuntimeException("Invalid memory address for cell read: " + address);
        }
        return ((long) memory[addr.index()] << 24) |
               ((long) memory[addr.index(1)] << 16) |
               ((long) memory[addr.index(2)] << 8) |
               ((long) memory[addr.index(3)] & 0xFF);
    }

    public void fill(Long address, int length, byte value) {
        if (address < 0 || address + length > memory.length) {
            throw new RuntimeException("Invalid memory address or length for fill: " + address + ", " + length);
        }
        var addr = Address.fromLong(address, this.align);
        for (int i = 0; i < length; i++) {
            memory[addr.index(i)] = value;
        }
    }

    public Long stringAllot(String value) {
        if (value == null) {
            throw new RuntimeException("String allot value cannot be null");
        }
        var length = value.length();
        if (length == 0) {
            return this.getHere();
        }
        var address = this.charAllot((long)length);
        var addr = Address.fromLong(address, this.align);
        for (int i = 0; i < length; i++) {
            memory[addr.index(i)] = (byte)value.charAt(i);
        }
        return address;
    }

    public Long charAllot(Long length) {
        if (length == null) {
            throw new RuntimeException("Constant allot length cannot be null");
        }
        if (length <= 0) {
            throw new RuntimeException("Constant allot length must be positive");
        }
        if (length > this.getLength()) {
            throw new RuntimeException("Constant allot length exceeds constant memory size");
        }
        if (this.getHere() + length > this.getLength()) {
            throw new RuntimeException("Not enough constant memory to allot " + length + " characters");
        }
        var address = this.getHere();
        this.dataAllocator.allot(length.intValue());
        return address;
    }

    public Long cellAllot(Long length) {
        if (length == null) {
            throw new RuntimeException("Allot length cannot be null");
        }
        if (length <= 0) {
            throw new RuntimeException("Allot length must be positive");
        }
        if (length * this.align > this.getLength()) {
            throw new RuntimeException("Allot length exceeds memory size");
        }
        if (this.getHere() + length * this.align > this.getLength()) {
            throw new RuntimeException("Not enough memory to allot " + length + " cells");
        }
        var address = this.getHere();
        this.dataAllocator.allot(length.intValue());
        return address;
    }

    public long getHere() {
        return dataAllocator.here();
    }
}