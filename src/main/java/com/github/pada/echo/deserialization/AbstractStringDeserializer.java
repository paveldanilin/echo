package com.github.pada.echo.deserialization;

import com.github.pada.echo.definition.EventDefinition;

import java.util.List;

abstract public class AbstractStringDeserializer implements LineDeserializer {
    protected final List<EventDefinition> eventDefinitionList;

    protected AbstractStringDeserializer(List<EventDefinition> eventDefinitionList) {
        this.eventDefinitionList = eventDefinitionList;
    }
}
