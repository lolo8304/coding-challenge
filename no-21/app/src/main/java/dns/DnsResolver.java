package dns;

import java.util.Optional;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@SuppressWarnings("CanBeFinal")
@Command(name = "DnsResolver", mixinStandardHelpOptions = true, version = "dnsResolve 1.0", description = "dns resolve a domain name to IP")
public class DnsResolver implements Callable<Result<DnsMessage>> {

    public static void main(String[] args) {
        var dnsResolve = new DnsResolver();
        var cmd = new CommandLine(dnsResolve);
        var exitCode = cmd.execute(args);
        Result<DnsMessage> result = cmd.getExecutionResult();
        if (result != null) {
            if (result.hasMessage()) {
                System.out.println();
            } else {
                System.out.printf("** server can't find %s: NXDOMAIN\n", dnsResolve.domain);
            }
        }
        System.exit(exitCode);
    }

    @Option(names = "-d", arity = "1..1", description = "-d specifies the domain name")
    String domain = null;

    @Option(names = "-dns", description = "-dns specifies the DNS server")
    String dnsServer = null;
    DnsServer.Name dnsServerName = null;

    @Option(names = "-p", description = "-p specifies the port of the dns server")
    int port = 53;

    @Option(names = "-type", description = "-type specifies the type of request. default all. possible: a, cname, txt, mx, ns, all")
    String typeString = "ALL";
    int type = HeaderFlags.QTYPE_ALL;

    @Option(names = "-norecurse",  arity = "0..", description = "-norecurse specifies to not use ")
    boolean norecurse = false;

    @Option(names = "-v",  arity = "0..", description = "-v specifies to output verbose information with level FINE")
    boolean vFlag = false;
    @Option(names = "-vv",  arity = "0..", description = "-vv specifies to output verbose information with level FINER")
    boolean vvFlag = false;

    @Option(names = "-root",  arity = "0..", description = "-root specifies to use a root server")
    boolean useRootServer = false;

    DnsServer.Verbose verbose = DnsServer.Verbose.NONE;

    @Override
    public Result<DnsMessage> call() throws Exception {
        init();
        var recurseFlag = this.norecurse ? Flags.RECURSION_DESIRED_OFF : Flags.RECURSION_DESIRED;
        Optional<DnsMessage> result;
        System.out.printf("Server: \t%s\n", this.dnsServerName.getSearchValue());
        System.out.printf("Address:\t%s#%d\n\n", this.dnsServerName.getSearchValue(), this.port);

        if (this.type == HeaderFlags.QTYPE_ALL) {
            System.out.println("Non-authoritative answer:");
            result = new DnsServer(this.dnsServerName, port, this.verbose).resolve(domain, recurseFlag);
        } else{
            result = new DnsServer(this.dnsServerName, port, this.verbose).lookup(domain, this.type, recurseFlag);
        }
        return result.<Result<DnsMessage>>map(Result::new).orElseGet(Result::new);
    }

    private void init() {
        switch (this.typeString.toUpperCase()) {
            case "A": this.type = HeaderFlags.QTYPE_A;break;
            case "CNAME": this.type = HeaderFlags.QTYPE_CNAME;break;
            case "TXT": this.type = HeaderFlags.QTYPE_TXT;break;
            case "NS": this.type = HeaderFlags.QTYPE_NS;break;
            case "SOA": this.type = HeaderFlags.QTYPE_SOA;break;
            case "ALL": this.type = HeaderFlags.QTYPE_ALL;break;
            default:
                System.out.printf("Type '%s' invalid, using default A%n\n", this.typeString);
                this.type = HeaderFlags.QTYPE_A;break;
        }

        this.dnsServerName = DnsServer.Name.localhostDnsLoopback();
        if (this.useRootServer) {
            this.dnsServerName = DnsServer.randomRootServer();
        }
        if (dnsServer !=null && !dnsServer.isBlank()) {
            if (OctetHelper.isValidIPAddress(dnsServer)) {
                this.dnsServerName = DnsServer.Name.fromIpAddress(dnsServer);
            } else {
                this.dnsServerName = DnsServer.Name.fromName(dnsServer);
            }
        }
        if (vvFlag) {
            this.verbose = DnsServer.Verbose.FINER;
        } else {
            this.verbose= DnsServer.Verbose.fromValue(vFlag);
        }
    }
}