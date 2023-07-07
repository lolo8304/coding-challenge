package irc.message;

import java.io.IOException;
import java.util.Optional;

import irc.IIrcSenderProtocol;

public class IrcPrivMessage extends IrcGeneralMessage {

    public static Optional<IrcPrivMessage> parsePrivMsg(String response) {
        Optional<IrcGeneralMessage> msg = IrcGeneralMessage.parseGeneralMsg(response);
        if (msg.isPresent()) {
            var m = msg.get();
            if (m.command.equals("PRIVMSG")) {
                return Optional
                        .of(new IrcPrivMessage(m.sender, m.nickName, m.hostName, m.command, m.target, m.content));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public IrcPrivMessage(String sender, String nickName, String hostName, String command, String target,
            String content) {
        super(sender, nickName, hostName, command, target, content);
    }

    @Override
    public void handle(IIrcSenderProtocol sender) throws IOException {
        sender.printMessage(String.format("%s: %s", this.sender, this.content));
    }
}
