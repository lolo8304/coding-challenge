package bot.commands;

import discord4j.core.object.entity.Message;

public class PingCmd implements Cmd {

    @Override
    public String commandPrefix() {
        return "!ping";
    }

    @Override
    public void onMessage(BotRequest request, BotResponse response) {
        response.sendTextMessage("Pong!");
    }

}
