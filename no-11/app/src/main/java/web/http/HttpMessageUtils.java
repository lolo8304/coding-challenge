package web.http;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

final class HttpMessageUtil {

    public static final String NEWLINE = "\r\n";

    static StringBuilder appendFullResponse(StringBuilder buf, HttpWebResponse res) {
        appendInitialLine(buf, res);
        appendHeaders(buf, res.headers());
        buf.append(NEWLINE);
        return buf;
    }

    private static void appendInitialLine(StringBuilder buf, HttpWebResponse res) {
        buf.append(res.protocolVersion());
        buf.append(' ');
        buf.append(res.status().getStatusCode());
        buf.append(' ');
        buf.append(res.status());
        buf.append(NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
        for (Map.Entry<String, List<String>> e : headers.map().entrySet()) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(String.join(",", e.getValue()));
            buf.append(NEWLINE);
        }
    }

    private HttpMessageUtil() {
    }
}
