package json.model;

import json.JsonSerializeOptions;

import java.io.IOException;

public class JsonBuilder implements java.io.Serializable, Appendable, CharSequence {

    private final StringBuilder builder;
    private final JsonSerializeOptions options;
    private int indent;
    private boolean hasNewLine;

    public JsonBuilder() {
        this(new StringBuilder(), new JsonSerializeOptions());
        this.indent = 0;
    }

    public JsonBuilder(JsonSerializeOptions options) {
        this(new StringBuilder(), options);
    }

    public JsonBuilder(StringBuilder builder, JsonSerializeOptions options) {
        this.builder = builder;
        this.options = options;
    }

    public JsonBuilder newLineIfNotCompact() {
        if (!this.options.isCompact()) {
            this.hasNewLine = true;
        }
        return this;
    }
    private JsonBuilder appendIndentIfNewLine() {
        if (this.hasNewLine) {
            this.hasNewLine = false;
            this.append("\r\n");
            this.appendIndent();
        }
        return this;
    }

    @Override
    public JsonBuilder append(CharSequence csq) {
        this.appendIndentIfNewLine();
        this.builder.append(csq);
        return this;
    }

    @Override
    public JsonBuilder append(CharSequence csq, int start, int end) {
        this.appendIndentIfNewLine();
        this.builder.append(csq, start, end);
        return this;
    }

    @Override
    public JsonBuilder append(char c) {
        this.appendIndentIfNewLine();
        builder.append(c);
        return this;
    }

    @Override
    public int length() {
        return this.builder.length();
    }

    @Override
    public char charAt(int index) {
        return this.builder.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.builder.subSequence(start,end);
    }

    public JsonBuilder indentPlus() {
        if (!this.options.isCompact()) {
            this.newLineIfNotCompact();
            this.indent++;
        }
        return this;
    }
    public JsonBuilder indentMinus() {
        if (!this.options.isCompact()) {
            this.newLineIfNotCompact();
            this.indent--;
        }
        return this;
    }

    public JsonBuilder appendIndent() {
        this.append(this.options.indentString(this.indent));
        return this;
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
