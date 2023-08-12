package dns;


public class Result<T> {

    private String ip;

    public Result(String ip){
        this.ip = ip;
    }

    @Override
    public String toString() {
        return ip;
    }

    
}