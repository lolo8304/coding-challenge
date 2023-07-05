package irc;

import java.io.IOException;

public class IrcProtocolHandler implements IIrcMessageProcotol {
    private IIrcSenderProtocol senderProtocol;

    public IrcProtocolHandler(IIrcSenderProtocol senderProtocol) {
        this.senderProtocol = senderProtocol;
    }

    @Override
    public IIrcSenderProtocol getMessageSender() {
        return this.senderProtocol;
    }

    @Override
    public void receiveRawMessage(String message) throws IOException {
        if (message.startsWith("PING")) {
            this.handlePing(message);
        } else if (message.contains("PRIVMSG")) {
            this.handlePrivateMessage(message);
        } else {
            this.handleMessage(message);
        }
    }

    @Override
    public void handlePing(String message) throws IOException {
        String pingKey = message.substring(5);
        this.getMessageSender().sendRawMessage("PONG " + pingKey);
    }

    @Override
    public void handlePrivateMessage(String message) throws IOException {
        String[] parts = message.split(" :", 2);
        String sender = parts[0].substring(1, parts[0].indexOf('!'));
        String textMessage = parts[1];
        this.getMessageSender().printMessage("[" + sender + "] " + textMessage);
    }

    @Override
    public void handleMessage(String message) throws IOException {
        this.getMessageSender().printMessage(message);
    }

}
