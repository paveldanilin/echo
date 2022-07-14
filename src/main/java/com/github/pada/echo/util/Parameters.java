package com.github.pada.echo.util;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
    protected final Map<String, String> parameters;

    public Parameters() {
        this.parameters = new HashMap<>();
    }

    public Parameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public boolean hasParameter(String name) {
        return this.parameters.containsKey(name);
    }

    public String getParameter(String name) {
        return this.parameters.get(name);
    }

    public String getParameterOrDefault(String name, String defaultValue) {
        return this.parameters.getOrDefault(name, defaultValue);
    }

    public Integer getIntParameter(String name) {
        String value = this.getParameter(name);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public Map<String, String> getAll() {
        return this.parameters;
    }
}
