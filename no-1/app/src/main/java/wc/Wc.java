package wc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Wc {

    private final File file;

    public Wc(File file) {
        this.file = file;
    }

    public Result count(boolean switchLines, boolean switchWords, boolean switchCharacters) throws IOException {
        var result = new Result();
        result.fileName = this.file.getName();
        var bytes = Files.readAllBytes(Path.of(this.file.toURI()));
        var switchAll = (switchCharacters == switchLines) && (switchLines == switchWords);
        if (switchAll) {
            switchCharacters = true;
            switchLines = true;
            switchWords = true;
        }
        if (switchCharacters) {
            calculateChars(result, bytes);
        }
        if (switchLines) {
            calulcateLines(result, bytes);
        }
        if (switchWords) {
            calculateWords(result, bytes);
        }
        return result;
    }

    private static void calculateChars(Result result, byte[] bytes) {
        result.countChars = bytes.length;
    }

    private static void calulcateLines(Result result, byte[] bytes) {
        var i = 0;
        for (byte byte2 : bytes) {
            if (byte2 == '\n') {
                i++;
            }
        }
        result.countLines = i;
    }

    private static void calculateWords(Result result, byte[] bytes) {
        var i = 0;
        var lastWordCount = 0;
        for (byte byte2 : bytes) {
            if (Character.isWhitespace(byte2)) {
                if (lastWordCount > 0) {
                    i++;
                    lastWordCount = 0;
                }
            } else {
                lastWordCount++;
            }
        }
        if (lastWordCount > 0){
            i++;
        }
        result.countWords = i;
    }
}
