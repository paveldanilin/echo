package com.github.pada.echo;

import com.github.pada.echo.channel.AbstractChannel;
import com.github.pada.echo.channel.Channel;
import com.github.pada.echo.channel.BatchedChannel;
import com.github.pada.echo.definition.EventFieldType;
import com.github.pada.echo.definition.EventDefinition;
import com.github.pada.echo.definition.EventFieldDefinition;
import com.github.pada.echo.deserialization.PatternDeserializer;
import com.github.pada.echo.io.reader.file.SimpleLineReader;
import com.github.pada.echo.io.writer.FileWriter;
import com.github.pada.echo.script.NashornScriptManager;
import com.github.pada.echo.script.ScriptManager;
import com.github.pada.echo.serialization.EventSerializer;
import com.github.pada.echo.serialization.JsonEventSerializer;

import javax.script.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ScriptException, NoSuchMethodException {

        EventSerializer serializer = new JsonEventSerializer();//new MustacheEventSerializer("INSERT INTO mylog VALUES ('{{datetime}}', '{{ip}}');");


        String inputFile = "./data/nginx/access.log";
        //String inputFile = "./data/jsonl/service.log";

        // Queue
        BlockingQueue<Event> queue = new LinkedBlockingQueue<>(5000);

        AbstractChannel ch = BatchedChannel
                .newBuilder()
                .withWriter(new FileWriter("./out.me"))
                .withSerializer(serializer)
                .setQueueSize(3000)
                .setConcurrency(3)
                .setPeriodMillis(2000)
                .setBatchSize(2)
                .build();
        ch.start();

        // ?
        Map<String, Channel> routing = new HashMap<>();
        routing.put("nginx.access", ch);

        // Script
        ScriptManager scriptManager = new NashornScriptManager();
        //scriptManager.compile("filter", "function filter(event) { return event.getField('ip') === '217.138.222.101'; }");
        scriptManager.compile("filter", "function filter(event) { return event.getField('ip') !== '123'; }");

        // Processor
        Dispatcher dispatcher = new Dispatcher(queue, routing, scriptManager);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(dispatcher);

        // Def
        List<EventDefinition> eventDefinitionList = new ArrayList<>();
        eventDefinitionList.add(createPatternEventDef());

        // Producer
        EventProducer producer = new EventProducer(queue,
                new SimpleLineReader(inputFile),
                new PatternDeserializer(eventDefinitionList));
        producer.run();
        System.out.println("ASd");

        executorService.shutdown();

        /*
        ScriptManager scriptManager = new NashornScriptManager();
        String id = scriptManager.compile("function sum(a, b) { " +
                "if (typeof a === 'undefined') {\n" +
                        "  a = adef;\n" +
                        "} \n" +
                "if (typeof b === 'undefined') {\n" +
                "  b = bdef;\n" +
                "} \n" +
                " return a + b;}");


        scriptManager.compile("zz", "function gg(e) { print(e.getFields()); }");

        Map<String, Object> context = new HashMap<>();
        context.put("adef", 10);
        context.put("bdef", 20);
        Object result = scriptManager.executeFunction(id, "sum", context, 5);
        System.out.println(result);
        Object result2 = scriptManager.executeFunction(id, "sum", 1,2);
        System.out.println(result2);

        Map<String, Object> context2 = new HashMap<>();
        context2.put("adef", 50);
        context2.put("bdef", 50);
        Object result3 = scriptManager.executeFunction(id, "sum", context2);
        System.out.println(result3);

        Map<String, Object> fields = new HashMap<>();
        fields.put("bzz", 123);
        Event event = new Event(0, 100, null, fields);
        scriptManager.executeFunction("zz", "gg", event);
         */

        /*
        String inputFile = "./data/nginx/access.log";
        //String inputFile = "./data/jsonl/service.log";

        List<EventDefinition> eventFieldDefinitions = new ArrayList<>();
        eventFieldDefinitions.add(createPatternEventDef());

        LineDeserializer deserializer = new PatternDeserializer(eventFieldDefinitions);

        LineReader reader = new FileLineReader(inputFile);

        Map<String, Channel> routing = new HashMap<>();
        routing.put("nginx.access", new ConsoleChannel());

        BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>(5000);

        Processor processor = new Processor(queue, routing);
        AtomicLong offset = new AtomicLong();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(processor);

        reader.forEach(line -> {
            LogEvent logEvent = deserializer.deserialize(line);
            if (logEvent != null) {
                try {
                    queue.put(logEvent);
                    offset.set(logEvent.getOffsetEnd());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Monitor monitor = new DefaultMonitor("./data/nginx", "access\\.log");
        monitor.watch(filename -> {
            try {
                long fs = Files.size(Paths.get(filename));
                System.out.println(fs);

                reader.forEach(line -> {
                    LogEvent logEvent = deserializer.deserialize(line);
                    if (logEvent != null) {
                        try {
                            queue.put(logEvent);
                            offset.set(logEvent.getOffsetEnd());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, offset.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
         */


        /*
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval("function handleEvent(event) {" +
                "print(event);" +
                //"return 'Hello ' + name" +
                "}");
        engine.eval("function zzz(event) {" +
                "print(event + '123');" +
                //"return 'Hello ' + name" +
                "}");
        Invocable invocable = (Invocable) engine;

        try {
            Object funcResult = invocable.invokeFunction("handleEvent", "test");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        processor.process();*/


        /*
        processor.next(event -> {
            System.out.println(event.getOffset() + ":" + event.getFields());
            try {
                Object funcResult = invocable.invokeFunction("handleEvent", event);
            } catch (ScriptException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }, 0);*/

        // Watch file changes
        // Main.watch(processor);
    }
/*
    private static EventDefinition createJsonNormalEventDef() {
        List<EventFieldDefinition> fields = new ArrayList<>();

        fields.add(new EventFieldDefinition("eventTime", EventFieldType.STRING));
        fields.add(new EventFieldDefinition("message", EventFieldType.STRING));

        return new EventDefinition("json.normal", fields,
                new HashMap<String, String>() {{
                    put("event_type_field", "type");
                    //put("event_type_mode", "eq");
                    put("event_type_value", "normal");
                }}
        );
    }
*/
    /*
    private static EventDefinition createJsonExtendedEventDef() {
        List<EventFieldDefinition> fields = new ArrayList<>();

        fields.add(new EventFieldDefinition("eventTime", EventFieldType.STRING));
        fields.add(new EventFieldDefinition("message", EventFieldType.STRING));
        fields.add(new EventFieldDefinition("ignored_field", EventFieldType.ARRAY));

        return new EventDefinition("json.extended", fields,
                new HashMap<String, String>() {{
                    put("event_type_field", "type");
                    //put("event_type_mode", "eq");
                    put("event_type_value", "extended");
                }}
        );
    }
*/
    private static EventDefinition createPatternEventDef() {
        List<EventFieldDefinition> fields = new ArrayList<>();

        fields.add(new EventFieldDefinition("ip", EventFieldType.STRING, new HashMap<String, String>() {{
            put("group_index", "1");
        }}));
        fields.add(new EventFieldDefinition("remote_logname", EventFieldType.STRING, new HashMap<String, String>() {{
            put("group_index", "2");
        }}));
        fields.add(new EventFieldDefinition("remote_username", EventFieldType.STRING, new HashMap<String, String>() {{
            put("group_index", "3");
        }}));
        fields.add(new EventFieldDefinition("datetime", EventFieldType.DATE, new HashMap<String, String>() {{
            put("group_index", "4");
        }}));
        fields.add(new EventFieldDefinition("datetime", EventFieldType.DATE, new HashMap<String, String>() {{
            put("group_index", "4");
        }}));
        fields.add(new EventFieldDefinition("session_id", EventFieldType.STRING, new HashMap<String, String>() {{
            put("pattern", "\\[sessionId=(\\d{1,})\\]");
        }}));
        fields.add(new EventFieldDefinition("request_id", EventFieldType.STRING, new HashMap<String, String>() {{
            put("pattern", "\\[requestId=(\\d{1,})\\]");
        }}));
        fields.add(new EventFieldDefinition("str", EventFieldType.STRING, new HashMap<String, String>() {{
            put("pattern", "\\[str=(\\w+)\\]");
        }}));
        fields.add(new EventFieldDefinition("message", EventFieldType.STRING));

        return new EventDefinition(
                "nginx.access",
                fields,
                new HashMap<String, String>() {{
                    put("pattern", "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) (\\w+|\\-) (\\w|\\-) (\\[.*\\]) (\\\"[\\w\\s\\d\\/\\.-]+\\\") (\\d{1,3}) (\\d+) (\\\"[\\w\\d-:\\.\\/]+\\\") (\\\"[\\s\\w\\d\\/\\.;:\\(\\)_,-]+\\\")");
                }}
        );
    }
}
