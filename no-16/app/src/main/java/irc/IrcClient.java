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

public class IrcClient implements IIrcSenderProtocol {

    static final Logger _logger = Logger.getLogger(IrcClient.class.getName());

    private String hostname;
    private int port;
    private String nickName;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String channel;

    private IIrcMessageProcotol handler;

    public IrcClient(String hostname, int port, String nickName, String channel) {
        this.hostname = hostname;
        this.port = port;
        this.nickName = nickName;
        this.channel = channel;
        this.handler = new IrcProtocolHandler(this);
    }

    public void connect() throws UnknownHostException, IOException {
        this.socket = new Socket(this.hostname, this.port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        this.setNickName(this.nickName);
        this.joinChannel(this.channel);

        listen();
    }

    @Override
    public void sendRawMessage(String message) throws IOException {
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
                this.handler.receiveRawMessage(line);
            } else {
                _logger.warning("[empty message] received");
                this.disconnect();
            }
        }
    }

    @Override
    public void printMessage(String message) throws IOException {
        _logger.warning("[ msg to client ]: " + message);
    }

    @Override
    public void setNickName(String name) throws IOException {
        sendRawMessage("NICK " + name);
        sendRawMessage("USER guest 0 * :" + name);
    }

    @Override
    public void joinChannel(String channel) throws IOException {
        sendRawMessage("JOIN #" + this.channel);

    }
}
