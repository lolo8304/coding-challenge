package jq;

import java.io.InputStream;

public class SystemIn implements Input {

    private InputStream stream;

    public SystemIn() {
        this.stream = System.in;
    }
    @Override
    public boolean hasNext() {
        return this.stream != null;
    }

    @Override
    public InputStream next() {
        var in = this.stream;
        this.stream = null;
        return in;
    }
}
