package com.github.pada.echo.io.reader.file;

import com.github.pada.echo.io.Line;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SimpleLineReader extends AbstractFileReader {
    private final static int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final int bufferSize;
    private long offset;

    public SimpleLineReader(String fileName) {
        this(fileName, DEFAULT_BUFFER_SIZE, 0);
    }

    public SimpleLineReader(String fileName, int bufferSize, long offset) {
        super(fileName);
        this.bufferSize = bufferSize;
        this.offset = offset;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            this.beforeRun();
            reader = new BufferedReader(new FileReader(this.fileName), this.bufferSize);
            String inputString;
            if (offset > 0) {
                offset = reader.skip(offset);
            }
            while (this.isRunning() && (inputString = reader.readLine()) != null) {
                this.consumer.accept(new Line(inputString, offset));
                offset += inputString.length() + LINE_SEPARATOR.length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
