package jq;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
    public Reader next() {
        var in = this.stream;
        this.stream = null;
        return new InputStreamReader(in);
    }
}
