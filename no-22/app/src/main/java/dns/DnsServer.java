package dns;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;

public class DnsServer {

    public static final List<Name> RootServers;

    static {
        RootServers = new ArrayList<>();
        RootServers.add(Name.fromName("a.root-servers.net", "198.41.0.4"));
        RootServers.add(Name.fromName("b.root-servers.net", "199.9.14.201"));
        RootServers.add(Name.fromName("c.root-servers.net", "192.33.4.12"));
        RootServers.add(Name.fromName("d.root-servers.net", "199.7.91.13"));
        RootServers.add(Name.fromName("e.root-servers.net", "192.203.230"));
        RootServers.add(Name.fromName("f.root-servers.net", "192.5.5.241"));
        RootServers.add(Name.fromName("i.root-servers.net", "192.36.148.17"));
        RootServers.add(Name.fromName("j.root-servers.net", "192.58.128.30"));
        RootServers.add(Name.fromName("k.root-servers.net", "193.0.14.129"));
        RootServers.add(Name.fromName("l.root-servers.net", "199.7.83.42"));
        RootServers.add(Name.fromName("m.root-servers.net", "202.12.27.33"));
    }

    private static final Logger _logger = Logger.getLogger(DnsServer.class.getName());
    private final Name dnsServer;
    private final int port;
    private final DnsServer.Verbose verbose;

    public static Name randomRootServer() {
        return RootServers.get(new SecureRandom().nextInt(RootServers.size()));
    }
    public DnsServer() {
        this.dnsServer = randomRootServer();
        this.port = 53;
        this.verbose = Verbose.NONE;
    }
    public DnsServer(boolean verbose) {
        this.dnsServer = randomRootServer();
        this.port = 53;
        this.verbose = Verbose.fromValue(verbose);
    }
    public DnsServer(Verbose verbose) {
        this.dnsServer = randomRootServer();
        this.port = 53;
        this.verbose = verbose;
    }
    public DnsServer(String dnsServer, int port) {
        this(Name.fromName(dnsServer), port, false);
    }

    public DnsServer(Name dnsServer, int port, boolean verbose) {
        this.dnsServer = dnsServer;
        this.port = port;
        this.verbose = Verbose.fromValue(verbose);
    }
    public DnsServer(Name dnsServer, int port, Verbose verbose) {
        this.dnsServer = dnsServer;
        this.port = port;
        this.verbose = verbose;
    }

    public String sendAndReceive(String packageString) throws IOException {
        if (verbose == Verbose.FINER) {
            _logger.info(String.format("DNS %s, Request: %s", this.dnsServer, packageString));
        }
        var dnsRequestData = OctetHelper.hexStringToByteArray(packageString);
        try(var socket = new DatagramSocket()) {

            var serverAddress = InetAddress.getByName(this.dnsServer.getSearchValue());
            var packet = new DatagramPacket(dnsRequestData, dnsRequestData.length, serverAddress, this.port);
            socket.send(packet);
            socket.setSoTimeout(3000);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            byte[] responseData = receivePacket.getData();
            int responseLength = receivePacket.getLength();
            var hexResult = OctetHelper.byteArrayToHexString(responseData, responseLength);
            if (verbose == Verbose.FINER) {
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
        if (verbose == Verbose.FINE || verbose == Verbose.FINER) {
            _logger.info(String.format("Querying %s for %s", this.dnsServer, domainName));
        }
        var request = new DnsMessage(additionalFlags).addQuestion(new DnsQuestion(domainName, type));
        var response = new DnsMessage(new OctetReader(sendAndReceive(request.write(new OctetWriter()).toString())));
        if (response.hasAnswerOf(type)) {
            return Optional.of(response);
        } else if (response.getAuthorityCount() > 0) {
            if (response.hasAuthority(this.dnsServer)) {
                var nsRecord = response.getRandomAuthority();
                return new DnsServer(nsRecord.getAuthorityName(), port, verbose).lookup(domainName, type, additionalFlags);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unused")
    public Optional<DnsMessage> lookupCNAME(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_CNAME);
    }
    @SuppressWarnings({"unused"})
    public Optional<DnsMessage> lookupA(String domainName) throws IOException {
        return this.lookup(domainName, HeaderFlags.QTYPE_A, Flags.RECURSION_DESIRED_OFF);
    }

    public Optional<DnsMessage> resolve(String domainName, int additionalFlags) throws IOException {
        var resultA = this.lookup(domainName, HeaderFlags.QTYPE_A, additionalFlags);
        if (resultA.isPresent()) {
            var ipAddresses = resultA.get().getIpAddresses();
            System.out.printf("Name:  %s\n", domainName);
            for (var ip : ipAddresses) {
                System.out.printf("Address:  %s\n", ip);
            }
            return resultA;
        } else {
            var resultCNAME = this.lookup(domainName, HeaderFlags.QTYPE_CNAME, additionalFlags);
            if (resultCNAME.isPresent()) {
                var cname = resultCNAME.get().getCName();
                if (cname.isPresent()) {
                    System.out.printf("%s\tcanonical name = %s\n", domainName, cname.get());
                    return this.resolve(cname.get(), additionalFlags);
                }
            }
        }
        return Optional.empty();
    }

    public static class Name {
        private static final Logger _logger = Logger.getLogger(Name.class.getName());

        private final String name;
        private final String ipAddress;

        public static Name fromName(String name) {
            return OctetHelper.isValidIPAddress(name) ? fromIpAddress(name) : new Name(name, null);
        }
        public static Name fromName(String name, String ipAddress) {
            return new Name(name, ipAddress);
        }
        public static Name fromIpAddress(String ipAddress) {
            return new Name(null, ipAddress);
        }
        private Name(String name, String ipAddress) {
            this.name = name;
            this.ipAddress = ipAddress;
        }

        public static Name localhostDnsLoopback() {
            return fromIpAddress("127.0.0.53");
        }

        public static Name locoalDns() {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
            /* https://github.com/dnsjava/dnsjava/blob/master/src/main/java/org/xbill/DNS/config/JndiContextResolverConfigProvider.java */
            try {
                DirContext ctx = new InitialDirContext(env);
                String servers = (String) ctx.getEnvironment().get("java.naming.provider.url");
                ctx.close();
                StringTokenizer st = new StringTokenizer(servers, " ");
                while (st.hasMoreTokens()) {
                    String server = st.nextToken();
                    try {
                        URI serverUri = new URI(server);
                        String host = serverUri.getHost();
                        if (host == null || host.isEmpty()) {
                            // skip the fallback server to localhost
                            continue;
                        }
                        return OctetHelper.isValidIPAddress(host) ? Name.fromIpAddress(host) : Name.fromName(host);
                    } catch (URISyntaxException e) {
                        _logger.info(String.format("Could not parse %s as a dns server", server));
                    }
                }
            } catch (NamingException e) {
                _logger.info("Error occured while parsing DNS environment");
            }
            return localhostDnsLoopback();
        }

        @Override
        public String toString() {
            if (this.name != null && this.ipAddress != null) {
                return String.format("[ %s (%s) ]", this.name, this.ipAddress);
            } else {
                return String.format("[ %s ]", this.getSearchValue());
            }
        }

        public String getSearchValue() {
            return this.ipAddress != null ? this.ipAddress : this.name;
        }

        public String getCompareValue() {
            return this.name != null ? this.name : this.ipAddress;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Name name1 = (Name) o;
            return Objects.equals(this.getCompareValue(), name1.getCompareValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getCompareValue());
        }
    }

    @SuppressWarnings("unused")
    public enum Verbose {
        NONE(0), FINE(1), FINER(2);

        private final int value;

        Verbose(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Verbose fromValue(int value) {
            for (Verbose level : Verbose.values()) {
                if (level.value == value) {
                    return level;
                }
            }
            throw new IllegalArgumentException("No matching enum value for " + value);
        }
        public static Verbose fromValue(boolean value) {
            return value ? FINE : NONE;
        }
    }
}
