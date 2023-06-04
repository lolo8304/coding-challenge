package web.http;

/* reused from javax.ws.rs.core */
public interface StatusType {
    public int getStatusCode();

    public Status.Family getFamily();

    public String getReasonPhrase();
}
