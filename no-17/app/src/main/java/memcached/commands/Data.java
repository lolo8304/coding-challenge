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

    public void incr(String incrData) {
        var newValue = String.valueOf(Integer.parseInt(this.data) + Integer.parseInt(incrData));
        this.data = newValue;
    }

    public void decr(String decrData) {
        var newValue = String.valueOf(Integer.parseInt(this.data) - Integer.parseInt(decrData));
        this.data = newValue;
    }

    public int dataInt() {
        return Integer.parseInt(this.data);
    }
}
