package com.github.pada.echo.io.reader;

import com.github.pada.echo.io.Line;

import java.util.function.Consumer;

public interface LineReader extends Runnable {
    String getSource();
    void stop();
    boolean isRunning();
    void setConsumer(Consumer<Line> eventConsumer);
}
