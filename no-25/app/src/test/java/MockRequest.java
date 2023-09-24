import java.io.IOException;

import nats.protocol.NatsHandler;

public class MockRequest extends NatsHandler.Request {

    private String[] additionalLines;
    private int index;

    public MockRequest(String... lines) {
        super(null, null);
        this.additionalLines = lines;
        this.index = 0;
    }

    @Override
    public int clientId() {
        return -42;
    }

    @Override
    public String readNextLine() throws IOException {
        if (this.index < additionalLines.length) {
            return this.additionalLines[this.index++];
        } else {
            throw new IllegalCallerException("no more data available to read");
        }
    }

}
