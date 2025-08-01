package dhcp;

import dhcp.message.DhcpMessage;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
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
        System.out.println("[1 DISCOVER] Sent DHCPDISCOVER broadcast to server.");
    }

    public void receiveOffer() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.receive(packet);
        var msg = new DhcpMessage(buffer, DhcpMessage.DHCPOFFER, this.offeredIp.getAddress(), this.serverIp.getAddress(), this.transactionId);

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
        var msg = new DhcpMessage(buffer, DhcpMessage.DHCPACK, this.offeredIp.getAddress(), this.serverIp.getAddress(), this.transactionId);

        if (msg.isResponseOfMessageType()) {
            this.clientIp = msg.getLeasedIp();
            System.out.printf("[4 ACK] DHCPACK received. Lease IP: %s%n", this.clientIp);
        } else if (msg.isResponseOfMessageType(DhcpMessage.DHCPNAK)) {
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
