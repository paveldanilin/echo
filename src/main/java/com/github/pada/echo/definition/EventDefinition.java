package com.github.pada.echo.definition;

import com.github.pada.echo.util.Parameters;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventDefinition extends Parameters {
    private final String id;
    private final List<EventFieldDefinition> fieldDefinitions;

    public EventDefinition(String id, List<EventFieldDefinition> fieldDefinitions, Map<String, String> parameters) {
        super(parameters);
        this.id = id;
        this.fieldDefinitions = fieldDefinitions;
    }

    public String getId() {
        return this.id;
    }

    public EventFieldDefinition getFieldDefinition(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        Optional<EventFieldDefinition> fieldDefinition = this.fieldDefinitions
                .stream()
                .filter(f -> f.getFieldName().equals(fieldName))
                .findFirst();
        return fieldDefinition.orElse(null);
    }

    public List<String> getFieldNames() {
        return this.fieldDefinitions.stream().map(EventFieldDefinition::getFieldName).collect(Collectors.toList());
    }

    public boolean anyFieldWithParameter(String name) {
        Optional<EventFieldDefinition> fieldDefinition = this.fieldDefinitions
                .stream()
                .filter(f -> f.hasParameter(name))
                .findFirst();
        return fieldDefinition.isPresent();
    }

    public List<EventFieldDefinition> getFieldsWithParameter(String name) {
        return this.fieldDefinitions.stream().filter(f -> f.hasParameter(name)).collect(Collectors.toList());
    }
}
