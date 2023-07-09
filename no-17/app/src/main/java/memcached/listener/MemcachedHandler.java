package memcached.listener;

import java.io.BufferedReader;
import java.io.IOException;

import listener.StringHandler;

public class MemcachedHandler extends StringHandler {

    @Override
    public String request(BufferedReader bufferedReader) throws IOException {
        var buffer = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            buffer.append("RESULT:" + line);
            buffer.append("\r");
        }
        return buffer.toString();
    }

}
