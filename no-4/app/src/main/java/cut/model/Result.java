package cut.model;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {

    private int dimensions;
    private List<Row<T>> rows = new ArrayList<>();
    private Row<T> currentRow;
    private char delimiter;

    public static <T> T[] createArray(int size) {
        // Create an array of objects
        Object[] objArray = new Object[size];
        
        // Cast the array to generic array of type T
        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) objArray;
        
        return newArray;
    }

    public Result(int dimensions, char delimiter) {
        this.dimensions = dimensions;
        this.delimiter = delimiter;
    }
    
    public Row<T> addField(T value) {
        var oldCurrentRow = currentRow;

        if (currentRow == null) {
            currentRow = new Row<>(dimensions);
        }
        currentRow = currentRow.addField(value);
        if (oldCurrentRow != currentRow) {
            rows.add(currentRow);
        }
        return currentRow;
    }

    public Shape shape() {
        return new Shape(this.rows.size(), this.dimensions);
    }

    public T get(int r, int c) {
        var row = r < this.rows.size() ? this.rows.get(r) : null;
        return (row != null && c < this.dimensions) ? row.get(c) : null;
    }

    public T[] column(int c) {
        T[] column = Result.createArray(this.rows.size());
        for (int i = 0; i < rows.size(); i++) {
            var row = rows.get(i);
            column[i] = row.get(c);
        }
        return column;
    }

    public String printResult() {
        var buffer = new StringBuilder();
        var start = true;
        for (Row<T> row : rows) {
            if (!start) {
                buffer.append('\n');
            }
            for (int i = 0; i < this.dimensions; i++) {
                if (i > 0) {
                    buffer.append(delimiter);
                }
                buffer.append(row.get(i));
            }
            start = false;
        }
        return buffer.toString();
    }

    public Result<T> add(Result<T> result) {
        Result<T> newResult = new Result<>(dimensions, delimiter);
        newResult.rows.addAll(this.rows);
        newResult.rows.addAll(result.rows);
        return newResult;
    }

    public class Row<S> {
        private int index = 0;
        private int dimensions;
        private S[] data;

        public Row(int dimensions) {
            this.dimensions = dimensions;
            this.data = Result.createArray(this.dimensions);
        }

        public S get(int c) {
            return c < this.dimensions ? data[c] : null;
        }
        public Row<S> addField(S value) {
            if (index < dimensions) {
                data[index] = value;
                index++;
                return this;
            } else {
                return new Row<S>(this.dimensions).addField(value);
            }
        }
    }
}