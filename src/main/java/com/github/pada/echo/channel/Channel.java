package com.github.pada.echo.channel;

import com.github.pada.echo.Event;

public interface Channel {
    boolean accept(Event event);
}
