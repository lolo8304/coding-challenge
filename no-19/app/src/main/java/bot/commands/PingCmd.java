package bot.commands;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class PingCmd implements Cmd {

    @Override
    public String commandPrefix() {
        return "!ping";
    }

    @Override
    public void onMessage(Message message) {
        MessageChannel channel = message.getChannel().block();
        if (channel != null) {
            channel.createMessage("Pong!").block();
        }
    }

}
