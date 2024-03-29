/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import memcached.commands.DataCommand;
import memcached.commands.Response;

class ResponseTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = ResponseTest.class.getResource("tests/" + testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Test
    void new_emptyCmd_expectsok() throws URISyntaxException, IOException {

        // Arrange
        // Act
        var response = new Response("END");
        // Assert
        assertEquals(0, response.cmds.size());
        assertEquals("END", response.finalNote);

    }

    @Test
    void new_dataCmd_expectsok() throws URISyntaxException, IOException {

        // Arrange
        var response = new Response("END");
        // Act
        response.addValue(new DataCommand("VALUE", "key", "0", "5", "hello"));
        // Assert
        assertEquals(1, response.cmds.size());
        assertEquals("hello", response.cmds.get(0).data.data);
        assertEquals("END", response.finalNote);

    }

    @Test
    void toResponseString_standard_expectsok() throws URISyntaxException, IOException {

        // Arrange
        var response = new Response("END");
        response.addValue(new DataCommand("set", "key", "0", "5", "hello").asValueCommand());

        // Act
        var responseString = response.toResponseString();
        // Assert
        assertEquals("VALUE key 0 5\r\nhello\r\nEND\r\n", responseString);

    }
}
