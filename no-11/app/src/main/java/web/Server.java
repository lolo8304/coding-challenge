package web;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import web.http.Http11Handler;

@Command(name = "webserver", mixinStandardHelpOptions = true, version = "web 1.0", description = "This challenge is to build your own webserver based on HTTP1.1")
public class Server implements Callable<Result> {

    public static void main(String[] args) {
        var server = new Server();
        var cmd = new CommandLine(server);
        var exitCode = cmd.execute(args);
        cmd.getExecutionResult();
        System.exit(exitCode);
    }

    @Option(names = "-p", description = "-p specifies the port - default 8080")
    int port = 8080;

    @Option(names = "-webRoot", description = "-webRoot specifies the default web root folder")
    String webRoot = "./www";

    @Override
    public Result call() throws Exception {
        new Listener(this.port, new Http11Handler(webRoot));
        return new Result();
    }
}