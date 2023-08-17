package dns;

import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class DnsServer {

    public static final Map<String, String> RootServers;
    public static final List<String> RootServerNames;

    static {
        RootServers = new HashMap<>();
        RootServers.put("a.root-servers.net", "198.41.0.4");
        RootServers.put("b.root-servers.net", "199.9.14.201");
        RootServers.put("c.root-servers.net", "192.33.4.12");
        RootServers.put("d.root-servers.net", "199.7.91.13");
        RootServers.put("e.root-servers.net", "192.203.230");
        RootServers.put("f.root-servers.net", "192.5.5.241");
        RootServers.put("i.root-servers.net", "192.36.148.17");
        RootServers.put("j.root-servers.net", "192.58.128.30");
        RootServers.put("k.root-servers.net", "193.0.14.129");
        RootServers.put("l.root-servers.net", "199.7.83.42");
        RootServers.put("m.root-servers.net", "202.12.27.33");

        RootServerNames = new java.util.ArrayList<>(RootServers.keySet().stream().toList());
        RootServerNames.sort(String::compareTo);
    }

    private static final Logger _logger = Logger.getLogger(DnsServer.class.getName());
    private final String dnsServer;
    private final int port;
    private final boolean verbose;


    public String randomRootName() {
        return RootServerNames.get(new SecureRandom().nextInt(RootServerNames.size()));
    }
    @SuppressWarnings("unused")
    public String randomRootIpAddress() {
        return RootServers.get(randomRootName());
    }

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

    public DnsServer() {
        this.dnsServer = randomRootName();
        this.port = 53;
        this.verbose = false;
    }
    public DnsServer(boolean verbose) {
        this.dnsServer = randomRootName();
        this.port = 53;
        this.verbose = verbose;
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
            _logger.info(String.format("DNS %s, Request: %s", this.dnsServer, packageString));
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
                _logger.info(String.format("DNS %s, Response: %s", this.dnsServer, hexResult));
            }
            return hexResult;
        }
    }

    @SuppressWarnings("unused")
    public String lookup(DnsMessage message) throws IOException {
        return sendAndReceive(message.write(new OctetWriter()).toString());
    }
    @SuppressWarnings("unused")
    public Optional<DnsMessage> lookupNS(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_NS,Flags.RECURSION_DESIRED_OFF);
    }
    public Optional<DnsMessage> lookup(String domainName, int type) throws IOException {
        return this.lookup(domainName, type, 0);
    }
    public Optional<DnsMessage> lookup(String domainName, int type, int additionalFlags) throws IOException {
        var message = new DnsMessage(additionalFlags).addQuestion(new DnsQuestion(domainName, type));
        return Optional.of(new DnsMessage(new OctetReader(sendAndReceive(message.write(new OctetWriter()).toString()))));
    }
    @SuppressWarnings("unused")
    public Optional<DnsMessage> lookupCNAME(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_CNAME);
    }
    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public Optional<DnsMessage> lookupA(String domainName) throws IOException {
        var dnsResponse = this.lookup(domainName, HeaderFlags.QTYPE_A, Flags.RECURSION_DESIRED);
        if (dnsResponse.isPresent()) {
            var dnsRecord = dnsResponse.get();
            if (dnsRecord.hasIpAddress()) {
                return Optional.of(dnsRecord);
            } else if (dnsRecord.getAuthorityCount() > 0) {
                var nsRecord = dnsRecord.getRandomAuthority();
                if (nsRecord.getRDataString("ADDRESS")!= null) {
                    return new DnsServer(nsRecord.getRDataString("ADDRESS"), port, verbose).lookupA(domainName);
                } else {
                    return new DnsServer(nsRecord.getRDataString("NSDNAME"), port, verbose).lookupA(domainName);
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
