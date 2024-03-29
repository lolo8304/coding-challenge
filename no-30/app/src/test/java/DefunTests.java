/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import lisp.parser.LispRuntime;
import lisp.parser.Parser;
import lisp.parser.TokenValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefunTests {

    private BufferedReader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = DefunTests.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new BufferedReader(new FileReader(file));
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void tokenize_defuns_expectok() throws URISyntaxException, IOException {
        // Arrange
        ReadReader("step1/valid.defun.txt");
        var runtime = new LispRuntime();

        // Action
        var tokens = new Parser(reader).parse();
        for (var token : tokens) {
            var result = (TokenValue)token.apply(runtime);
            // Assert
            assertNotNull(result);
            System.out.println(token.appendTo(new StringBuilder()).toString());
            System.out.println(result.appendTo(new StringBuilder()).toString());
        }
    }


}
