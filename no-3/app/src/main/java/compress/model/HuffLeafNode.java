package compress.model;

import java.math.BigInteger;

public class HuffLeafNode extends HuffNode {

  private int element;
  private int weight;
  private BigInteger code;

  public HuffLeafNode(int el, int wt) {
    element = el;
    weight = wt;
  }

  public int value() {
    return element;
  }

  public int weight() {
    return weight;
  }

  public int inc() {
    return weight++;
  }

  public boolean isLeaf() {
    return true;
  }

  public String debugString() {
    return "["+(char)element+"="+weight+", code="+code()+"]"; 
  }

  @Override
  public BigInteger code() {
    return code;
  }

  @Override
  public void setCode(BigInteger c) {
    code = c;
    //System.out.println("SET "+c+": "+this.toString());
  }

  public String printFrequency() {
      return this.debugString();
  }

  @Override
  public String toString() {
    return "HuffLeafNode [element=" + (char)element + ", weight=" + weight + ", code=" + code + "]";
  }

  @Override
  public String valueString() {
    return ""+(char)this.value();
  }



}
