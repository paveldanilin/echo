package com.github.pada.echo.io;

final public class Line {
    private final String text;
    private final long offset;

    public Line() {
        this.text = null;
        this.offset = -1;
    }

    public Line(String str, long offset) {
        this.text = str;
        this.offset = offset;
    }

    public long getOffset() {
        return this.offset;
    }

    public String getText() {
        return this.text;
    }

    public boolean isEmpty() {
        return this.text == null && this.offset == -1;
    }
}
