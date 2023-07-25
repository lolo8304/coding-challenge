package memcached.server.handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import listener.Listener;
import listener.StringHandler;
import memcached.commands.Command;
import memcached.commands.CommandLine;
import memcached.commands.Data;
import memcached.commands.DataCommand;
import memcached.commands.Response;
import memcached.commands.SetCommand;
import memcached.commands.ValidationCode;
import memcached.commands.ValidationException;
import memcached.server.cache.MemCache;

public class MemcachedHandler extends StringHandler {

    private static final String RESPONSE_SET = "Response SET: ";
    private static final Logger _logger = Logger.getLogger(MemcachedHandler.class.getName());
    private static final String CLIENT_ERROR_BAD_DATA_CHUNK = "CLIENT_ERROR bad data chunk\r\n";
    private static final String END = "END";

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
                case "delete":
                    return this.deleteCommand(cmd);
                case "set":
                case "add":
                case "replace":
                case "cas":
                    var res = this.setCommand(clientSocketChannel, cmd);
                    return res.isPresent() ? Optional.of(res.get().toString() + "\r\n") : Optional.empty();
                case "incr":
                case "decr":
                    var res2 = this.setCommand(clientSocketChannel, cmd, false);
                    return res2.isPresent() ? Optional.of(res2.get().toString() + "\r\n") : Optional.empty();
                case "quit":
                    Listener._logger.info("client closing: " + clientSocketChannel.getRemoteAddress());
                    this.deregisterBuffer(clientSocketChannel);
                    clientSocketChannel.close();
                    return Optional.empty();

                default:
                    return Optional.of("ERROR - cmd '" + cmd.type + "' not implemented yet\r\n");
            }
        } catch (ValidationException e) {
            return Optional.of(CLIENT_ERROR_BAD_DATA_CHUNK);
        }
    }

    private Optional<String> getCommand(Command cmd) throws ValidationException {
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Request GET: " + cmd.toResponseString());
        }
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
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Response GET: " + responseString);
        }
        return Optional.of(responseString);
    }

    private Optional<String> deleteCommand(Command cmd) {
        if (_logger.isLoggable(Level.INFO)) {

            _logger.info("Request DELETE: " + cmd.toResponseString());
        }
        var response = new Response();
        response.finalNote = this.cache.deleteKey(cmd.key).toString();
        var responseString = response.toResponseString();
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Response DELETE: " + responseString);
        }
        return Optional.of(responseString);
    }

    private Optional<ValidationCode> setCommand(SocketChannel clientSocketChannel, Command cmd)
            throws IOException, ValidationException {
        return this.setCommand(clientSocketChannel, cmd, true);
    }

    private Optional<ValidationCode> setCommand(SocketChannel clientSocketChannel, Command cmd, boolean isReadingData)
            throws IOException, ValidationException {
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Request SET: " + cmd.toResponseString());
        }
        // read data from buffer
        SetCommand setCmd;
        if (isReadingData) {
            var data = this.readLineFromSocketChannel(clientSocketChannel);
            var dataCmd = new DataCommand(cmd.commandLine, new Data(data));
            setCmd = dataCmd.asSetCommand();
        } else {
            var dataCmd = new DataCommand(cmd.commandLine, (Data) null);
            setCmd = dataCmd.asSetCommand();
        }
        setCmd.validate();
        var responseAfterSet = this.cache.set(setCmd);
        return getResultFromResponse(cmd, responseAfterSet);
    }

    private Optional<ValidationCode> getResultFromResponse(Command cmd, Optional<ValidationCode> responseAfterSet) {
        Optional<ValidationCode> result;
        if (responseAfterSet.isPresent()) {
            if (cmd.noreply()) {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info(RESPONSE_SET + "no reply");
                }
                result = Optional.empty();
            } else {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info(RESPONSE_SET + responseAfterSet.get());
                }
                result = Optional.of(responseAfterSet.get());
            }
        } else {
            if (_logger.isLoggable(Level.INFO)) {
                _logger.info(RESPONSE_SET + ValidationCode.NOT_STORED);
            }
            result = Optional.of(ValidationCode.NOT_STORED);
        }
        return result;
    }

}
