package com.github.pada.echo.io.reader.file;

import com.github.pada.echo.io.Line;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.function.Consumer;

public class TailLineReader extends AbstractFileReader {
    private Tailer tailer;

    public TailLineReader(String fileName) {
        super(fileName);
    }

    @Override
    public void stop() {
        if (this.isRunning() && this.tailer != null) {
            super.stop();
            this.tailer.stop();
        }
    }

    @Override
    public void run() {
        this.beforeRun();
        if (this.tailer == null) {
            this.tailer = new Tailer(new File(this.fileName), new LineListener(this.consumer), 3000, false, true);
        }
        this.tailer.run();
    }

    private static class LineListener extends TailerListenerAdapter {
        private final Consumer<Line> consumer;

        public LineListener(Consumer<Line> consumer) {
            this.consumer = consumer;
        }

        public void handle(String s) {
            this.consumer.accept(new Line(s, 0));
        }

        public void endOfFileReached() {
            this.consumer.accept(new Line());
        }
    }
}
