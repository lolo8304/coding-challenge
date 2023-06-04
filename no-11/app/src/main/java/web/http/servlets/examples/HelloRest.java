package web.http.servlets.examples;

import web.http.HttpWebRequest;
import web.http.HttpWebResponse;
import web.http.MediaType;
import web.http.servlets.WebServlet;

public class HelloRest implements WebServlet {

    @Override
    public HttpWebResponse request(HttpWebRequest req) {
        return HttpWebResponse
                .ok("{ \"hello\": \"hello\" }", MediaType.APPLICATION_JSON_TYPE)
                .build(req);
    }

}
