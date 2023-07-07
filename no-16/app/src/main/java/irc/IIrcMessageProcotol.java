package irc;

import java.io.IOException;
import java.util.Optional;

import irc.message.IrcMessage;

public interface IIrcMessageProcotol {

    public IIrcSenderProtocol getMessageSender();

    public void receiveRawMessage(String message) throws IOException;

    public Optional<? extends IrcMessage> parseMessage(String message) throws IOException;

}
