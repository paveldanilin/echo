package com.github.pada.echo;

import com.github.pada.echo.definition.EventDefinition;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final public class Event {
    private final long time;
    private String source;
    private final Offset offset;
    private final Map<String, Object> fields;
    private final EventDefinition eventDefinition;
    private final Set<String> tags;

    public Event(Offset offset, EventDefinition eventDefinition, Map<String, Object> fields) {
        this.time = System.currentTimeMillis();
        this.offset = offset;
        this.eventDefinition = eventDefinition;
        this.fields = fields;
        this.tags = new HashSet<>();
    }

    public long getTime() {
        return this.time;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Offset getOffset() {
        return this.offset;
    }

    public EventDefinition getDefinition() {
        return this.eventDefinition;
    }

    public Map<String, Object> getFields() {
        return this.fields;
    }

    public Object getField(String fieldName) {
        return this.fields.get(fieldName);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * Event offset
     */
    public static class Offset {
        private final long begin;
        private final long end;

        public Offset(long begin, long end) {
            this.begin = begin;
            this.end = end;
        }

        public long getBegin() {
            return this.begin;
        }

        public long getEnd() {
            return this.end;
        }
    }
}
