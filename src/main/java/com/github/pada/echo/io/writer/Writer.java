package com.github.pada.echo.io.writer;

import java.util.List;
import java.util.Map;

public interface Writer {
    void write(Map<String, String> headers, String eventBody);
    void write(Map<String, String> headers, List<String> batch);
}
