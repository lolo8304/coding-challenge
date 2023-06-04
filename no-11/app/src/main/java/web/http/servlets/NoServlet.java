package web.http.servlets;

import web.http.HttpWebRequest;
import web.http.HttpWebResponse;
import web.http.Status;

public class NoServlet implements WebServlet {

    @Override
    public HttpWebResponse request(HttpWebRequest req) {
        return HttpWebResponse
                .status(Status.NOT_FOUND)
                .build(req);
    }

}
