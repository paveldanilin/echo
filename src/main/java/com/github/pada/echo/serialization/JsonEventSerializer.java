package com.github.pada.echo.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pada.echo.Event;

// TODO: add parameters ie name of fields that should be serialized (jsonpath?)
public class JsonEventSerializer implements EventSerializer {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String serialize(Event event) {
        try {
            return mapper.writeValueAsString(event.getFields());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
