package memcached.listener;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import listener.Listener;
import listener.StringHandler;
import memcached.commands.Command;
import memcached.commands.CommandLine;
import memcached.commands.Data;
import memcached.commands.DataCommand;
import memcached.commands.Response;
import memcached.listener.cache.MemCache;

public class MemcachedHandler extends StringHandler {

    private MemCache cache;

    public MemcachedHandler() {
        this.cache = new MemCache();
    }

    @Override
    public Optional<String> request(SocketChannel clientSocketChannel, String line) throws IOException {
        var cmd = new Command(new CommandLine(line));
        switch (cmd.type) {
            case "get":
                return this.getCommand(cmd);
            case "set":
                return this.setCommand(clientSocketChannel, cmd);
            case "quit":
                Listener._logger.info("client closing: " + clientSocketChannel.getRemoteAddress());
                clientSocketChannel.close();
                return Optional.empty();

            default:
                return Optional.of("ERROR - cmd '" + cmd.type + "' not implemented yet");
        }
    }

    private Optional<String> getCommand(Command cmd) {
        var response = new Response();
        var getCmd = cmd.asGetCommand();
        for (String key : getCmd.keys) {
            var data = this.cache.get(key);
            if (data.isPresent()) {
                var dataCmd = data.get();
                response.addValue(dataCmd.asValueCommand());
            }
        }
        response.finalNote = "END";
        return Optional.of(response.toResponseString());
    }

    private Optional<String> setCommand(SocketChannel clientSocketChannel, Command cmd) throws IOException {
        // read data from buffer
        var data = this.readLineFromSocketChannel(clientSocketChannel);
        var dataCmd = new DataCommand(cmd.commandLine, new Data(data));
        this.cache.set(dataCmd);
        if (dataCmd.hasNoReply()) {
            return Optional.empty();
        } else {
            return Optional.of("STORED" + '\r' + '\n');
        }
    }

}
