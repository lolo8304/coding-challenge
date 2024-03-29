/*
 * This Java source file was generated by the Gradle 'init' task.
 */



import json.JsonParser;
import json.JsonParserException;
import json.JsonSerializeOptions;
import json.Lexer;
import json.model.JsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

class IndentTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = IndentTest.class.getResource("tests/"+testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test void compact_objects() throws URISyntaxException, IOException, JsonParserException {
        // Arrange
        ReadReader("indent-test-object.json");
        var lexer = new Lexer(reader);
        var json = new JsonParser(lexer).parse();

        var serializer = new JsonSerializeOptions(true);
        var builder = new JsonBuilder(serializer);

        // Action
        var result = json.serialize(builder).toString();

        // Assert
        System.out.println(result);
        Assertions.assertFalse(result.contains("\r\n"));
    }

    @Test void noncompact_objects() throws URISyntaxException, IOException, JsonParserException {
        // Arrange
        ReadReader("indent-test-object.json");
        var lexer = new Lexer(reader);
        var json = new JsonParser(lexer).parse();

        var serializer = new JsonSerializeOptions(false, 3, ' ');
        var builder = new JsonBuilder(serializer);

        // Action
        var result = json.serialize(builder).toString();

        // Asser
        System.out.println(result);
        Assertions.assertTrue(result.contains("\r\n"));
    }


    @Test void compact_array() throws URISyntaxException, IOException, JsonParserException {
        // Arrange
        ReadReader("indent-test-array.json");
        var lexer = new Lexer(reader);
        var json = new JsonParser(lexer).parseValue();

        var serializer = new JsonSerializeOptions(true);
        var builder = new JsonBuilder(serializer);

        // Action
        var result = json.serialize(builder).toString();

        // Assert
        System.out.println(result);
        Assertions.assertFalse(result.contains("\r\n"));
    }

    @Test void noncompact_array() throws URISyntaxException, IOException, JsonParserException {
        // Arrange
        ReadReader("indent-test-array.json");
        var lexer = new Lexer(reader);
        var json = new JsonParser(lexer).parseValue();

        var serializer = new JsonSerializeOptions(false);
        var builder = new JsonBuilder(serializer);

        // Action
        var result = json.serialize(builder).toString();

        // Assert
        System.out.println(result);
        Assertions.assertTrue(result.contains("\r\n"));
    }
}
