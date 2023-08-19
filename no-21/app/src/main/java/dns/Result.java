package dns;


public class Result<T> {

    private final DnsMessage message;

    public Result(DnsMessage message){
        this.message = message;
    }
    public Result(){
        this.message = null;
    }

    @Override
    public String toString() {
        return this.message != null ? String.join(",", message.getIpAddresses()) : "";
    }

    public boolean hasMessage() {
        return this.message != null;
    }
    
}