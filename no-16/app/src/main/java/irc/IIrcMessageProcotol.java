package irc;

import java.io.IOException;

public interface IIrcMessageProcotol {

    public IIrcSenderProtocol getMessageSender();

    public void receiveRawMessage(String message) throws IOException;

    public void handlePing(String message) throws IOException;

    public void handlePrivateMessage(String message) throws IOException;

    public void handleMessage(String message) throws IOException;

}
