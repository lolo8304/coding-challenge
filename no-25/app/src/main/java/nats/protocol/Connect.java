package nats.protocol;

import java.io.IOException;
import java.util.Optional;

public class Connect implements ICmd {

    public boolean verbose;

    public Connect() {
        this.verbose = true;
    }

    @Override
    public Optional<String> send() throws IOException {
        return Optional.of("CONNECT {} " + EOL);
    }

    @Override
    public Optional<String> execute() throws IOException {
        if (this.verbose) {
            return Optional.of("Ok" + EOL);
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
