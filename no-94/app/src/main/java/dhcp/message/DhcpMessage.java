package dhcp.message;

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

    private final byte messageType;
    private byte[] offerIp;
    private byte[] serverIp;
    private final byte[] transactionId;
    private byte[] clientIdentifier;
    private byte[] clientIp;

    private byte[] bytes;

    public DhcpMessage(byte messageType, byte[] clientIdentifier, byte[] transactionId) {
        this.bytes = new byte[548]; // 236 BOOTP + 312 options (max)
        this.messageType = messageType;
        this.clientIdentifier = clientIdentifier;
        this.transactionId = transactionId;
        this.bytes = this.buildBuffer();
    }

    public DhcpMessage(byte[] bytes, byte messageType, byte[] offerIp, byte[] serverIp, byte[] transactionId) {
        this.bytes = bytes;
        this.messageType = messageType;
        this.offerIp = offerIp;
        this.serverIp = serverIp;
        this.transactionId = transactionId;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    private byte[] buildBuffer() {
        var buffer = ByteBuffer.wrap(this.bytes);

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

        ByteBuffer buf = ByteBuffer.allocate(548);
        buf.put((byte) 1); // op
        buf.put((byte) 1); // htype
        buf.put((byte) 6); // hlen
        buf.put((byte) 0); // hops
        buf.put(transactionId); // xid
        buf.putShort((short) 0); // secs
        buf.putShort((short) 0x8000); // flags (broadcast)
        buf.putInt(0); // ciaddr
        buf.putInt(0); // yiaddr
        buf.putInt(0); // siaddr
        buf.putInt(0); // giaddr

        buf.put(this.clientIdentifier); // chaddr (first 6 bytes)
        buf.position(236); // skip to options
        buf.putInt(0x63825363); // magic cookie

        buf.put((byte) 53); // DHCP Message Type
        buf.put((byte) 1);
        buf.put(messageType);

        buf.put((byte) 61); // Client identifier
        buf.put((byte) (1 + this.clientIdentifier.length));
        buf.put((byte) 1);
        buf.put(this.clientIdentifier);

        if (messageType == DHCPREQUEST) {
            buf.put((byte) 50); // Requested IP
            buf.put((byte) 4);
            buf.put(this.offerIp); // Offered IP address

            buf.put((byte) 54); // Server identifier
            buf.put((byte) 4);
            buf.put(this.serverIp);
        }

        buf.put((byte) 55); // Parameter Request List
        buf.put((byte) 3);
        buf.put((byte) 1); // Subnet mask
        buf.put((byte) 3); // Router
        buf.put((byte) 6); // DNS

        buf.put((byte) 255); // End

        return Arrays.copyOf(buffer.array(), buffer.position());
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

    public byte getMessageType() {
        return this.messageType;
    }

    public byte getResponseType() {
        return this.bytes != null && this.bytes.length > 242 ? this.bytes[242] : -1;
    }
}
