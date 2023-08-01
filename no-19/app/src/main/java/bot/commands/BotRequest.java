package bot.commands;

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

    public boolean isRealMessageFromAuthor(Message message) {
        var author = message.getAuthor();
        return (author.isPresent() && !(author.get().isBot()));

    }

}
