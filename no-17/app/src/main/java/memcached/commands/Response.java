package memcached.commands;

import java.util.ArrayList;
import java.util.List;

public class Response {
    public final List<DataCommand> cmds;
    public String finalNote;

    public Response() {
        this.cmds = new ArrayList<>();
    }

    public Response(String finalNote) {
        this.cmds = new ArrayList<>();
        this.finalNote = finalNote;
    }

    public Response addValue(DataCommand cmd) {
        this.cmds.add(cmd);
        return this;
    }

    public String toResponseString() {
        var buffer = new StringBuilder();
        for (DataCommand cmd : this.cmds) {
            buffer.append(cmd.toResponseString());
        }
        buffer.append(this.finalNote + "\r\n");
        return buffer.toString();
    }
}
