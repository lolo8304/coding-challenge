package dns;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class DnsServer {

    private static final Logger _logger = Logger.getLogger(DnsServer.class.getName());
    private final String dnsServer;
    private final int port;
    private final boolean verbose;


    public static byte[] hexStringToByteArray(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] data, int length) {
        StringBuilder hex = new StringBuilder(length * 2);
        for (int i = 0; i < length; i++) {
            hex.append(String.format("%02X", data[i]));
        }
        return hex.toString();
    }
    public DnsServer(String dnsServer, int port) {
        this(dnsServer, port, false);
    }

    public DnsServer(String dnsServer, int port, boolean verbose) {
        this.dnsServer = dnsServer;
        this.port = port;
        this.verbose = verbose;
    }

    public String sendAndReceive(String packageString) throws IOException {
        if (verbose) {
            _logger.info(String.format("DNS Request: %s", packageString));
        }
        var dnsRequestData = hexStringToByteArray(packageString);
        try(var socket = new DatagramSocket()) {

            var serverAddress = InetAddress.getByName(this.dnsServer);
            var packet = new DatagramPacket(dnsRequestData, dnsRequestData.length, serverAddress, this.port);
            socket.send(packet);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            byte[] responseData = receivePacket.getData();
            int responseLength = receivePacket.getLength();
            var hexResult = byteArrayToHexString(responseData, responseLength);
            if (verbose) {
                _logger.info(String.format("DNS Response: %s", hexResult));
            }
            return hexResult;
        }
    }

    public String lookup(DnsMessage message) throws IOException {
        return sendAndReceive(message.write(new OctetWriter()).toString());
    }
    public DnsMessage lookup(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_All);
    }
    public DnsMessage lookup(String domainName, int type) throws IOException {
        var message = new DnsMessage().addQuestion(new DnsQuestion(domainName, type));
        return new DnsMessage(new OctetReader(sendAndReceive(message.write(new OctetWriter()).toString())));
    }
    public DnsMessage lookupCNameRecord(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_CNAME);
    }
    public DnsMessage lookupARecord(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_A);
    }
}
