package com.github.lucbui.bot.services.translate;

public class TranslateKeyArgs {
    private final String key;
    private final Object[] args;

    public TranslateKeyArgs(String key, Object... args) {
        this.key = key;
        this.args = args;
    }

    public String getKey() {
        return key;
    }

    public Object[] getArgs() {
        return args;
    }
}
