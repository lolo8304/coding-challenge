/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import nats.protocol.NatsLineParser;
import nats.protocol.commands.ICmd;

class NatsLineParserTests {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = NatsLineParserTests.class.getResource("tests/" + testfile);
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
    void nextToken_connectSimple_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var lineparser = new NatsLineParser("CONNECT { \"verbose\":true }" + ICmd.CRLF);

        // Action
        var token1 = lineparser.nextToken();
        var token2 = lineparser.nextToken();
        var token3 = lineparser.nextToken();
        var token4 = lineparser.nextToken();

        // Assert
        assertTrue(token1.isPresent());
        assertEquals(NatsLineParser.Type.STRING, token1.get().type());
        assertEquals("CONNECT", token1.get().toString());

        assertTrue(token2.isPresent());
        assertEquals(NatsLineParser.Type.JSON, token2.get().type());

        assertTrue(token3.isPresent());
        assertEquals(NatsLineParser.Type.CRLF, token3.get().type());

        assertFalse(token4.isPresent());

    }

    @Test
    void nextToken_connectSimpleButNoCrLf_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var lineparser = new NatsLineParser("CONNECT { \"verbose\":true }");

        // Action
        var token1 = lineparser.nextToken();

        // Assert
        assertTrue(token1.isPresent());
        assertEquals(NatsLineParser.Type.STRING, token1.get().type());

        assertThrows(IllegalArgumentException.class, () -> {
            lineparser.nextToken();
        });

    }

    @Test
    void nextToken_ping_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var lineparser = new NatsLineParser("PING", null);

        // Action
        var token1 = lineparser.nextToken();

        // Assert
        assertTrue(token1.isPresent());
        assertEquals(NatsLineParser.Type.STRING, token1.get().type());
        assertEquals("PING", token1.get().toString());

    }

    @Test
    void nextToken_pubsimple_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var mockRequest = new MockRequest("PUB Foo.Bar 11" + ICmd.CRLF, "12345678901" + ICmd.CRLF);
        var lineparser = new NatsLineParser(mockRequest.readNextLine(), mockRequest);

        // Action
        var command = lineparser.nextToken();
        var subject = lineparser.nextToken();
        var bytes = lineparser.nextToken();
        var payload = lineparser.readNextLine().nextToken();

        // Assert
        assertTrue(command.isPresent());
        assertEquals(NatsLineParser.Type.STRING, command.get().type());
        assertEquals("PUB", command.get().toString());

        assertTrue(subject.isPresent());
        assertEquals(NatsLineParser.Type.STRING, subject.get().type());
        assertEquals("Foo.Bar", subject.get().toString());

        assertTrue(bytes.isPresent());
        assertEquals(NatsLineParser.Type.INTEGER, bytes.get().type());
        assertEquals(11, bytes.get().toInt());

        assertTrue(payload.isPresent());
        assertEquals(NatsLineParser.Type.STRING, payload.get().type());
        assertEquals("12345678901", payload.get().toString());
    }

    @Test
    void nextToken_pubReplyTo_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var mockRequest = new MockRequest("PUB Foo.Bar ReplyTo.Foo 11" + ICmd.CRLF, "12345678901" + ICmd.CRLF);
        var lineparser = new NatsLineParser(mockRequest.readNextLine(), mockRequest);

        // Action
        var command = lineparser.nextToken();
        var subject = lineparser.nextToken();
        var replyTo = lineparser.nextToken();
        var bytes = lineparser.nextToken();
        var payload = lineparser.readNextLine().nextToken();

        // Assert
        assertTrue(command.isPresent());
        assertEquals(NatsLineParser.Type.STRING, command.get().type());
        assertEquals("PUB", command.get().toString());

        assertTrue(subject.isPresent());
        assertEquals(NatsLineParser.Type.STRING, subject.get().type());
        assertEquals("Foo.Bar", subject.get().toString());

        assertTrue(replyTo.isPresent());
        assertEquals(NatsLineParser.Type.STRING, replyTo.get().type());
        assertEquals("ReplyTo.Foo", replyTo.get().toString());

        assertTrue(bytes.isPresent());
        assertEquals(NatsLineParser.Type.INTEGER, bytes.get().type());
        assertEquals(11, bytes.get().toInt());

        assertTrue(payload.isPresent());
        assertEquals(NatsLineParser.Type.STRING, payload.get().type());
        assertEquals("12345678901", payload.get().toString());
    }

    @Test
    void nextToken_subsimple_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var mockRequest = new MockRequest("SUB Foo.Bar 42" + ICmd.CRLF);
        var lineparser = new NatsLineParser(mockRequest.readNextLine(), mockRequest);

        // Action
        var command = lineparser.nextToken();
        var subject = lineparser.nextToken();
        var sid = lineparser.nextToken();

        // Assert
        assertTrue(command.isPresent());
        assertEquals(NatsLineParser.Type.STRING, command.get().type());
        assertEquals("SUB", command.get().toString());

        assertTrue(subject.isPresent());
        assertEquals(NatsLineParser.Type.STRING, subject.get().type());
        assertEquals("Foo.Bar", subject.get().toString());

        assertTrue(sid.isPresent());
        assertEquals("42", sid.get().toString());

    }

    @Test
    void nextToken_subWithQueueGroup_expectsok() throws URISyntaxException, IOException, InterruptedException {
        // Arrange
        var mockRequest = new MockRequest("SUB Foo.Bar BarQueueGroup 42" + ICmd.CRLF);
        var lineparser = new NatsLineParser(mockRequest.readNextLine(), mockRequest);

        // Action
        var command = lineparser.nextToken();
        var subject = lineparser.nextToken();
        var queueGroup = lineparser.nextToken();
        var sid = lineparser.nextToken();

        // Assert
        assertTrue(command.isPresent());
        assertEquals(NatsLineParser.Type.STRING, command.get().type());
        assertEquals("SUB", command.get().toString());

        assertTrue(subject.isPresent());
        assertEquals(NatsLineParser.Type.STRING, subject.get().type());
        assertEquals("Foo.Bar", subject.get().toString());

        assertTrue(queueGroup.isPresent());
        assertEquals(NatsLineParser.Type.STRING, queueGroup.get().type());
        assertEquals("BarQueueGroup", queueGroup.get().toString());

        assertTrue(sid.isPresent());
        assertEquals("42", sid.get().toString());

    }
}