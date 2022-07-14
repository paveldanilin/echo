package com.github.pada.echo.io.writer;

import java.util.List;
import java.util.Map;

public class ConsoleWriter implements Writer {
    @Override
    public void write(Map<String, String> headers, String event) {
        long threadId = Thread.currentThread().getId();
        System.out.println(threadId + ":" + event);
    }

    @Override
    public void write(Map<String, String> headers, List<String> batch) {
        for (String event : batch) {
            this.write(headers, event);
        }
    }
}
