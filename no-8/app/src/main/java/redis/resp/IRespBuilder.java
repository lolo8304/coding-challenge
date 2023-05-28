package redis.resp;

import redis.resp.types.RespArray;
import redis.resp.types.RespType;

public interface IRespBuilder {
    public void toRespString(StringBuilder buffer);

    public RespType toRespType();

    public void loadFrom(RespArray data) throws RespException;

    public static String toRespString(IRespBuilder object) {
        var builder = new StringBuilder();
        object.toRespString(builder);
        return builder.toString();
    }

    public static String toRespEscapedString(IRespBuilder object) {
        return RespScanner.convertNewLinesBack(toRespString(object));
    }
}
