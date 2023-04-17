package cut;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import cut.model.Result;

public class Cutter {

    private Reader reader;
    private Cut cut;

    public Cutter(Reader reader, Cut cut) {
        this.reader = reader;
        this.cut = cut;
    }


    public Result<String> processLines() throws IOException, CutterException {
        var result = new Result<String>(cut.fields.length, cut.delimiter);
        var line = parseLine();
        while (line.isPresent()) {
            var lineString = line.get();
            var splitted = lineString.split(""+cut.delimiter);
            // fieldIndex starts with 1 ....
            for (int fieldIndex : cut.fields) {
                var index = fieldIndex - 1;
                if (index < splitted.length) {
                    result.addField(splitted[index]);
                } else {
                    result.addField("");
                }
            }
            line = parseLine();
        }
        return result;
    }


    private Optional<String> parseLine() throws IOException, CutterException {
        var ch = reader.read();
        if (ch < 0) {
            return Optional.empty();
        }
        var buffer = new StringBuilder();
        while (ch >= 0 && (ch != '\n' && ch != '\r')) {
            buffer.append((char)ch);
            ch = reader.read();
        }
        return Optional.of(buffer.toString());
    }

    
}
