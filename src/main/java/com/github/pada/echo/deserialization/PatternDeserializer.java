package com.github.pada.echo.deserialization;

import com.github.pada.echo.definition.EventDefinition;
import com.github.pada.echo.definition.EventFieldDefinition;
import com.github.pada.echo.io.Line;
import com.github.pada.echo.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternDeserializer extends AbstractStringDeserializer {

    private final static int DEFAULT_BUFFER_CAPACITY = 4 * 1024;
    private final static String DEFAULT_PATTERN_OPTION = "pattern";

    private final StringBuilder buffer;
    private MatchedDefinition<Matcher, EventDefinition> matchedEventDef;
    private long startOffset;
    private final Map<String, Pattern> patternMap = new HashMap<>();
    private final String patternOptionName;

    public PatternDeserializer(int bufferCapacity, List<EventDefinition> eventDefinitionList, String patternOption) {
        super(eventDefinitionList);
        this.buffer = new StringBuilder(bufferCapacity <= 0 ? DEFAULT_BUFFER_CAPACITY : bufferCapacity);
        this.patternOptionName = patternOption != null ? patternOption : DEFAULT_PATTERN_OPTION;
        for (EventDefinition def : eventDefinitionList) {
            this.patternMap.put(
                    def.getId(),
                    Pattern.compile(def.getParameter(this.patternOptionName))
            );
        }
    }

    public PatternDeserializer(List<EventDefinition> eventDefinitionList) {
        this(DEFAULT_BUFFER_CAPACITY, eventDefinitionList, null);
    }

    public PatternDeserializer(List<EventDefinition> eventDefinitionList, String patternOption) {
        this(DEFAULT_BUFFER_CAPACITY, eventDefinitionList, patternOption);
    }

    @Override
    public Event deserialize(Line line) {
        if (line == null || line.isEmpty()) {
            return this.popEventFromBuffer();
        }

        MatchedDefinition<Matcher, EventDefinition> def = this.findEventDefinition(line.getText());
        if (def == null && this.matchedEventDef != null) {
            this.buffer.append(line.getText());
            return null;
        }

        if (this.matchedEventDef == null) {
            this.matchedEventDef = def;
            this.startOffset = line.getOffset();
            this.buffer.setLength(0);
            this.buffer.append(line.getText());
            return null;
        }

        Event logEvent = this.createLogEvent(this.startOffset, this.buffer.toString(), this.matchedEventDef);
        this.buffer.setLength(0);
        this.buffer.append(line.getText());
        this.startOffset = line.getOffset();
        this.matchedEventDef = def;

        return logEvent;
    }

    private Event popEventFromBuffer() {
        if (this.matchedEventDef == null) {
            return null;
        }
        Event event = this.createLogEvent(this.startOffset, this.buffer.toString(), this.matchedEventDef);
        this.buffer.setLength(0);
        this.matchedEventDef = null;
        this.startOffset = 0;
        return event;
    }

    private Event createLogEvent(long offsetBegin, String text, MatchedDefinition<Matcher, EventDefinition> matchedEventDef) {
        Map<String, Object> eventFields = new HashMap<>();
        this.populateFieldsFromMatcher(eventFields, matchedEventDef.getDefinition(), matchedEventDef.getMatcher());
        this.populateFieldsFromText(eventFields, matchedEventDef.getDefinition(), text);
        return new Event(new Event.Offset(offsetBegin, offsetBegin + text.length()),
                matchedEventDef.getDefinition(),
                eventFields
        );
    }

    private void populateFieldsFromText(Map<String, Object> fields, EventDefinition eventDefinition, String text) {
        for (EventFieldDefinition fieldDefinition : eventDefinition.getFieldsWithParameter(this.patternOptionName)) {
            // TODO: cache compiled patterns
            Pattern pattern = Pattern.compile(fieldDefinition.getParameter(this.patternOptionName));
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                fields.put(fieldDefinition.getFieldName(), matcher.group(1));
            }
        }
    }

    private void populateFieldsFromMatcher(Map<String, Object> fields, EventDefinition eventDefinition, Matcher matcher) {
        for (String fieldName : eventDefinition.getFieldNames()) {
            Integer groupIndex = eventDefinition.getFieldDefinition(fieldName).getIntParameter("group_index");
            if (groupIndex != null) {
                fields.put(fieldName, matcher.group(groupIndex));
            }
        }
    }

    private MatchedDefinition<Matcher, EventDefinition> findEventDefinition(String rawString) {
        for (EventDefinition eventDefinition : this.eventDefinitionList) {
            Matcher matcher = this.patternMap.get(eventDefinition.getId()).matcher(rawString);
            if (matcher.find()) {
                return new MatchedDefinition<>(matcher, eventDefinition);
            }
        }
        return null;
    }

    private static class MatchedDefinition<M, D> {
        private final M matcher;
        private final D definition;

        public MatchedDefinition(M m, D d) {
            this.matcher = m;
            this.definition = d;
        }

        public M getMatcher() {
            return this.matcher;
        }

        public D getDefinition() {
            return this.definition;
        }
    }
}
