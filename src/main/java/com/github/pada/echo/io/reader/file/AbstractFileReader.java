package com.github.pada.echo.io.reader.file;

import com.github.pada.echo.io.Line;
import com.github.pada.echo.io.reader.LineReader;

import java.util.function.Consumer;

abstract public class AbstractFileReader implements LineReader {
    protected final String fileName;
    protected Consumer<Line> consumer;
    private volatile boolean run;

    public AbstractFileReader(String fileName) {
        this.fileName = fileName;
        this.run = false;
    }

    @Override
    public String getSource() {
        return this.fileName;
    }

    @Override
    public void stop() {
        this.run = false;
    }

    @Override
    public boolean isRunning() {
        return this.run;
    }

    @Override
    public void setConsumer(Consumer<Line> eventConsumer) {
        this.consumer = eventConsumer;
    }

    protected void beforeRun() {
        if (this.consumer == null) {
            throw new RuntimeException("Consumer not defined");
        }
        this.run = true;
    }
}
