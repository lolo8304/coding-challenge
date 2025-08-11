package forth.memory;

import lombok.Getter;

@Getter
public class Constant extends Variable {
    private boolean writable = false;
    public Constant(long address, Long length) {
        super(address, length);
    }

    public Constant makeReadWrite() {
        this.writable = true;
        return this;
    }

    public Constant makeReadOnly() {
        this.writable = false;
        return this;
    }

}

