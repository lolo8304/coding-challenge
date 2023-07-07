package irc.message;

import java.io.IOException;

import irc.IIrcSenderProtocol;

public interface IrcMessage {

    public void handle(IIrcSenderProtocol sender) throws IOException;
}