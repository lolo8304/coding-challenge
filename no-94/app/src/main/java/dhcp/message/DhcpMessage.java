package dhcp.message;

import dhcp.YourClient;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Getter
@Setter
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

    private byte messageType;
    private byte op; // op - operation code (1 for request, 2 for reply)
    private byte[] hostName = YourClient.getInstance().hostName();  // sname - server host name (not used)
    private byte[] clientIp; // ciaddr - client IP address only if ip is in BOUNDING, RENEWING, or REBINDING state
    private byte[] offerIp; // yiaddr - your (client) IP address, used in OFFER and ACK messages
    private byte[] serverIp; // siaddr - server (next) IP address
    private byte[] transactionId; // xid - transaction ID, 4 bytes
    private byte[] clientIdentifier = YourClient.getInstance().clientIdentifier(); // chaddr - client hardware address, 6 bytes for Ethernet
    private int leaseTime = 90 * 24 * 60 * 60; // Default lease time: 90 days in seconds;
    private final DhcOptions options = new DhcOptions();

    private byte[] bytes;
    private byte hardwareType; // htype - hardware type (1 for Ethernet)
    private byte[] gatewayIp; // giaddr - gateway (relay agent) IP address, 4 bytes
    private boolean broadcastFlag; // flags - broadcast flag, 0x8000 for broadcast
    private short secondsElapsed; // secs - seconds elapsed since the client began the address acquisition or renewal process
    private byte hops; // hops - number of hops, used by relay agents to indicate the number of hops a message has taken
    private byte hardwareAddressLength; // hlen - hardware address length, 6 for Ethernet

    public static DhcpMessage buildDiscoverMessage(byte[] transactionId) {
        return new DhcpMessage(DHCPDISCOVER, transactionId);
    }

    public static DhcpMessage parse(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length < 240) {
            throw new IOException("Buffer is too short to parse DHCP message");
        }
        return new DhcpMessage(bytes);
    }

    public DhcpMessage(byte messageType, byte[] transactionId) {
        this.op = BOOTREQUEST;
        this.messageType = messageType;
        this.transactionId = transactionId;
        this.bytes = this.buildBuffer();
    }

    public DhcpMessage(byte[] bytes) {
        this.bytes = bytes;
        this.parse();
    }

    public void updateBuffer() {
        this.options.clear();
        this.bytes = this.buildBuffer();
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
        buf.put(Converters.convertEmptyAddress()); // ciaddr - client IP address only if ip is in BOUNDING, RENEWING, or REBINDING state
        buf.put(Converters.convertEmptyAddress()); // yiaddr - your (client) IP address
        buf.put(Converters.convertEmptyAddress()); // siaddr - server (next) IP address
        buf.put(Converters.convertEmptyAddress()); // giaddr - gateway (relay agent) IP address

        buf.put(Converters.convertAddress(this.clientIdentifier)); // chaddr (first 6 bytes) - client hardware address
        // server  host name (sname) and boot file name (file) are not used in this message
        //buf.put(new byte[64]); // sname - server host name (not used)
        //buf.put(new byte[128]); // file - boot file name (not used)

        buf.position(236); // skip to magic cookie position
        buf.putInt(0x63825363); // magic cookie

        this.options.add(DhcpOptionEnum.DHCP_MESSAGE_TYPE, messageType);
        this.options.add(DhcpOptionEnum.PARAMETER_REQUEST_LIST, new byte[]{1, 121, 3, 6, 15, 114, 119, (byte)252, 95, 44, 46}); // Parameter request list
        this.options.add(DhcpOptionEnum.MAX_DHCP_MESSAGE_SIZE, Converters.convertUIntToByteArray(1500)); // Maximum DHCP message size
        this.options.add(DhcpOptionEnum.CLIENT_IDENTIFIER, Converters.convertByteToByteArray(HTYPE_ETHERNET), this.clientIdentifier); // Client identifier option
        if (messageType == DHCPREQUEST) {
            this.options.add(DhcpOptionEnum.REQUESTED_IP_ADDRESS, Converters.convertAddress(this.offerIp)); // Requested IP address
            this.options.add(DhcpOptionEnum.SERVER_IDENTIFIER, Converters.convertAddress(this.serverIp)); // Server identifier
            this.options.add(DhcpOptionEnum.HOST_NAME, this.hostName); // Host name (not used)
        }

        if (this.leaseTime > 0 && messageType == DHCPDISCOVER) {
            this.options.add(51, Converters.convertIntToByteArray(this.leaseTime)); // Lease time
        }
        this.options.setToBuffer(buf);
        int padding = 4 - (buf.position() % 4);
        buf.position(buf.position() + padding); // Align to 4-byte boundary
        if (buf.position() < 300) {
            // Ensure minimum size of 300 bytes
            buf.position(300);
        }
        return Arrays.copyOf(buf.array(), buf.position());
    }
    
    private void parse() {
        if (this.bytes == null || this.bytes.length < 240) {
            throw new IllegalArgumentException("Buffer is too short to parse DHCP message");
        }
        ByteBuffer buf = ByteBuffer.wrap(this.bytes);
        this.op = buf.get(); // op
        this.hardwareType = buf.get(); // htype
        this.hardwareAddressLength = buf.get(); // hlen
        this.hops = buf.get(); // hops

        this.transactionId = new byte[4];
        buf.get(this.transactionId); // xid
        this.secondsElapsed = buf.getShort(); // secs
        this.broadcastFlag = (buf.getShort() & 0x80) == 0x80; // flags
        this.clientIp = new byte[4];
        buf.get(this.clientIp); // ciaddr
        this.offerIp = new byte[4];
        buf.get(this.offerIp); // yiaddr
        this.serverIp = new byte[4];
        buf.get(this.serverIp); // siaddr
        this.gatewayIp = new byte[4];
        buf.get(this.gatewayIp); // giaddr - gateway IP address

        this.clientIdentifier = new byte[this.hardwareAddressLength];
        buf.get(this.clientIdentifier); // chaddr - client hardware address
        buf.position(buf.position() + (16 - this.hardwareAddressLength)); // skip padding to 16 bytes

        // Skip sname and file fields
        buf.position(236); // skip to magic cookie position
        int magicCookie = buf.getInt();
        if (magicCookie != 0x63825363) {
            throw new IllegalArgumentException("Invalid DHCP magic cookie");
        }

        this.options.parse(buf);
        this.messageType = this.options.getByte(DhcpOptionEnum.DHCP_MESSAGE_TYPE);
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
