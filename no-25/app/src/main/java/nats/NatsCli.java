package nats;

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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import nats.protocol.commands.ICmd;

public class NatsCli {
    private static final Logger _logger = Logger.getLogger(NatsCli.class.getName());

    private String hostName;
    private int port;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean started;

    private Thread readerThread;

    public NatsCli(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        this.started = false;
    }

    public NatsCli command(String command) {
        if (_logger.isLoggable(Level.INFO))
            _logger.info(String.format("%s:%d> %s", this.hostName, this.port, command));
        return this;
    }

    public NatsCli start() {
        if (!this.started) {

            try {
                this.socket = new Socket(this.hostName, this.port);
                this.reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream()),
                        StandardCharsets.UTF_8));
                this.writer = new BufferedWriter(new OutputStreamWriter(
                        new BufferedOutputStream(socket.getOutputStream()), StandardCharsets.UTF_8));
                if (_logger.isLoggable(Level.INFO))
                    _logger.info(String.format("Server %s:%d connected", this.hostName, this.port));
                this.started = true;
                return this;
            } catch (IOException e) {
                if (_logger.isLoggable(Level.SEVERE))
                    _logger
                            .severe(String.format("Server %s:%d not started: %s", this.hostName, this.port,
                                    e.getMessage()));
                this.started = false;
                this.socket = null;
                this.reader = null;
                this.writer = null;
            }
        } else {
            if (_logger.isLoggable(Level.INFO))
                _logger.info(String.format("Server %s:%d already connected", this.hostName, this.port));
        }
        return this;
    }

    public void stop() {
        if (this.readerThread != null) {
            this.started = false;
            this.readerThread = null;
        }
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

    public void sendCommand(ICmd command) throws IOException {
        var msgToSend = command.print();
        if (msgToSend.isPresent()) {
            this.sendCommand(msgToSend.get());
        }
    }

    public void sendCommand(String command) throws IOException {
        if (!command.endsWith(ICmd.CRLF)) {
            command = command + ICmd.CRLF;
        }
        this.writer.append(command);
        this.writer.flush();
    }

    public Optional<String> readFromConsole() throws IOException {
        while (System.in.available() > 0) {
            System.in.read();
        }
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) {
            return Optional.of(scanner.nextLine());
        } else {
            return Optional.empty();
        }
    }

    public NatsCli receive() {
        this.readerThread = new Thread(() -> {
            while (!this.started) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            while (this.started) {
                try {
                    if (this.reader.ready()) {
                        System.out.println(this.reader.readLine());
                    }
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        this.readerThread.start();
        return this;
    }

    public NatsCli startAndInput() throws IOException {
        this.receive();
        this.start();
        if (this.started) {
            var input = this.readFromConsole();
            while (input.isPresent() && !input.get().equalsIgnoreCase("quit")) {
                this.sendCommand(input.get());
                input = this.readFromConsole();
            }
        }
        return this;
    }
}
