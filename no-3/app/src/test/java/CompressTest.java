/*
 * This Java source file was generated by the Gradle 'init' task.
 */


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import compress.Compress;
import compress.Compressor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

class CompressTest {

    private Reader reader;

    void ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = CompressTest.class.getResource("tests/"+testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
    }

    @AfterEach
    void CloseReader() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    
    @Test void X_LesMisearble_expectsOK() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("135-0.txt");
        var compress = new Compressor();
        compress.buildTree(reader);

        //Action
        var count_X = compress.getOccurances('X');

        //Assert
        assertEquals(333, count_X, "count X is not correct");
    }


    @Test void t_LesMisearble_expectsOK() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("135-0.txt");
        var compress = new Compressor();
        compress.buildTree(reader);

        //Action
        var count_t = compress.getOccurances('t');

        //Assert
        assertEquals(223000, count_t, "count t is not correct");
    }

    @Test void buildTree_LesMisearble_expectsOK() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("little.txt");
        var compress = new Compressor();
        compress.buildTree(reader);

        //Action
        compress.printFrequency();
        
        ReadReader("little.txt");
        var enc = compress.encode(reader);

        var decoded = compress.decode(enc);
        System.out.print(decoded);

        //Assert
        assertNotNull(decoded, "tree is not built");
    }


    @Test void enc_dec_LesMisearble_expectsOK() throws URISyntaxException, IOException {

        //Arrange
        ReadReader("135-0.txt");
        var compress = new Compressor();
        compress.buildTree(reader);

        //Action
        compress.printFrequency();
        
        ReadReader("135-0.txt");
        var enc = compress.encode(reader);
        System.out.println("Compressed characters:   "+enc.length());

        var decoded = compress.decode(enc);
        System.out.println("Uncompressed characters: "+decoded.length());

        //Assert
        assertNotNull(decoded, "tree is not built");
    }

}
