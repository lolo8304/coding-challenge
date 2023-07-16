package memcached.server.handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.logging.Logger;

import listener.Listener;
import listener.StringHandler;
import memcached.commands.Command;
import memcached.commands.CommandLine;
import memcached.commands.Data;
import memcached.commands.DataCommand;
import memcached.commands.Response;
import memcached.commands.ValidationException;
import memcached.server.cache.MemCache;

public class MemcachedHandler extends StringHandler {

    private static final Logger _logger = Logger.getLogger(MemcachedHandler.class.getName());
    private static final String CLIENT_ERROR_BAD_DATA_CHUNK = "CLIENT_ERROR bad data chunk";
    private static final String END = "END";
    private static final String STORED = "STORED" + '\r' + '\n';

    private MemCache cache;

    public MemcachedHandler() {
        this.cache = new MemCache();
    }

    @Override
    public Optional<String> request(SocketChannel clientSocketChannel, String line) throws IOException {
        var cmd = new Command(new CommandLine(line));
        try {
            switch (cmd.type) {
                case "get":
                    return this.getCommand(cmd);
                case "set":
                    return this.setCommand(clientSocketChannel, cmd);
                case "quit":
                    Listener._logger.info("client closing: " + clientSocketChannel.getRemoteAddress());
                    this.deregisterBuffer(clientSocketChannel);
                    clientSocketChannel.close();
                    return Optional.empty();

                default:
                    return Optional.of("ERROR - cmd '" + cmd.type + "' not implemented yet");
            }
        } catch (ValidationException e) {
            return Optional.of(CLIENT_ERROR_BAD_DATA_CHUNK);
        }
    }

    private Optional<String> getCommand(Command cmd) throws ValidationException {
        _logger.info("Request GET: " + cmd.toResponseString());
        var response = new Response();
        var getCmd = cmd.asGetCommand();
        getCmd.validate();
        for (String key : getCmd.keys) {
            var data = this.cache.get(key);
            if (data.isPresent()) {
                var dataCmd = data.get();
                response.addValue(dataCmd.asValueCommand());
            }
        }
        response.finalNote = END;
        var responseString = response.toResponseString();
        _logger.info("Response GET: " + responseString);
        return Optional.of(responseString);
    }

    private Optional<String> setCommand(SocketChannel clientSocketChannel, Command cmd)
            throws IOException, ValidationException {
        _logger.info("Request SET: " + cmd.toResponseString());
        // read data from buffer
        var data = this.readLineFromSocketChannel(clientSocketChannel);
        var dataCmd = new DataCommand(cmd.commandLine, new Data(data));
        var setCmd = dataCmd.asSetCommand();
        setCmd.validate();
        this.cache.set(setCmd);
        if (dataCmd.noreply()) {
            _logger.info("Response SET: " + "no reply");
            return Optional.empty();
        } else {
            _logger.info("Response SET: " + STORED);
            return Optional.of(STORED);
        }
    }

}
