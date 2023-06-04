package web.http;

import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

public class HttpWebResponse {

    private Version version;
    private URI uri;
    private Map<String, List<String>> headers;
    private Object entity;
    private ByteBuf content;
    private Status status;

    public HttpWebResponse(Version version, URI uri, Object entity, Status status,
            Map<String, List<String>> headers) {
        this.version = version;
        this.uri = uri;
        this.entity = entity;
        this.status = status;
        this.headers = headers;
        this.setContent();
    }

    private void setContent() {
        setHeader(this.headers, "Content-Length", this.content().readableBytes());
    }

    public Version version() {
        return this.version != null ? version : Version.HTTP_1_1;
    }

    public Object protocolVersion() {
        switch (version()) {
            case HTTP_1_1:
                return "HTTP/1.1";
            case HTTP_2:
                return "HTTP/2";
            default:
                return "HTTP/1.1";
        }
    }

    public URI uri() {
        return this.uri;
    }

    public Status status() {
        return this.status;
    }

    public HttpHeaders headers() {
        return HttpHeaders.of(this.headers, (key, value) -> true);
    }

    public ByteBuf content() {
        if (this.content == null) {
            if (this.entity == null) {
                return UnpooledByteBufAllocator.DEFAULT.buffer(0);
            } else {
                return Unpooled.copiedBuffer(this.entity.toString(), StandardCharsets.UTF_8);
            }
        } else {
            return this.content;
        }
    }

    public static Builder ok(Object entity) {
        var b = ok();
        b.entity = entity;
        return b;
    }

    public static Builder ok(Object entity, MediaType mediaType) {
        var b = ok();
        b.entity = entity;
        b.mediaType = mediaType;
        return b;
    }

    public static Builder ok(Object entity, String mediaType) {
        var b = ok();
        b.entity = entity;
        b.mediaType = MediaType.valueOf(mediaType);
        return b;
    }

    public static Builder ok() {
        return status(Status.OK);
    }

    public static Builder status(Status status) {
        var b = new Builder();
        b.status = status;
        return b;
    }

    public static Builder status(int status) {
        return status(Status.fromStatusCode(status));
    }

    public static Builder serverError() {
        return status(Status.INTERNAL_SERVER_ERROR);
    }

    public static Builder created(URI location) {
        return status(Status.CREATED).location(location);
    }

    public static Map<String, List<String>> addHeader(Map<String, List<String>> map, String name, Object value) {
        var list = map.get(name);
        if (list != null) {
            list.add(value.toString());
        } else {
            var newList = new ArrayList<String>();
            newList.add(value.toString());
            map.put(name, newList);
        }
        return map;
    }

    public static Map<String, List<String>> setHeader(Map<String, List<String>> map, String name, Object value) {
        var newList = new ArrayList<String>();
        newList.add(value.toString());
        map.put(name, newList);
        return map;
    }

    public static class Builder {
        Status status;
        MediaType mediaType;
        Object entity;
        URI location;
        Date lastModified;
        final Map<String, List<String>> headers;

        public Builder() {
            this.status = Status.OK;
            this.mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
            this.headers = new HashMap<>();
        }

        public Builder location(URI location) {
            this.location = location;
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder lastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder addHeader(String name, Object value) {
            HttpWebResponse.addHeader(this.headers, name, value);
            return this;
        }

        public Builder setHeader(String name, Object value) {
            HttpWebResponse.setHeader(this.headers, name, value);
            return this;
        }

        public HttpWebResponse build(Version version, URI uri) {
            if (mediaType != null) {
                this.setHeader("Content-Type", this.mediaType.toString());
            }
            return new HttpWebResponse(version, uri, this.entity, this.status,
                    this.headers);
        }

        public Builder entity(Object entity) {
            this.entity = entity;
            return this;
        }

    }

}
