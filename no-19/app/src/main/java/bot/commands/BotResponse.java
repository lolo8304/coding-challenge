package bot.commands;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class BotResponse {

    private Message message;

    public BotResponse(Message message) {
        this.message = message;

    }

    public void sendTextMessage(String content) {
        MessageChannel channel = message.getChannel().block();
        if (channel != null) {
            channel.createMessage(content).block();
        }
    }
}
