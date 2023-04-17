package compress.model;

import java.math.BigInteger;

public abstract class HuffNode implements Comparable<HuffNode> {
    
    public abstract BigInteger code();
    public abstract int weight();
    public abstract boolean isLeaf();
    public abstract String valueString();
    public abstract String debugString();

    public HuffNode left() { return null; }
    public HuffNode right() { return null; }

    public abstract void setCode(BigInteger c);
    public Encoding encoding() {
        return new Encoding(code());
    }

    public HuffNode forward(HuffNode with) {
        var newHuff = new HuffInternalNode(this, with, this.weight()+with.weight());
        //System.out.println("stack: "+this.toString()+" + "+with.toString()+ " --> "+newHuff.toString());
        return newHuff;
    }

    @Override
    public int compareTo(HuffNode o) {
        if (this.weight() == o.weight()) {
          return this.valueString().compareTo(o.valueString());
        }
        return this.weight() - o.weight();
    }

}
