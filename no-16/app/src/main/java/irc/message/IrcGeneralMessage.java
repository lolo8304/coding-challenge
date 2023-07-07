package irc.message;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IIrcSenderProtocol;

public class IrcGeneralMessage implements IrcMessage {
    private static final String WELCOME = "001";
    private static final String YOUR_HOST = "002";
    private static final String CREATED = "003";
    private static final String MOTD_START = "375";
    private static final String MOTD = "372";
    private static final String MOTD_END = "376";
    private static final String QUIT = "QUIT";
    private static final String JOIN = "JOIN";
    private static final String NICK = "NICK";
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "^:(?>(?<sender>[^!]+)!(?<nickname>[^@]+)@)?(?<hostname>[^ ]+) ((?<command>([^ ]+)) )?((?<target>([^ ]+)) )?:(?<content>.+)$");

    public static Optional<IrcGeneralMessage> parseGeneralMsg(String message) {
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        if (matcher.matches()) {
            String sender = matcher.group("sender");
            String nickName = matcher.group("nickname");
            String hostName = matcher.group("hostname");
            String command = matcher.group("command");
            String target = matcher.group("target");
            String content = matcher.group("content");
            return Optional.of(new IrcGeneralMessage(sender, nickName, hostName, command, target, content));
        } else {
            return Optional.empty();
        }
    }

    public final String sender;
    public final String nickName;
    public final String hostName;
    public final String command;
    public final String target;
    public final String content;

    public IrcGeneralMessage(String sender, String nickName, String hostName, String command, String target,
            String content) {
        this.sender = sender;
        this.nickName = nickName;
        this.hostName = hostName;
        this.command = command;
        this.target = target;
        this.content = content;

    }

    @Override
    public void handle(IIrcSenderProtocol sender) throws IOException {
        switch (this.command) {
            case NICK:
                sender.printMessage(String.format("%s is now known as %s", this.sender, this.content));
                break;
            case JOIN:
                sender.printMessage(String.format("%s has joined %s", this.sender, this.content));
                break;
            case QUIT:
                sender.printMessage(String.format("%s has left IRC %s", this.sender, this.content));
                break;

            case MOTD_START:
                break;
            case MOTD_END:
                break;
            case MOTD:
            case WELCOME:
            case CREATED:
            case YOUR_HOST:
                sender.printMessage(String.format("%s", this.content));
                break;

            default:
                sender.printMessage(String.format("Cmd '%s' missing: %s", this.command, this.content));
                break;
        }
    }

}
