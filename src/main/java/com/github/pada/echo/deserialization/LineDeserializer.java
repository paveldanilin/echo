package com.github.pada.echo.deserialization;

import com.github.pada.echo.io.Line;
import com.github.pada.echo.Event;

public interface LineDeserializer {
    Event deserialize(final Line line);
}
