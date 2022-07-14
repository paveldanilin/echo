package com.github.pada.echo;

import com.github.pada.echo.channel.Channel;
import com.github.pada.echo.script.ScriptExecutor;

import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Dispatcher implements Runnable {
    private final BlockingQueue<Event> queue;
    private final Map<String, Channel> channelMap;
    private final ScriptExecutor scriptExecutor;

    public Dispatcher(BlockingQueue<Event> queue, Map<String, Channel> channelMap, ScriptExecutor scriptExecutor) {
        this.queue = queue;
        this.channelMap = channelMap;
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public void run() {
        for (;;) {
            try {
                Event event = this.queue.take();
                Boolean r = (Boolean) this.scriptExecutor.executeFunction("filter", "filter", event);
                if (!r) {
                    continue;
                }
                if (this.channelMap.containsKey(event.getDefinition().getId())) {
                    // TODO: add strategy for handling situation when chanel is not able to accept event
                    this.channelMap.get(event.getDefinition().getId()).accept(event);
                }
            } catch (InterruptedException e) {
                break;
            } catch (ScriptException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
