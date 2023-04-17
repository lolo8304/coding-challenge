package compress.model;

import java.math.BigInteger;

public class Encoding {
    private BigInteger code;
    private int bits;

    public Encoding(BigInteger code, int bits) {
        this.code = code;
        this.bits = bits;
    }
    public Encoding(BigInteger code) {
        this.code = code;
        this.bits = String.valueOf(code).length();
    }

}