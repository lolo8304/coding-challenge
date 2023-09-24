package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONObject;

import nats.protocol.NatsLineParser;
import nats.runtime.NatsContext;

public class Connect implements ICmd {

    public boolean verbose;

    public Connect() {
        this.verbose = true;
    }

    public Connect(NatsLineParser line) {
        build(line);
    }

    public void build(NatsLineParser line) {
        var json = line.nextToken();
        if (json.isPresent()) {
            var parameters = new JSONObject(json.get().toString());
            if (parameters.has("verbose")) {
                this.verbose = parameters.getBoolean("verbose");
            }
        }
    }

    @Override
    public Optional<String> print() throws IOException {
        return Optional.of("CONNECT {} " + CRLF);
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        context.verbose(this.verbose);
        if (this.verbose) {
            return Optional.of("Ok" + CRLF);
        }
        return Optional.empty();
    }

    public Connect buildVerbose() {
        this.verbose = true;
        return this;
    }

    public Connect buildNoVerbose() {
        this.verbose = false;
        return this;
    }
}
