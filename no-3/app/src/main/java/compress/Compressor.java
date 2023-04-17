package compress;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;

import compress.model.HuffLeafNode;
import compress.model.HuffmanCodingTree;

public class Compressor {

    public HuffmanCodingTree tree;

    private void CalculateFrequencies(Reader reader) throws IOException {
        if (!tree.frequenciesLoaded) {
            var ch = reader.read();
            while (ch >= 0) {
                tree.count(ch);
                ch = reader.read();
            }
            tree.frequenciesLoaded = true;                        
        }
    }

    public int getOccurances(char c) throws IOException {
        if (this.tree.frequenciesLoaded) {
            return this.tree.count(c);
        } else {
            throw new IOException("tree is built");
        }
    }
    public void buildTree(String str) throws IOException {
        buildTree(new StringReader(str));
    }

    public void buildTree(Reader reader) throws IOException {
        this.tree = new HuffmanCodingTree();
        this.CalculateFrequencies(reader);
        this.tree.buildTree();
    }

    private String splitCharsInto01Bytes(String binaryCode01String) {
        var i = 0;
        var buffer = new StringBuilder();
        while (i < binaryCode01String.length()) {
            var code01String = String.valueOf(Integer.toBinaryString(binaryCode01String.charAt(i)));
            String binaryString = String.join("", Collections.nCopies(8 - code01String.length(), "0"))+code01String;
            buffer.append(binaryString);
            i++;
        }
        return buffer.toString();        
    }

    private String split01BytesIntoChars(String code01String) {
        var i = 0;
        var buffer = new StringBuilder();
        while (i < code01String.length()) {
            var bin = code01String.substring(i, Math.min(i+8, code01String.length()));
            if (bin.length() < 8) {
                bin = bin + (String.join("", Collections.nCopies(8-bin.length(), "0")));
            }
            var ch = Integer.parseInt(bin, 2);
            buffer.append((char)ch);
            i += 8;
        }
        return buffer.toString();
    }

    public String encode(Reader reader) throws IOException {
        if (!this.tree.treeLoaded) {
            throw new IOException("Tree is not built. can only encode after build of tree.");
        }

        var buffer = new StringBuilder();
        var ch = reader.read();
        while (ch >= 0) {
            var enc = tree.encode(ch);
            buffer.append(enc);
            ch = reader.read();
        }
        return split01BytesIntoChars(buffer.toString());
    }

    public HuffLeafNode node(int c) {
        return tree.node(c);
    }

    public String encode(String str) throws IOException {
        if (!this.tree.treeLoaded) {
            throw new IOException("Tree is not built. can only encode after build of tree.");
        }
        return this.encode(new StringReader(str));
    }

    public String decode(String binaryCode01String) {
        return tree.decode(splitCharsInto01Bytes(binaryCode01String));
    }

    public int decode(int code) {
        return tree.decode(code);
    }


    public void printFrequency() {
        this.tree.printFrequency();
        
    }
}
