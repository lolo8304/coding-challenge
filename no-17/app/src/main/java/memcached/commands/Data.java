package memcached.commands;

public class Data {
    public String data;

    public Data(String data) {
        this.data = data;
    }

    public void append(String appendData) {
        this.data = data + appendData;
    }

    public void prepend(String prependData) {
        this.data = prependData + data;
    }

    public int length() {
        return this.data.length();
    }
}
