package dhcp.message;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@SuppressWarnings("UnusedReturnValue")
@Getter
@Setter
public class DhcOptions {
    public static final DhcOption END_OPTION = new DhcOption((byte) 255);

    private final ArrayList<DhcOption> options;

    public DhcOptions() {
        this.options = new ArrayList<>();
    }

    public DhcOptions add(int code, byte data) {
        return add((byte) code, data);
    }

    public DhcOptions add(byte code, byte data) {
        this.options.add(new DhcOption(code, data));
        return this;
    }

    public DhcOptions add(DhcpOptionEnum codeEnum, byte data) {
        return add(codeEnum.getCode(), data);
    }

    public DhcOptions add(int code, byte[] data) {
        return add((byte) code, data);
    }

    public DhcOptions add(DhcpOptionEnum codeEnum, byte[] data) {
        return add(codeEnum.getCode(), data);
    }

    public DhcOptions add(byte code, byte[] data) {
        this.options.add(new DhcOption(code, data));
        return this;
    }

    public DhcOptions add(int code, byte[] data, byte[] data2) {
        return add((byte) code, data, data2);
    }

    public DhcOptions add(DhcpOptionEnum codeEnum, byte[] data, byte[] data2) {
        return add(codeEnum.getCode(), data, data2);
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
        var withoutPad = this.options.stream()
                .filter(option -> option.getCode() != 0 && option.getCode() != -1).toList();
        for (var option : withoutPad) {
            option.appendToBuffer(buffer);
        }
        // Add end option all the time
        addEndOption(buffer);
    }

    private void addEndOption(ByteBuffer buffer) {
        END_OPTION.appendToBuffer(buffer);
    }

    public void parse(ByteBuffer buf) {
        this.options.clear();
        while (buf.hasRemaining()) { // Read options until END_OPTION or buffer is exhausted
            byte code = buf.get();
            if (code == 0) {
                // PAD option, skip it
                continue;
            }
            if (code == -1) {
                // END option, stop parsing
                break;
            }
            var length = buf.get();
            var data = new byte[length];
            buf.get(data);
            this.options.add(new DhcOption(code, length, data));
        }
        // Ensure the last option is END_OPTION
        if (this.options.isEmpty() || !this.options.getLast().equals(END_OPTION)) {
            this.options.add(END_OPTION);
        }
    }

    public DhcOption get(int code) {
        for (var option : this.options) {
            if (option.getCode() == code) {
                return option;
            }
        }
        return null; // Option not found
    }

    public DhcOption get(DhcpOptionEnum codeEnum) {
        return get(codeEnum.getCode());
    }

    public byte getByte(DhcpOptionEnum dhcpOptionEnum) {
        DhcOption option = get(dhcpOptionEnum);
        if (option != null && option.getData() != null && option.getData().length > 0) {
            return option.getData()[0];
        }
        return 0; // Return 0 if option not found or data is empty
    }

    public void clear() {
        this.options.clear();
    }
}

