package qr;

public class Result {

    private final String data;
    private final String outputFileName;

    public Result(String data, String outputFileName) {
        this.data = data;
        this.outputFileName = outputFileName;
    }

    @Override
    public String toString() {
        return "'" + this.outputFileName + "' file generated";
    }
}
