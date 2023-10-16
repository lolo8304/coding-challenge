package lisp.parser;

import java.util.*;
import java.util.function.Function;

public class Tensor {

    private int[] dimensions;
    private TokenValue[] data;

    public static Tensor Random(int[] dimensions) {
        return new Tensor(dimensions, (unused) -> TokenValue.randomDouble());
    }
    public static Tensor Random(int[] dimensions, double max) {
        return new Tensor(dimensions, (unused) -> TokenValue.randomDouble(max));
    }
    public static Tensor RandomInteger(int[] dimensions, int max) {
        return new Tensor(dimensions, (unused) -> TokenValue.randomInteger(max));
    }
    public static Tensor Zeros(int[] dimensions) {
        return new Tensor(dimensions, TokenValue.ZERO);
    }
    public static Tensor Ones(int[] dimensions) {
        return new Tensor(dimensions, TokenValue.ONE);
    }
    public static Tensor ZerosInt(int[] dimensions) {
        return new Tensor(dimensions, TokenValue.ZERO_INT);
    }
    public static Tensor OnesInt(int[] dimensions) {
        return new Tensor(dimensions, TokenValue.ONE_INT);
    }
    public Tensor(int dimension) {
        this(new int[]{dimension}, TokenValue.NIL);
    }

    public Tensor(int dimension, TokenValue initializer) {
        this(new int[]{dimension}, initializer);
    }
    public Tensor(int dimension, TensorInitializer initializer) {
        this(new int[]{dimension}, initializer);
    }

    private void initData(Function<Void, TokenValue> initializer) {
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = initializer.apply(null);
        }
    }
    private void initDataContent(Function<Void, TokenValue> initializer) {
        var intializeContentExpress = initializer.apply(null);
        this.data = intializeContentExpress.getExpression().toArray(TokenValue[]::new);
    }
    private void initData(TensorInitializer initializer) {
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = initializer.get();
        }
    }


    public Tensor(int[] dimensions) {
        this(dimensions, TokenValue.NIL);
    }

    public Tensor(int[] dimensions, TokenValue initializer) {
        this.dimensions = dimensions;
        this.data = new TokenValue[this.dataSize()];
        if (initializer.getToken() == Token.S_EXPRESSION) {
            this.initDataContent((unused) -> initializer);
        } else {
            this.initData((unused) -> initializer);
        }
    }

    public Tensor(int[] dimensions, Function<Void, TokenValue> initializer) {
        this.dimensions = dimensions;
        this.data = new TokenValue[this.dataSize()];
        this.initData(initializer);
    }
    public Tensor(int[] dimensions, TensorInitializer initializer) {
        this.dimensions = dimensions;
        this.data = new TokenValue[this.dataSize()];
        this.initData(initializer);
    }
    public int[] dimensions() {
        return this.dimensions;
    }

    public int dataSize() {
        return Arrays.stream(this.dimensions).reduce(1, (x, y) -> x * y);
    }

    public TokenValue get(int... indices) {
        return this.data[calculateFlatIndex(indices)];
    }

    private int calculateFlatIndex(int... indices) {
        if (indices.length != dimensions.length) {
            // special case if dimension size of indec + n-tlayer = 1
            for (int i = indices.length; i < dimensions.length; i++) {
                if (dimensions[i] != 1) {
                    throw new IllegalArgumentException("Index dimensions "+indices.length+" does not fit the tensor dimensions "+this.dimensions.length);
                }
            }
        }
        int flatIndex = 0;
        int multiplier = 1;
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= dimensions[i]) {
                throw new IllegalArgumentException("Index "+indices[i] +" out of bounds in "+(i+1)+". dimension (max="+this.dimensions[i]+")");
            }
            flatIndex += indices[i] * multiplier;
            multiplier *= dimensions[i];
        }
        return flatIndex;
    }

    public List<TokenValue> toList() {
        return new DataList(this);
    }

    public ILispFunction set(int[] indices, ILispFunction value) {
        var index = this.calculateFlatIndex(indices);
        var oldValue = this.data[this.calculateFlatIndex(indices)];
        this.data[index] = (TokenValue)value;
        return oldValue;
    }


    public static class DataList extends AbstractList<TokenValue> {
        private final Tensor tensor;

        public DataList(Tensor tensor) {
            this.tensor = tensor;
        }

        @Override
        public TokenValue get(int index) {
            return this.tensor.data[index];
        }

        @Override
        public int size() {
            return this.tensor.data.length;
        }

        @Override
        public TokenValue set(int index, TokenValue element) {
            var oldValue = this.tensor.data[index];
            this.tensor.data[index] = element;
            return oldValue;
        }

    }

}
