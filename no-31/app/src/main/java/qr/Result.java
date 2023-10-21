package qr;

public class Result {

    private final String outputFileName;

    public Result(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    @Override
    public String toString() {
        return "'" + this.outputFileName + "' file generated";
    }
}
