package jq;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FileInput implements Input {

    private int index;
    private final FileInputStream[] fileInputStreams;

    public FileInput(File[] files) throws FileNotFoundException {
        List<FileInputStream> list = new ArrayList<>();
        for (File file : files) {
            FileInputStream fileInputStream = new FileInputStream(file);
            list.add(fileInputStream);
        }
        this.fileInputStreams = list.toArray(FileInputStream[]::new);
        this.index = this.fileInputStreams.length > 0 ? 0 : -1;
    }
    @Override
    public boolean hasNext() {
        return (this.index >= 0 && this.index < this.fileInputStreams.length);
    }

    @Override
    public InputStream next() {
        return this.fileInputStreams[this.index++];
    }
}
