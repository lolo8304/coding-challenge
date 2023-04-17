package compress.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import compress.printer.BinaryTreePrinter;

public class HuffmanCodingTree {

    private static Logger logger = Logger.getLogger(HuffmanCodingTree.class.getName());

    private HuffLeafNode[] frequencies;
    private List<HuffNode> sortedNodes;
    private Dictionary<BigInteger, HuffLeafNode> nodesByEncoding;
    public boolean frequenciesLoaded = false;
    public boolean treeLoaded = false;

    public HuffmanCodingTree() {
        this.frequencies = new HuffLeafNode[(int)Math.pow(256, Character.BYTES)];
        this.sortedNodes = new ArrayList<HuffNode>();
        this.nodesByEncoding = new Hashtable<BigInteger,HuffLeafNode>();
    }

    public int count(int c) {
        if (frequenciesLoaded) {
            return this.frequencies[c] == null ? 0 : this.frequencies[c].weight();
        } else {
            if (this.frequencies[c] == null) {
                this.frequencies[c] = new HuffLeafNode(c, 1);
                return 1;
            } else {
                return this.frequencies[c].inc();
            }
        }
    }

    public HuffLeafNode node(int c) {
        return this.frequencies[c];
    }

    public BigInteger encode(int c) {
        return this.frequencies[c].code();
    }

    public int decode(int e) {
        return nodesByEncoding.get(e).value();
    }

    public String decode(String binaryCode01String) {
        var traverser = new Traverser(binaryCode01String, this.sortedNodes.get(0));
        return traverser.decode();
    }

    public HuffmanCodingTree buildTree() {
        if (!this.treeLoaded) {
            buildNodes();
            build();
            buildEncodings();
            this.treeLoaded = true;
        }
        return this;
    }

    private void buildEncodings() {
        this.sortedNodes.get(0).setCode(BigInteger.valueOf(1));
        this.printFrequency();
        
        for (HuffLeafNode huffLeafNode : frequencies) {
            if (huffLeafNode != null && huffLeafNode.weight() > 0) {
                if (nodesByEncoding.get(huffLeafNode.code()) != null) {
                    System.out.println(huffLeafNode.debugString());
                    throw new IllegalArgumentException("code "+huffLeafNode.code()+" is identical - should never be");
                }
                nodesByEncoding.put(huffLeafNode.code(), huffLeafNode);
            }
        }
    }

    private void build() {
        while (sortedNodes.size() > 1) {
            var first = sortedNodes.get(0); sortedNodes.remove(0);
            var second = sortedNodes.get(0); sortedNodes.remove(0);
            var forwarded = first.forward(second);
            sortedNodes.add(forwarded);
            Collections.sort(this.sortedNodes);
        }
        //printTree();
    }

    private void buildNodes() {
        for (int i = 0; i < frequencies.length; i++) {
            if (count(i)> 0) {
                this.sortedNodes.add(frequencies[i]);    
            }
        }
        Collections.sort(this.sortedNodes);
    }

    public void printTree() {
        BinaryTreePrinter.printBinaryTree(sortedNodes.get(0));
    }

    public void printFrequency() {
        var buffer = new StringBuffer();
        for (int i = 0; i < frequencies.length; i++) {
            if (count(i)> 0) {
                buffer.append(frequencies[i].printFrequency());
                buffer.append("\n");
            }
        }
        System.out.println(buffer);
    }
    
}
