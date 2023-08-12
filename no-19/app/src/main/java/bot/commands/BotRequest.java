package bot.commands;

import java.lang.reflect.Array;
import java.util.Arrays;

import discord4j.core.object.entity.Message;

public class BotRequest {

    private final Message message;
    private final String content;
    private final String contentLower;

    public BotRequest(Message message) {
        this.message = message;
        this.content = message.getContent();
        this.contentLower = content.toLowerCase();

    }

    public boolean isCommand(Cmd cmd) {
        return contentLower.startsWith(cmd.commandPrefix());
    }

    public Message message() {
        return this.message;
    }

    public String content() {
        return this.content;
    }

    public boolean isRealMessageFromAuthor() {
        var author = this.message().getAuthor();
        return (author.isPresent() && !(author.get().isBot()));

    }

    public String[] getTokens() {
        return this.content().split("\\s");
    }

    public String getCommand() {
        return getTokens()[0];
    }

    public String[] getParameters() {
        var tokens = this.getTokens();
        return Arrays.copyOfRange(tokens, 1, tokens.length);
        
    }

}
