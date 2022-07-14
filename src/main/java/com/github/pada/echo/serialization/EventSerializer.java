package com.github.pada.echo.serialization;

import com.github.pada.echo.Event;

public interface EventSerializer {
    String serialize(Event event);
}
