package wc;

public class Result {

    public String fileName;
    public int countChars = -1;
    public int countLines = -1;
    public int countWords = -1;

    @Override
    public String toString() {
        var builder = new StringBuilder();
        if (countLines >= 0) {
            builder.append("\t").append(this.countLines);
        }
        if (countWords >= 0) {
            builder.append("\t").append(this.countWords);
        }
        if (countChars >= 0) {
            builder.append("\t").append(this.countChars);
        }
        builder.append(" ").append(this.fileName);
        return builder.toString();
    }


}