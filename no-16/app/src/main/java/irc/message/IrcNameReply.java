package irc.message;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IIrcSenderProtocol;

public class IrcNameReply implements IrcMessage {

    private static final Pattern NAME_REPLY_PATTERN = Pattern.compile(
            "^:(?<hostname>[^ ]+) (?<command>([^ ]+)) (?<nickname>[^ ]+) (?<channeltype>[=*@]) (?<channel>([^ ]+) )?:(?<content>.+)$");
    private String hostName;
    private String command;
    private String nickName;
    private String channelType;
    private String channel;
    private String content;

    public static Optional<IrcNameReply> parseNameReplyMsg(String response) {
        Matcher matcher = NAME_REPLY_PATTERN.matcher(response);
        if (matcher.matches()) {
            String hostName = matcher.group("hostname");
            String command = matcher.group("command");
            String nickName = matcher.group("nickname");
            String channelType = matcher.group("channeltype");
            String channel = matcher.group("channel");
            String content = matcher.group("content");
            return Optional.of(new IrcNameReply(hostName, command, nickName, channelType, channel, content));
        } else {
            return Optional.empty();
        }
    }

    public IrcNameReply(String hostName, String command, String nickName, String channelType, String channel,
            String content) {
        this.hostName = hostName;
        this.command = command;
        this.nickName = nickName;
        this.channelType = channelType;
        this.channel = channel;
        this.content = content;
    }

    @Override
    public void handle(IIrcSenderProtocol sender) throws IOException {

    }
}
