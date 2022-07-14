package com.github.pada.echo.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pada.echo.io.Line;
import com.github.pada.echo.Event;
import com.github.pada.echo.definition.EventDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDeserializer extends AbstractStringDeserializer {

    private final TypeReference<HashMap<String,Object>> mapTypeRef = new TypeReference<HashMap<String,Object>>() {};

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonDeserializer(List<EventDefinition> eventDefinitionList) {
        super(eventDefinitionList);
    }

    @Override
    public Event deserialize(Line line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        try {
            Map<String, Object> jsonData = this.objectMapper.readValue(line.getText(), this.mapTypeRef);
            EventDefinition def = this.findDefinition(jsonData);
            if (def == null) {
                return null;
            }

            Event event = new Event(new Event.Offset(line.getOffset(), line.getOffset() + line.getText().length()),
                    def,
                    new HashMap<>()
            );
            for (String fieldName : def.getFieldNames()) {
                event.getFields().put(fieldName, jsonData.getOrDefault(fieldName, ""));
            }

            return event;
        } catch (JsonProcessingException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    private EventDefinition findDefinition(Map<String, Object> jsonData) {
        for (EventDefinition eventDefinition : this.eventDefinitionList) {
            if (eventDefinition.hasParameter("event_type_field")) {
                // TODO: regex
                // EQ
                String typeField = eventDefinition.getParameter("event_type_field");
                //String typeMode = eventDefinition.getOptionOrDefault("event_type_mode", "eq");
                String typeValue = eventDefinition.getParameterOrDefault("event_type_value", "");
                if (jsonData.containsKey(typeField) && jsonData.get(typeField).equals(typeValue)) {
                    return eventDefinition;
                }
            }
        }
        return null;
    }
}
