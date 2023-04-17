package compress.model;

import java.math.BigInteger;

public class HuffInternalNode extends HuffNode {
    private BigInteger code;
    private int weight;
    private HuffNode left;
    private HuffNode right;
    private BigInteger c;

    /** Constructor */
    public HuffInternalNode(HuffNode l, HuffNode r, int wt) {
        left = l;
        right = r;
        weight = wt;
    }

    /** @return The left child */
    public HuffNode left() {
        return left;
    }

    /** @return The right child */
    public HuffNode right() {
        return right;
    }

    /** @return The weight */
    public int weight() {
        return weight;
    }

    /** Return false */
    public boolean isLeaf() {
        return false;
    }

    public String debugString() {
        return "["+weight+"]"; 
    }

    @Override
    public void setCode(BigInteger c) {
        this.c = c;
        code = c;
        //System.out.println("SET "+c+": "+this.toString());
        left.setCode(c.multiply(BigInteger.valueOf(10)));
        right.setCode(c.multiply(BigInteger.valueOf(10)).add(BigInteger.valueOf(1)));
    }

    @Override
    public BigInteger code() {
        return code;
    }
    
    @Override
    public String valueString() {
      return ""+this.left.valueString()+this.right.valueString();
    }

    @Override
    public String toString() {
        return "HuffInternalNode [element="+this.valueString()+", weight=" + weight + ", code=" + code + "]";
    }

}
