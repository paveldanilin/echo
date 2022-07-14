package com.github.pada.echo.channel;

import com.github.pada.echo.Event;
import com.github.pada.echo.io.writer.Writer;
import com.github.pada.echo.serialization.EventSerializer;

import java.util.ArrayList;
import java.util.List;

public class BatchedChannel extends AbstractChannel {
    private final long periodMillis;
    private final int batchSize;

    public BatchedChannel(Writer writer, EventSerializer eventSerializer, int queueSize, int batchSize) {
        this(writer, eventSerializer, queueSize, 1, 0, batchSize);
    }

    public BatchedChannel(Writer writer, EventSerializer eventSerializer, int queueSize, int concurrency, int batchSize) {
        this(writer, eventSerializer, queueSize, concurrency, 0, batchSize);
    }

    public BatchedChannel(Writer writer, EventSerializer eventSerializer, int queueSize, int concurrency, long periodMillis, int batchSize) {
        super(writer, eventSerializer, queueSize, concurrency);
        this.periodMillis = periodMillis;
        this.batchSize = batchSize;
    }

    @Override
    protected void doWrite() throws InterruptedException {
        List<Event> batch = this.readBatch();
        if (batch.size() > 0) {
            this.writeToChannel(batch);
        }
        if (this.periodMillis > 0) {
            Thread.sleep(this.periodMillis);
        }
    }

    private List<Event> readBatch() {
        List<Event> batch = new ArrayList<>(this.batchSize);
        for (int i = 0; i < this.batchSize; i++) {
            Event event = this.outputQueue.poll();
            if (event == null) {
                break;
            }
            batch.add(event);
        }
        return batch;
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
        private long periodMillis = 0;
        private int batchSize = 1;

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

        public Builder setPeriodMillis(long periodMillis) {
            this.periodMillis = periodMillis;
            return this;
        }

        public Builder setBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public BatchedChannel build() {
            return new BatchedChannel(this.writer, this.eventSerializer, this.queueSize, this.concurrency, this.periodMillis, this.batchSize);
        }
    }
}
