package dhcp.message;

import dhcp.YourClient;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DhcpMessage {

    // DHCP Message Types
    public static final byte DHCPDISCOVER = 1;
    public static final byte DHCPOFFER = 2;
    public static final byte DHCPREQUEST = 3;
    public static final byte DHCPDECLINE = 4;
    public static final byte DHCPACK = 5;
    public static final byte DHCPNAK = 6;
    public static final byte DHCPRELEASE = 7;

    public static final byte BOOTREQUEST = 1;
    public static final byte BOOTREPLY = 2;

    public static final byte HTYPE_ETHERNET = 1;

    private final byte messageType;
    private final byte op;
    private final byte[] hostName;
    private byte[] offerIp;
    private byte[] serverIp;
    private final byte[] transactionId;
    private byte[] clientIdentifier;
    private byte[] clientIp;
    private int leaseTime;
    private final DhcOptions options = new DhcOptions();

    private byte[] bytes;

    public DhcpMessage(byte messageType, byte[] transactionId) {
        this.op = BOOTREQUEST;
        this.messageType = messageType;
        this.clientIdentifier = YourClient.getInstance().clientIdentifier();
        this.hostName = YourClient.getInstance().hostName();
        this.transactionId = transactionId;
        this.bytes = new byte[548]; // 236 BOOTP + 312 options (max)
        this.leaseTime = 90 * 24 * 60 * 60; // Default lease time: 90 days in seconds
        this.bytes = this.buildBuffer();
    }

    public DhcpMessage(byte[] bytes, byte messageType, byte[] offerIp, byte[] serverIp, byte[] transactionId) {
        this.op = BOOTREPLY;
        this.bytes = bytes;
        this.messageType = messageType;
        this.hostName = YourClient.getInstance().hostName();
        this.offerIp = offerIp;
        this.serverIp = serverIp;
        this.transactionId = transactionId;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    private byte[] buildBuffer() {
        /* Buffer from RFC
           0                   1                   2                   3
   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     op (1)    |   htype (1)   |   hlen (1)    |   hops (1)    |
   +---------------+---------------+---------------+---------------+
   |                            xid (4)                            |
   +-------------------------------+-------------------------------+
   |           secs (2)            |           flags (2)           |
   +-------------------------------+-------------------------------+
   |                          ciaddr  (4)                          |
   +---------------------------------------------------------------+
   |                          yiaddr  (4)                          |
   +---------------------------------------------------------------+
   |                          siaddr  (4)                          |
   +---------------------------------------------------------------+
   |                          giaddr  (4)                          |
   +---------------------------------------------------------------+
   |                                                               |
   |                          chaddr  (16)                         |
   |                                                               |
   |                                                               |
   +---------------------------------------------------------------+
   |                                                               |
   |                          sname   (64)                         |
   +---------------------------------------------------------------+
   |                                                               |
   |                          file    (128)                        |
   +---------------------------------------------------------------+
   |                                                               |
   |                          options (variable)                   |
   +---------------------------------------------------------------+
         */

        var buf = ByteBuffer.allocate(548);
        buf.put((byte) 1); // op
        buf.put((byte) 1); // htype
        buf.put((byte) 6); // hlen
        buf.put((byte) 0); // hops

        buf.put(transactionId); // xid
        buf.putShort((short) 0); // secs
        buf.putShort((short) 0x8000); // flags (broadcast)
        buf.put(Converters.convertEmptyAddress()); // ciaddr - client IP address
        buf.put(Converters.convertEmptyAddress()); // yiaddr - your IP address
        buf.put(Converters.convertEmptyAddress()); // siaddr - server IP address
        buf.put(Converters.convertEmptyAddress()); // giaddr - gateway IP address

        buf.put(Converters.convertAddress(this.clientIdentifier)); // chaddr (first 6 bytes) - client hardware address
        // server  host name (sname) and boot file name (file) are not used in this message
        //buf.put(new byte[64]); // sname - server host name (not used)
        //buf.put(new byte[128]); // file - boot file name (not used)

        buf.position(236); // skip to magic cookie position
        buf.putInt(0x63825363); // magic cookie

        this.options.add(53, messageType);
        this.options.add(55, new byte[]{1, 121, 3, 6, 15, 114, 119, (byte)252, 95, 44, 46}); // Parameter request list
        this.options.add(57, Converters.convertUIntToByteArray(1500)); // Maximum DHCP message size
        this.options.add(61, Converters.convertByteToByteArray(HTYPE_ETHERNET), this.clientIdentifier); // Client identifier option
        if (messageType == DHCPREQUEST) {
            this.options.add(50, Converters.convertAddress(this.offerIp)); // Requested IP address
            this.options.add(54, Converters.convertAddress(this.serverIp)); // Server identifier
            this.options.add(12, Converters.convertAddress(YourClient.getInstance().hostName())); // Host name (not used)
        }

        if (this.leaseTime > 0) {
            this.options.add(51, Converters.convertIntToByteArray(this.leaseTime)); // Lease time
        }
        this.options.setToBuffer(buf);
        if (buf.position() % 4 != 0) {
            // Pad to 4-byte boundary
            int padding = 4 - (buf.position() % 4);
            for (int i = 0; i < padding; i++) {
                buf.put((byte) 0);
            }
        }
        return Arrays.copyOf(buf.array(), buf.position());
    }

    public boolean isResponseOfMessageType(byte msgType) {
        return this.bytes != null && this.bytes.length > 242 && this.bytes[242] == msgType;
    }

    public boolean isResponseOfMessageType() {
        return this.bytes != null && this.bytes.length > 242 && this.bytes[242] == this.messageType;
    }

    public InetAddress getOfferedIp() throws IOException {
        return this.getResponseIp();
    }

    public InetAddress getLeasedIp() throws IOException {
        return this.getResponseIp();
    }

    public InetAddress getResponseIp() throws IOException {
        if (this.bytes == null || this.bytes.length < 16) {
            throw new IOException("Buffer is too short to contain response IP");
        }
        byte[] ipBytes = Arrays.copyOfRange(this.bytes, 16, 20);
        return InetAddress.getByAddress(ipBytes);
    }

    public String getMessageTypeString() {
        return switch (this.messageType) {
            case DHCPDISCOVER -> "DHCPDISCOVER";
            case DHCPOFFER -> "DHCPOFFER";
            case DHCPREQUEST -> "DHCPREQUEST";
            case DHCPDECLINE -> "DHCPDECLINE";
            case DHCPACK -> "DHCPACK";
            case DHCPNAK -> "DHCPNAK";
            case DHCPRELEASE -> "DHCPRELEASE";
            default -> "UNKNOWN";
        };
    }

    public byte getResponseType() {
        return this.bytes != null && this.bytes.length > 242 ? this.bytes[242] : -1;
    }
}
