package web.http.servlets;

import web.http.HttpWebRequest;
import web.http.HttpWebResponse;

public interface WebServlet {

    public HttpWebResponse request(HttpWebRequest req);

}
