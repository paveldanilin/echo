package com.github.pada.echo.script;

import javax.script.ScriptException;
import java.util.Map;

public interface ScriptExecutor {
    Object executeFunction(String scriptId, String func, Object... args) throws ScriptException, NoSuchMethodException;
    Object executeFunction(String scriptId, String func, Map<String, Object> context, Object... args) throws ScriptException, NoSuchMethodException;
}
