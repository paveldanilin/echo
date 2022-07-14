package com.github.pada.echo.script;

import javax.script.ScriptException;

public interface ScriptManager extends ScriptExecutor {
    String compile(String script) throws ScriptException;
    String compile(String scriptId, String script) throws ScriptException;
}
