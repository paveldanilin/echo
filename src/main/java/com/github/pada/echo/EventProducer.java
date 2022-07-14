package com.github.pada.echo;

import com.github.pada.echo.deserialization.LineDeserializer;
import com.github.pada.echo.io.Line;
import com.github.pada.echo.io.reader.LineReader;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class EventProducer implements Runnable {
    private final BlockingQueue<Event> eventQueue;
    private final LineReader lineReader;

    public EventProducer(BlockingQueue<Event> eventQueue, LineReader lineReader, LineDeserializer lineDeserializer) {
        this.eventQueue = eventQueue;
        this.lineReader = lineReader;
        this.lineReader.setConsumer(new LineConsumer(eventQueue, lineDeserializer));
    }

    @Override
    public void run() {
        this.lineReader.run();
    }

    public BlockingQueue<Event> getEventQueue() {
        return this.eventQueue;
    }

    /**
     * Line -> Event
     */
    private static class LineConsumer implements Consumer<Line> {
        private final LineDeserializer lineDeserializer;
        private final BlockingQueue<Event> events;

        public LineConsumer(BlockingQueue<Event> events, LineDeserializer lineDeserializer) {
            this.events = events;
            this.lineDeserializer = lineDeserializer;
        }

        @Override
        public void accept(Line line) {
            Event event = this.lineDeserializer.deserialize(line);
            if (event != null) {
                try {
                    this.events.put(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
