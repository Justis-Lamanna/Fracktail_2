package com.github.lucbui.magic.command.context;

import com.github.lucbui.magic.command.func.BotMessageBehavior;

import java.util.HashMap;
import java.util.Map;

public class CommandCreateContext {
    private final String name;
    private final String[] aliases;
    private final BotMessageBehavior behavior;
    private Map<String, Object> properties;

    public CommandCreateContext(String name, String[] aliases, BotMessageBehavior behavior) {
        this.name = name;
        this.aliases = aliases;
        this.behavior = behavior;
        this.properties = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public BotMessageBehavior getBehavior() {
        return behavior;
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
