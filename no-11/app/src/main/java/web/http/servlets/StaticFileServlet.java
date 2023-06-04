package web.http.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import web.http.HttpWebRequest;
import web.http.HttpWebResponse;
import web.http.MediaType;
import web.http.Status;

public class StaticFileServlet implements WebServlet {
    private String webRoot;
    private String[] defaultIndexes = { "index.html", "index.htm" };

    public StaticFileServlet(String webRoot) {
        this.webRoot = webRoot != null ? webRoot : "./www";
    }

    private Optional<URI> getDefault(URI uri) {
        for (String defaultIndex : defaultIndexes) {
            var defaultUri = uri.resolve(defaultIndex);
            if (new File(defaultUri.getPath()).exists()) {
                return Optional.of(defaultUri);
            }
        }
        return Optional.empty();
    }

    private boolean isFolder(URI uri) {
        var f = new File(new File(webRoot), uri.getPath());
        return f.exists() && f.isDirectory();
    }

    @Override
    public HttpWebResponse request(HttpWebRequest req) {
        try {
            var uri = req.uri();
            if (this.isFolder(uri)) {
                uri = new URI(this.webRoot + uri + "/");
            }
            if (uri.getPath().endsWith("/")) {
                var defaultUri = this.getDefault(uri);
                if (defaultUri.isPresent()) {
                    uri = defaultUri.get();
                } else {
                    return HttpWebResponse
                            .status(Status.FORBIDDEN)
                            .entity("<!DOCTYPE html> <html><head><title>403 Forbidden</title></head><body><h1>403 Forbidden</h1><p>You don't have permission to access this directory.</p></body></html>")
                            .build(req);
                }
            } else {
                uri = new URI(this.webRoot + uri);
            }
            var pathFileName = uri.getPath();
            if (new File(pathFileName).exists()) {
                byte[] fileBytes = Files.readAllBytes(Paths.get(pathFileName));
                ByteBuf byteBuf = Unpooled.wrappedBuffer(fileBytes);

                MediaType mediaType = this.getMediaType(pathFileName);
                return HttpWebResponse
                        .ok(byteBuf)
                        .mediaType(mediaType)
                        .build(req);

            } else {
                return HttpWebResponse.status(Status.NOT_FOUND).entity("resource " + req.uri() + " not found")
                        .build(req);
            }
        } catch (IOException | URISyntaxException e) {
            return HttpWebResponse.serverError().build(req);
        }
    }

    private MediaType getMediaType(String uri) throws IOException {
        Path file = Paths.get(uri);
        String mimeType = Files.probeContentType(file);
        return MediaType.valueOf(mimeType);
    }

}
