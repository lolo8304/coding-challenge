package jq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
