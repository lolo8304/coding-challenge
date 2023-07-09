package memcached.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

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
    public Optional<String> request(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            var cmd = new Command(new CommandLine(line));
            switch (cmd.type) {
                case "get":
                    return this.getCommand(cmd);
                case "set":
                    return this.setCommand(cmd, bufferedReader);

                default:
                    return Optional.of("ERROR - cmd '" + cmd.type + "' not implemented yet");
            }
        }
        return Optional.empty();
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

    private Optional<String> setCommand(Command cmd, BufferedReader bufferedReader) throws IOException {
        // read data from buffer
        var data = bufferedReader.readLine();
        var dataCmd = new DataCommand(cmd.commandLine, new Data(data));
        this.cache.set(dataCmd);
        if (dataCmd.hasNoReply()) {
            return Optional.empty();
        } else {
            return Optional.of("STORED" + '\r' + '\n');
        }
    }

}
