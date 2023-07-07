package irc.handler;

import java.io.IOException;
import java.util.Optional;

import irc.IIrcMessageProcotol;
import irc.IIrcSenderProtocol;
import irc.message.IrcGeneralMessage;
import irc.message.IrcMessage;
import irc.message.IrcNameReply;
import irc.message.IrcPingMessage;
import irc.message.IrcPrivMessage;

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
        var msg = this.parseMessage(message);
        if (msg.isPresent()) {
            msg.get().handle(this.senderProtocol);
        }
    }

    @Override
    public Optional<? extends IrcMessage> parseMessage(String message) throws IOException {
        var msgPing = IrcPingMessage.parsePing(message);
        if (msgPing.isPresent()) {
            return msgPing;
        }
        var msgPriv = IrcPrivMessage.parsePrivMsg(message);
        if (msgPriv.isPresent()) {
            return msgPriv;
        }
        var msgName = IrcNameReply.parseNameReplyMsg(message);
        if (msgName.isPresent()) {
            return msgName;
        }
        var msgGeneral = IrcGeneralMessage.parseGeneralMsg(message);
        if (msgGeneral.isPresent()) {
            return msgGeneral;
        }
        return Optional.empty();
    }

}
