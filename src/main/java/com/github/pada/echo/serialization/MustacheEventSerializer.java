package com.github.pada.echo.serialization;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.pada.echo.Event;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.StringReader;
import java.io.StringWriter;

public class MustacheEventSerializer implements EventSerializer {
    private final ThreadLocal<StringWriter> stringWriter = ThreadLocal.withInitial(() -> new StringWriter(1024));
    private final Mustache template;

    public MustacheEventSerializer(String template) {
        this.template = this.compile(DigestUtils.md5Hex(template), template);
    }

    @Override
    public String serialize(Event event) {
        String s = this.template.execute(this.stringWriter.get(), event.getFields()).toString();
        this.stringWriter.get().getBuffer().setLength(0);
        return s;
    }

    private Mustache compile(String name, String template) {
        return new DefaultMustacheFactory().compile(new StringReader(template), name);
    }
}
