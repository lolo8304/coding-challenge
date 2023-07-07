package irc.message;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IIrcSenderProtocol;

public class IrcPingMessage implements IrcMessage {

    private static final Pattern PING_PATTERN = Pattern.compile("^PING :([^: ]+)$");

    public static Optional<? extends IrcMessage> parsePing(String message) {
        Matcher matcher = PING_PATTERN.matcher(message);
        if (matcher.matches()) {
            return Optional.of(new IrcPingMessage(matcher.group(1)));
        }
        return Optional.empty();
    }

    private String token;

    public IrcPingMessage(String token) {
        this.token = token;
    }

    @Override
    public void handle(IIrcSenderProtocol sender) throws IOException {
        sender.sendRawMessage("PONG " + this.token);
    }
}