package dns;


public class Result<T> {

    private final String ip;

    public Result(String ip){
        this.ip = ip;
    }

    @Override
    public String toString() {
        return ip;
    }

    
}