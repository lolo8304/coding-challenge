package bot.commands;

import discord4j.core.object.entity.Message;

public interface Cmd {

    String commandPrefix();

    void onMessage(BotRequest request, BotResponse response);

}