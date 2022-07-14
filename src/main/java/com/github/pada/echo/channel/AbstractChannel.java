package com.github.pada.echo.channel;

import com.github.pada.echo.Event;
import com.github.pada.echo.io.writer.Writer;
import com.github.pada.echo.serialization.EventSerializer;
import com.github.pada.echo.util.Parameters;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

abstract public class AbstractChannel implements Channel {
    private final Writer writer;
    private final EventSerializer eventSerializer;
    protected final BlockingQueue<Event> outputQueue;
    private final int concurrency;
    private ExecutorService executorService;
    private volatile boolean run;
    private final Parameters parameters;

    public AbstractChannel(Writer writer, EventSerializer eventSerializer, int queueSize, int concurrency) {
        this.writer = writer;
        this.eventSerializer = eventSerializer;
        this.outputQueue = new LinkedBlockingQueue<>(queueSize);
        this.concurrency = concurrency;
        this.run = false;
        this.parameters = new Parameters();
    }

    abstract protected void doWrite() throws InterruptedException;

    /**
     * If runs out of capacity, returns false.
     * If the event can be accepted, returns true.
     * @param event The event
     * @return boolean
     */
    @Override
    public boolean accept(Event event) {
        return this.outputQueue.offer(event);
    }

    public void start() {
        this.run = true;
        this.executorService = Executors.newFixedThreadPool(this.concurrency);
        for (int i = 0; i < this.concurrency; i++) {
            this.executorService.execute(() -> {
                try {
                    while (this.run) {
                        this.doWrite();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void stop() {
        this.run = false;
        this.executorService.shutdown();
    }

    protected void writeToChannel(Event event) {
        this.writer.write(
                this.parameters.getAll(),
                this.eventSerializer.serialize(event)
        );
    }

    protected void writeToChannel(List<Event> batch) {
        this.writer.write(this.parameters.getAll(),
                batch.stream()
                        .map(this.eventSerializer::serialize)
                        .collect(Collectors.toList())
        );
    }
}
