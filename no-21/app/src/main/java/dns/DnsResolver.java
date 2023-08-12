package dns;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "DnsResolver", mixinStandardHelpOptions = true, version = "dnsResolve 1.0",
description = "dns resolve a domain name to IP")
public class DnsResolver implements Callable<Result<String>>{

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


    @Override
    public Result<String> call() throws Exception {
        return new Result<String>("0.0.0.0");
    }
}