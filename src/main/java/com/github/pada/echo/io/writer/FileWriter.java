package com.github.pada.echo.io.writer;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class FileWriter implements Writer {
    private final static String NEW_LINE = System.getProperty("line.separator");
    private final BufferedWriter bufferedWriter;
    private final ThreadLocal<StringBuilder> stringBuilder = ThreadLocal.withInitial(StringBuilder::new);

    public FileWriter(String filename) throws IOException {
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(new File(filename).toPath())));
    }

    @Override
    public void write(Map<String, String> headers, String eventBody) {
        try {
            this.bufferedWriter.write(eventBody);
            this.bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(Map<String, String> headers, List<String> batch) {
        for (String eventBody : batch) {
            this.stringBuilder.get().append(eventBody);
            this.stringBuilder.get().append(NEW_LINE);
        }
        try {
            this.bufferedWriter.write(this.stringBuilder.get().toString());
            this.bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.stringBuilder.get().setLength(0);
    }
}
