package memcached.commands;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import memcached.client.MemcachedClient;
import memcached.server.cache.MemCache;

public class Command {
    public final String type;
    public final String key;
    public final String[] parameters;
    public final CommandLine commandLine;

    public static Command parse(String line) {
        return new Command(new CommandLine(line));
    }

    public Command(CommandLine commandLine) {
        this(commandLine, commandLine.getTokens());
    }

    public Command(CommandLine commandLine, String... tokens) {
        this.commandLine = commandLine;
        if (tokens.length > 0) {
            this.type = tokens[0].toLowerCase();
            if (tokens.length > 1) {
                this.key = tokens[1];
                this.parameters = Arrays.copyOfRange(tokens, 2, tokens.length);
            } else {
                this.key = "";
                this.parameters = new String[0];
            }
        } else {
            this.type = "";
            this.key = "";
            this.parameters = new String[0];
        }
    }

    public Command(String... tokens) {
        this(new CommandLine(tokens), tokens);
    }

    public String getLine() {
        return this.commandLine.line;
    }

    public void send(MemcachedClient client) {
        client.sendCommand(type);
    }

    public void write(BufferedWriter writer) throws IOException {
        writer.write(this.commandLine.line + '\r' + '\n');
    }

    public String toResponseString() {
        var buffer = new StringBuilder();
        buffer.append(this.commandLine.line);
        return buffer.toString();
    }

    public GetCommand asGetCommand() {
        return new GetCommand(this.commandLine);
    }

    public Optional<Integer> parameterInt(int index) {
        var par = this.parameter(index);
        if (par.isPresent()) {
            return Optional.of(Integer.parseInt(par.get()));
        } else {
            return Optional.empty();
        }
    }

    public void parameterInt(int index, int value) {
        this.parameter(index, String.valueOf(value));
    }

    public Optional<String> parameter(int index) {
        if (this.parameters != null && index >= 0 && index < this.parameters.length) {
            return Optional.of(this.parameters[index]);

        } else {
            return Optional.empty();
        }
    }

    public void parameter(int index, String value) {
        if (this.parameters != null && index >= 0 && index < this.parameters.length) {
            this.parameters[index] = value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public Optional<String> parameterLast() {
        return this.parameter(this.parameters.length - 1);
    }

    public Optional<String> parameter0() {
        return this.parameter(0);
    }

    public Optional<String> parameter1() {
        return this.parameter(1);
    }

    public int flags() {
        var f = this.parameterInt(0);
        if (f.isPresent()) {
            return f.get();
        } else {
            return 0;
        }
    }

    public int exptime() {
        var e = this.parameterInt(1);
        if (e.isPresent()) {
            return e.get();
        } else {
            return 0;
        }
    }

    public int bytes() {
        var e = this.parameterInt(2);
        if (e.isPresent()) {
            return e.get();
        } else {
            return 0;
        }
    }

    public void bytes(int len) {
        this.parameterInt(2, len);
    }

    public boolean noreply() {
        var noreply = this.parameterLast();
        return (noreply.isPresent() && noreply.get().equals("noreply"));
    }

    public void validate() throws ValidationException {

    }

    public ValidationCode isValidToAddToCache(MemCache cache) {
        return ValidationCode.OK;
    }

}
