package json;

public class JsonSerializeOptions {

    private final boolean compact;
    private final int indent;
    private final Character separator;

    public JsonSerializeOptions(boolean compact, int indent, Character separator) {
        this.compact = compact;
        this.indent = indent;
        this.separator = separator;
    }
    public JsonSerializeOptions() {
        this(false, 4, ' ');
    }
    public JsonSerializeOptions(boolean compact) {
        this(compact, 4, ' ');
    }

    public boolean isCompact(){return this.compact;}

    public String indentString(int count) {
        if (this.isCompact()) {
            return "";
        } else {
            var builder = new StringBuilder();
            for (int i = 0; i < this.indent * count; i++) {
                builder.append(this.separator);
            }
            return builder.toString();
        }
    }

}
