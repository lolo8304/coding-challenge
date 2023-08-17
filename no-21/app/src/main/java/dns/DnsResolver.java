package dns;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@SuppressWarnings("CanBeFinal")
@Command(name = "DnsResolver", mixinStandardHelpOptions = true, version = "dnsResolve 1.0", description = "dns resolve a domain name to IP")
public class DnsResolver implements Callable<Result<String>> {

    public static void main(String[] args) {
        var dnsResolve = new DnsResolver();
        var cmd = new CommandLine(dnsResolve);
        var exitCode = cmd.execute(args);
        Result<String> result = cmd.getExecutionResult();
        if (result != null) {
            System.out.println(result);
        }
        System.exit(exitCode);
    }

    @Option(names = "-d", description = "-d specifies the domain name")
    String domain = null;

    @Option(names = "-dns", description = "-dns specifies the DNS server")
    String dnsServer = "8.8.8.8";

    @Option(names = "-p", description = "-p specifies the port of the dns server")
    int port = 53;

    @Override
    public Result<String> call() throws Exception {
        var msg = new DnsMessage();
        msg.addQuestion(new DnsQuestion(this.domain));
        return new Result<>(msg.send(this.dnsServer, this.port));
    }
}