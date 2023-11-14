package jq;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileInput implements Input {

    private final File[] files;
    private int index;

    public FileInput(File[] files) throws FileNotFoundException {
        this.files = files;
        this.index = this.files.length > 0 ? 0 : -1;
    }
    @Override
    public boolean hasNext() {
        return (this.index >= 0 && this.index < this.files.length);
    }

    @Override
    public Reader next() {
        try {
            return new FileReader(this.files[this.index++]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
