package bot.commands;

import discord4j.core.object.entity.Message;

public class HelloCmd implements Cmd {

    @Override
    public String commandPrefix() {
        return "!hello";
    }

    @Override
    public void onMessage(BotRequest request, BotResponse response) {
        response.sendTextMessage("Hello, world!");
    }

}
