package redis.resp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import redis.resp.types.RespType;

public class RespResponse {

    public final RespType[] values;

    public RespResponse(RespType[] values) {
        this.values = values;
    }

    public RespResponse(List<RespType> values) {
        this.values = values.toArray(RespType[]::new);
    }

    public RespResponse(RespType value) {
        this.values = new RespType[] { value };
    }

    public RespResponse add(RespResponse response) {
        var combined = new RespType[this.values.length + response.values.length];
        System.arraycopy(this.values, 0, combined, 0, this.values.length);
        System.arraycopy(response.values, 0, combined, this.values.length, response.values.length);
        return new RespResponse(combined);
    }

    public static RespResponse join(RespResponse[] responses) {
        var list = new ArrayList<RespType>();
        for (RespResponse response : responses) {
            list.addAll(Arrays.asList(response.values));
        }
        return new RespResponse(list);
    }

    public String toRespString() {
        var builder = new StringBuilder();
        toRespString(builder);
        return builder.toString();
    }

    public void toRespString(StringBuilder builder) {
        var first = true;
        for (RespType respType : values) {
            if (!first) {
                builder.append("\r\n");
            } else {
                first = false;
            }
            respType.toRespString(builder);
        }
    }

}
