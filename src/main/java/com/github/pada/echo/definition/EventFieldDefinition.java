package com.github.pada.echo.definition;

import com.github.pada.echo.util.Parameters;

import java.util.HashMap;
import java.util.Map;

public class EventFieldDefinition extends Parameters {
    private final String fieldName;
    private final String dataType;
    private final Object defaultValue;

    public EventFieldDefinition(String fieldName, String dataType) {
        super(new HashMap<>());
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.defaultValue = null;
    }

    public EventFieldDefinition(String fieldName, String dataType, Map<String, String> options) {
        super(options);
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.defaultValue = null;
    }

    public EventFieldDefinition(String fieldName, String dataType, Object defaultValue, Map<String, String> options) {
        super(options);
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getDataType() {
        return this.dataType;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
