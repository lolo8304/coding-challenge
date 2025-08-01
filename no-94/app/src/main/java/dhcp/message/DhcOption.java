package dhcp.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class DhcOption {

    private final byte code;
    private final byte length;
    private final byte[] data;

    public DhcOption(byte code, byte length, byte[] data) {
        this.code = code;
        this.length = length;
        this.data = data;
    }
    public DhcOption(int code, int length, byte[] data) {
        this((byte)code, (byte)length, data);
    }
    public DhcOption(byte code, byte[] data, byte[] data2) {
        this(code, getFlattenedData(data, data2));
    }
    public DhcOption(int code, byte[] data, byte[] data2) {
        this((byte)code, getFlattenedData(data, data2));
    }
    public DhcOption(byte code, byte[] data) {
        this(code, (byte) (data != null ? data.length : 0), data);
    }
    public DhcOption(int code, byte[] data) {
        this((byte)code, data);
    }
    public DhcOption(byte code, byte data) {
        this(code, (byte)1, new byte[]{data});
    }
    public DhcOption(int code, byte data) {
        this((byte)code, data);
    }
    public DhcOption(byte code) {
        this(code, null);
    }
    public DhcOption(int code) {
        this((byte)code, null);
    }

    private static byte[] getFlattenedData(byte[] data, byte[] data2) {
        if (data == null && data2 == null) {
            return null;
        }
        if (data == null) {
            return data2;
        }
        if (data2 == null) {
            return data;
        }
        byte[] flattenedData = new byte[data.length + data2.length];
        System.arraycopy(data, 0, flattenedData, 0, data.length);
        System.arraycopy(data2, 0, flattenedData, data.length, data2.length);
        return flattenedData;
    }

    @Override
    public String toString() {
        return "DhcOption{code=%02x/%s, length=%s, data=%s}".formatted(code, code, length, data != null ? bytesToString(data) : "null");
    }

    private String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        var first = true;
        for (byte b : bytes) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append("%02x".formatted(b)); // Convert byte to unsigned int
        }
        sb.append("]");
        return sb.toString().trim();
    }

    public void appendToBuffer(ByteBuffer buffer) {
        buffer.put(code);
        if (this.code >= 0 && this.code < 3 || this.code == -1) {
            // For options 0, 1, 2, and 255, the length is not used
        } else {
            buffer.put((byte)length);
        }
        if (this.data == null || this.data.length == 0) {
            return;
        }
        buffer.put(data);
    }

    public int getCodeUInt() {
        return code & 0xFF; // Convert byte to unsigned int
    }
}
