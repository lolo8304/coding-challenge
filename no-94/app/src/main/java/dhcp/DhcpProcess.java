package dhcp;

import dhcp.message.Converters;
import dhcp.message.DhcpMessage;
import dhcp.message.DhcpOptionEnum;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

@Getter
public class DhcpProcess {
    public static final int CLIENT_PORT = 68;
    public static final int SERVER_PORT = 67;
    private static final int TIMEOUT_MS = 10000;

    private InetAddress serverIp;
    private InetAddress offeredIp;
    private InetAddress clientIp;
    private String subnetMask;
    private String gateway;
    private String dnsServer;

    @Getter(AccessLevel.NONE)
    private byte[] clientMacAddress;

    @Getter(AccessLevel.NONE)
    private final byte[] transactionId;

    @Getter(AccessLevel.NONE)
    private DatagramSocket socket;

    public DhcpProcess() {
        this.transactionId = new byte[4];
        new Random().nextBytes(transactionId);
    }

    public DhcpProcess run() throws IOException {
        try {
            this.start();
            this.discover();
            this.receiveOffer();
            this.sendRequest();
            this.receiveAck();
        } finally {
            this.stop();
        }
        return this;
    }

    public void start() throws SocketException {
        // Logic to start the DHCP process
        System.out.println("Starting DHCP process...");
        // Here you would implement the logic to send DHCPDISCOVER, receive DHCPOFFER, etc.
        this.socket = new DatagramSocket(CLIENT_PORT);
    }

    public void stop() {
        // Logic to stop the DHCP process
        System.out.println("Stopping DHCP process...");
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
    }

    public void discover() throws IOException {
        var msg = new DhcpMessage(DhcpMessage.DHCPDISCOVER, this.transactionId);
        this.send(msg);
        if (Client.verbose()) {
            System.out.println("\n[1 DISCOVER] Sent DHCPDISCOVER broadcast to server.");
        }
        if (Client.verbose2()) {
            System.out.println("[1 DISCOVER] DHCPDISCOVER message in hex:");
            System.out.println(Converters.convertByteArraryToHexDump(msg.getBytes()));
        }
    }

    public void receiveOffer() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.receive(packet);
        var msg = new DhcpMessage(buffer);
        if (Client.verbose2()) {
            System.out.println("[2 OFFER] Received DHCPOFFER message in hex:");
            System.out.println(Converters.convertByteArraryToHexDump(msg.getBytes()));
        }

        if (msg.isResponseOfMessageType()) {
            this.serverIp = packet.getAddress();
            System.out.printf("[2 OFFER] Received DHCPOFFER from server: %s%n", this.serverIp.getHostAddress());
            this.offeredIp = msg.getOfferedIp();
            System.out.printf("[2 OFFER] Received DHCPOFFER for IP: %s%n", this.offeredIp);
        } else {
            throw new IOException("2 Expected DHCPOFFER, got: %s".formatted(msg.getResponseType()));
        }
    }

    private void sendRequest() throws IOException {
        var msg = new DhcpMessage(DhcpMessage.DHCPREQUEST, this.transactionId);
        msg.setServerIp(this.serverIp.getAddress());
        msg.setOfferIp(this.offeredIp.getAddress());
        msg.updateBuffer();
        if (Client.verbose2()) {
            System.out.println("[3 REQUEST] Sent DHCPREQUEST message in hex:");
            System.out.println(Converters.convertByteArraryToHexDump(msg.getBytes()));
        }
        var data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), SERVER_PORT);
        socket.send(packet);
        System.out.println("[3 REQUEST] Sent DHCPREQUEST for IP: " + this.offeredIp);
    }

    private void receiveAck() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.receive(packet);
        var msg = new DhcpMessage(buffer);
        if (Client.verbose2()) {
            System.out.println("[4 ACK] DHCPACK message in hex:");
            System.out.println(Converters.convertByteArraryToHexDump(msg.getBytes()));
        }

        if (msg.isResponseOfMessageType(DhcpMessage.DHCPACK)) {
            this.clientIp = msg.getLeasedIp();
            System.out.printf("[4 ACK] DHCPACK received. Lease IP: %s%n", this.clientIp);
        } else if (msg.isResponseOfMessageType(DhcpMessage.DHCPNAK)) {
            var error = msg.getOptions().get(DhcpOptionEnum.MESSAGE);
            if (error != null && error.getData().length > 0) {
                System.out.printf("[4 NAK] DHCPNAK received. Lease rejected: %s%n", new String(error.getData()));
            } else {
                System.out.println("[4 NAK] DHCPNAK received. Lease rejected.");
            }
            throw new IOException("[4 NAK] DHCPNAK received. Lease rejected.");
        } else {
            throw new IOException("4 Unexpected response: %s".formatted(buffer[242]));
        }
    }




    public void send(DhcpMessage msg) throws IOException {
        var data = msg.getBytes();
        var packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), SERVER_PORT);
        socket.send(packet);
    }

}
