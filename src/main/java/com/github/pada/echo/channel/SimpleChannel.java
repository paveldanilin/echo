package com.github.pada.echo.channel;

import com.github.pada.echo.Event;
import com.github.pada.echo.io.writer.Writer;
import com.github.pada.echo.serialization.EventSerializer;

public class SimpleChannel extends AbstractChannel {
    public SimpleChannel(Writer writer, EventSerializer eventSerializer, int queueSize) {
        super(writer, eventSerializer, queueSize, 1);
    }

    public SimpleChannel(Writer writer, EventSerializer eventSerializer, int queueSize, int concurrency) {
        super(writer, eventSerializer, queueSize, concurrency);
    }

    @Override
    protected void doWrite() throws InterruptedException {
        Event event = this.outputQueue.take(); // blocks the current thread if there is nothing to take from a queue
        this.writeToChannel(event);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Builder
     */
    public static class Builder {
        private Writer writer;
        private EventSerializer eventSerializer;
        private int queueSize = 5000;
        private int concurrency = 1;

        private Builder() {
        }

        public Builder withWriter(Writer writer) {
            this.writer = writer;
            return this;
        }

        public Builder withSerializer(EventSerializer eventSerializer) {
            this.eventSerializer = eventSerializer;
            return this;
        }

        public Builder setQueueSize(int queueSize) {
            this.queueSize = queueSize;
            return this;
        }

        public Builder setConcurrency(int concurrency) {
            this.concurrency = concurrency;
            return this;
        }

        public SimpleChannel build() {
            if (this.writer == null) {
                throw new RuntimeException("Writer should be defined");
            }
            if (this.eventSerializer == null) {
                throw new RuntimeException("Event serializer should be defined");
            }
            return new SimpleChannel(this.writer, this.eventSerializer, this.queueSize, this.concurrency);
        }
    }
}
