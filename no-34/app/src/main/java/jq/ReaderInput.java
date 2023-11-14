package jq;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderInput implements Input {


    private Reader reader;

    public ReaderInput(Reader reader)  {
        this.reader = reader;
    }
    @Override
    public boolean hasNext() {
        return this.reader != null;
    }

    @Override
    public Reader next() {
        var r = this.reader;
        this.reader = null;
        return r;
    }
}
