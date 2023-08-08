package bot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import bot.commands.BotRequest;
import bot.commands.BotResponse;
import bot.commands.ChallengeCmd;
import bot.commands.Cmd;
import bot.commands.HelloCmd;
import bot.commands.PingCmd;
import bot.commands.QuotesCmd;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

public class Bot {
    public static final Logger _logger = Logger.getLogger(Bot.class.getName());

    private final Map<String, Cmd> commands;

    public Bot() {
        this.commands = new HashMap<String, Cmd>();
        this.registerCommands();

    }

    public void start(String token) {
        final GatewayDiscordClient gateway = DiscordClientBuilder.create(token).build()
                .login()
                .block();
        registerEvents(gateway);
        gateway.onDisconnect().block();
    }

    private void registerCommand(Cmd cmd) {
        this.commands.put(cmd.commandPrefix().toLowerCase(), cmd);

    }

    private void registerCommands() {
        this.registerCommand(new PingCmd());
        this.registerCommand(new HelloCmd());
        this.registerCommand(new QuotesCmd());
        this.registerCommand(new ChallengeCmd());
    }

    private Optional<Cmd> getCommandBy(BotRequest request) {
        var message = request.message();
        if (this.isRealMessageFromAuthor(message)) {
            var entries = this.commands.entrySet();
            var content = message.getContent();
            for (Map.Entry<String, Cmd> cmdEntry : entries) {
                if (content.equals(cmdEntry.getKey())) {
                    return Optional.of(cmdEntry.getValue());
                }
                var splittedContent = content.split("\\s");
                if (splittedContent.length > 0 && splittedContent[0].equalsIgnoreCase(cmdEntry.getKey())) {
                    return Optional.of(cmdEntry.getValue());                    
                }
            }
        }
        return Optional.empty();

    }

    private boolean isRealMessageFromAuthor(Message message) {
        var author = message.getAuthor();
        return (author.isPresent() && !(author.get().isBot()));

    }

    private void registerEvents(GatewayDiscordClient gateway) {
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            Message message = event.getMessage();
            var request = new BotRequest(message);
            var response = new BotResponse(message);

            var cmd = this.getCommandBy(request);
            if (cmd.isPresent()) {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info(String.format("Message for '%s' arrived", cmd.get().commandPrefix()));
                }
                cmd.get().onMessage(request, response);
            }

        });
    }

}
