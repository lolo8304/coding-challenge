package dhcp.message;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Getter
@Setter
public class DhcOptions {
    public static final DhcOption END_OPTION = new DhcOption((byte) 255);
    public static final DhcOption PAD_OPTION = new DhcOption((byte) 0);

    private final ArrayList<DhcOption> options;

    public DhcOptions() {
        this.options = new ArrayList<DhcOption>();
    }

    public DhcOptions(ArrayList<DhcOption> options) {
        this.options = options;
    }

    public DhcOptions add(int code, byte data) {
        return add((byte) code, data);
    }
    public DhcOptions add(byte code, byte data) {
        this.options.add(new DhcOption(code, data));
        return this;
    }
    public DhcOptions add(int code, byte[] data) {
        return add((byte) code, data);
    }
    public DhcOptions add(byte code, byte[] data) {
        this.options.add(new DhcOption(code, data));
        return this;
    }
    public DhcOptions add(int code, byte[] data, byte[] data2) {
        return add((byte) code, data, data2);
    }
    public DhcOptions add(byte code, byte[] data, byte[] data2) {
        this.options.add(new DhcOption(code, data, data2));
        return this;
    }

    public DhcOptions add(DhcOption option) {
        this.options.add(option);
        return this;
    }

    public void setToBuffer(ByteBuffer buffer) {
        buffer.position(240);
        // Set padding option if needed
        if (!this.options.isEmpty()) {
            addPadOption(buffer);
        }
        for (var option : this.options) {
            if (option.getCode() == 0 || option.getCode() == 255) continue; // Skip padding options
            option.appendToBuffer(buffer);
        }
        // Add end option all the time
        addEndOption(buffer);
    }

    private void addPadOption(ByteBuffer buffer) {
        PAD_OPTION.appendToBuffer(buffer);
    }

    private void addEndOption(ByteBuffer buffer) {
        END_OPTION.appendToBuffer(buffer);
    }
}

