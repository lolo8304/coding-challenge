package memcached.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import memcached.commands.Command;
import memcached.commands.DataCommand;
import memcached.commands.Response;

public class MemcachedClient {
    static final Logger _logger = Logger.getLogger(MemcachedClient.class.getName());

    private final String[] serverIds;
    private final ServerConfiguration[] servers;
    private final List<ServerConfiguration> validServers = new ArrayList<>();

    public MemcachedClient(String serverIds) {
        this.serverIds = Arrays.asList(serverIds.split(",")).stream().filter(x -> !x.isEmpty() && !x.isBlank())
                .toArray(String[]::new);
        this.servers = initializeServers();
    }

    public MemcachedClient(String[] serverIds) {
        this.serverIds = serverIds;
        this.servers = initializeServers();
    }

    private ServerConfiguration[] initializeServers() {
        return Arrays.asList(this.serverIds).stream().map(ServerConfiguration::new).toArray(ServerConfiguration[]::new);
    }

    public String readFromConsole() {
        System.out.print("> ");
        try (Scanner scanner = new Scanner(System.in);) {
            return scanner.nextLine();
        }
    }

    public boolean start() {
        for (ServerConfiguration server : this.servers) {
            if (server.start()) {
                validServers.add(server);
            }
        }
        return !this.validServers.isEmpty();
    }

    public void stop() {
        this.validServers.forEach(ServerConfiguration::stop);
    }

    public void close() {
        this.stop();
    }

    public ServerConfiguration[] getServers() {
        return this.servers;
    }

    public ServerConfiguration[] getValidServers() {
        return this.validServers.toArray(ServerConfiguration[]::new);
    }

    public Optional<Response> sendCommand(Command command) {
        for (ServerConfiguration server : this.validServers) {
            try {
                return server.sendAndReceive(command);
            } catch (IOException e) {
                System.out.println("error response: " + e.getMessage());
                return Optional.of(new Response("ERROR"));
            }
        }
        return Optional.of(new Response("NO_SERVER_CONNECTED"));
    }

    public Optional<Response> sendCommand(String command) {
        return this.sendCommand(Command.parse(command));
    }

    public Optional<Response> sendCommand(String command, String data) {
        return this.sendCommand(DataCommand.parse(command, data));
    }

    public void startAndInput() {
        if (this.start()) {
            var input = this.readFromConsole();
            while (input != null && !input.equalsIgnoreCase("quit")) {
                var response = this.sendCommand(input);
                if (response.isPresent()) {
                    System.out.println(response.get().toResponseString());
                }
                input = this.readFromConsole();
            }
        }
    }
}
