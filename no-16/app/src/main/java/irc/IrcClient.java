package irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class IrcClient {

    static final Logger _logger = Logger.getLogger(IrcClient.class.getName());

    private String hostname;
    private int port;
    private String nickName;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String channel;

    public IrcClient(String hostname, int port, String nickName, String channel) {
        this.hostname = hostname;
        this.port = port;
        this.nickName = nickName;
        this.channel = channel;
    }

    public void connect() throws UnknownHostException, IOException {
        this.socket = new Socket(this.hostname, this.port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        sendRawMessage("NICK " + this.nickName);
        sendRawMessage("USER guest 0 * :" + this.nickName);
        sendRawMessage("JOIN #" + this.channel);

        listen();
    }

    private void sendRawMessage(String message) throws IOException {
        this.writer.write(message + "\r\n");
        this.writer.flush();
        _logger.info("SENT: " + message);
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public void disconnect() throws IOException {
        sendRawMessage("QUIT :Goodbye!");
        this.socket.close();
        reader = null;
        writer = null;
        socket = null;
    }

    public void listen() throws IOException {
        while (this.isConnected()) {
            var line = this.reader.readLine();
            if (line != null && !line.isEmpty()) {
                _logger.info("RECEIVED: " + line);
                if (line.startsWith("PING")) {
                    String pingKey = line.substring(5);
                    sendRawMessage("PONG " + pingKey);
                } else if (line.contains("PRIVMSG")) {
                    String[] parts = line.split(" :", 2);
                    String sender = parts[0].substring(1, parts[0].indexOf('!'));
                    String message = parts[1];
                    _logger.warning("[" + sender + "] " + message);
                }
            } else {
                _logger.warning("[empty message] received");
                this.disconnect();
            }
        }
    }
}
