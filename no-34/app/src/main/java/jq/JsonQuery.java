package jq;

import jq.parser.JsonQueryParser;
import json.JsonParser;
import json.JsonParserException;
import json.JsonSerializeOptions;
import json.Lexer;
import json.model.JObject;
import json.model.JValue;
import json.model.JsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonQuery {
    private final Input input;

    public JsonQuery(Input input) {
        this.input = input;
    }

    public void execute(String filterExpression) throws IOException, JsonParserException {
        while (this.input.hasNext()) {
            try (var reader = this.input.next()) {
                var json = this.jsonFromReader(reader);
                var builder = new JsonBuilder(new JsonSerializeOptions(false, 4, ' '));
                var jquery = new JsonQueryParser(filterExpression).parse();
                if (jquery.isPresent()) {
                    jquery.get().execute(json, ( x -> {
                        System.out.println(x);
                    }));
                }
            }
        }
    }

    private JValue jsonFromReader(Reader reader) throws JsonParserException, IOException {
        var lexer = new Lexer(reader);
        var parser = new JsonParser(lexer);
        return parser.parseValue();
    }
}
