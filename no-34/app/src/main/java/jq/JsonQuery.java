package jq;

import json.*;
import json.model.JObject;
import json.model.JsonBuilder;

import java.io.BufferedReader;
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
            try (var inputStream = this.input.next()) {
                var json = this.jsonFromReader(new InputStreamReader(inputStream));
                var builder = new JsonBuilder(new JsonSerializeOptions(false, 4, ' '));
                System.out.println(json.serialize(builder).toString());
            }
        }
    }

    private JObject jsonFromReader(Reader reader) throws JsonParserException {
        var lexer = new Lexer(reader);
        var parser = new JsonParser(lexer);
        return parser.parse();
    }
}
