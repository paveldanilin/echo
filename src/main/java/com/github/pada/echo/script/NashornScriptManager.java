package com.github.pada.echo.script;


import javax.script.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NashornScriptManager implements ScriptManager {
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
    private final Map<String, CompiledScript> scriptMap = new ConcurrentHashMap<>();


    @Override
    public String compile(String script) throws ScriptException {
        return this.compile(UUID.randomUUID().toString(), script);
    }

    @Override
    public String compile(String scriptId, String script) throws ScriptException {
        CompiledScript compiledScript = ((Compilable)this.scriptEngine).compile(script);
        this.scriptMap.put(scriptId, compiledScript);
        return scriptId;
    }

    @Override
    public Object executeFunction(String scriptId, String func, Object... args) throws ScriptException, NoSuchMethodException {
        return this.executeFunction(scriptId, func, null, args);
    }

    @Override
    public Object executeFunction(String scriptId, String func, Map<String, Object> context, Object... args) throws ScriptException, NoSuchMethodException {
        CompiledScript compiledScript = this.scriptMap.get(scriptId);
        if (compiledScript == null) {
            return null;
        }

        if (context == null) {
            compiledScript.eval();
        } else {
            Bindings scriptContext = this.scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            scriptContext.putAll(context);
            compiledScript.eval(scriptContext);
        }

        return ((Invocable) compiledScript.getEngine()).invokeFunction(func, args);
    }
}
