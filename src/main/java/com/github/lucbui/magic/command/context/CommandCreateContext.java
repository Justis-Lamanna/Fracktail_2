package com.github.lucbui.magic.command.context;

import java.util.HashMap;
import java.util.Map;

public class CommandCreateContext {
    private Map<String, Object> properties;

    public CommandCreateContext() {
        this.properties = new HashMap<>();
    }

    public void set(String key, Object obj) {
        this.properties.put(key, obj);
    }

    public <T> T get(String key, Class<T> clazz) {
        if(properties.containsKey(key)){
            return clazz.cast(properties.get(key));
        }
        return null;
    }
}
