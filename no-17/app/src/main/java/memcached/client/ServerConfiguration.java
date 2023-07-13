package memcached.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import memcached.commands.Command;
import memcached.commands.CommandLine;
import memcached.commands.Data;
import memcached.commands.DataCommand;
import memcached.commands.Response;

public class ServerConfiguration {
    public final String hostName;
    public final int port;
    public final String serverId;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean started;

    public ServerConfiguration(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        this.serverId = String.format("%s:%d", this.hostName, this.port);
        this.started = false;
    }

    public ServerConfiguration(String serverId) {
        var splitted = serverId.split(":");
        this.hostName = splitted[0];
        this.port = Integer.parseInt(splitted[1]);
        this.serverId = serverId;
        this.started = false;
    }

    public boolean start() {
        if (!this.started) {

            try {
                this.socket = new Socket(this.hostName, this.port);
                this.reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream()),
                        StandardCharsets.UTF_8));
                this.writer = new BufferedWriter(new OutputStreamWriter(
                        new BufferedOutputStream(socket.getOutputStream()), StandardCharsets.UTF_8));
                MemcachedClient._logger.info(String.format("Server %s connected", this.serverId));
                this.started = true;
                return true;
            } catch (IOException e) {
                MemcachedClient._logger
                        .severe(String.format("Server %s not started: %s", this.serverId, e.getMessage()));
                this.started = false;
                this.socket = null;
                this.reader = null;
                this.writer = null;
            }
        } else {
            MemcachedClient._logger.info(String.format("Server %s already connected", this.serverId));
        }
        return this.started;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void stop() {
        try {
            this.socket.close();
        } catch (IOException e) {
        }
        try {
            this.reader.close();
        } catch (IOException e) {
        }
        try {
            this.writer.close();
        } catch (IOException e) {
        }
    }

    public void send(Command command) throws IOException {
        command.write(this.writer);
        this.writer.flush();
    }

    public Optional<Response> receive(Command command) throws IOException {
        if (!command.noreply()) {
            String line = this.reader.readLine();
            var response = new Response();
            while (line != null) {
                if (line.startsWith("VALUE")) {
                    response.addValue(this.receiveValue(line));
                } else {
                    switch (line) {
                        case "STORED":
                        case "NOT_STORED":
                        case "EXISTS":
                        case "NOT_FOUND":
                        case "DELETED":
                        case "ERROR":
                        case "CLIENT_ERROR":
                        case "SERVER_ERROR":
                        case "END":
                            response.finalNote = line;
                            break;

                        default:
                            break;
                    }
                }
                if (reader.ready()) {
                    line = this.reader.readLine();
                } else {
                    line = null;
                }
            }
            return Optional.of(response);
        } else {
            return Optional.empty();
        }
    }

    private DataCommand receiveValue(String line) throws IOException {
        var data = this.reader.readLine();
        return new DataCommand(new CommandLine(line), new Data(data));
    }

    public Optional<Response> sendAndReceive(Command command) throws IOException {
        this.send(command);
        return this.receive(command);
    }

}