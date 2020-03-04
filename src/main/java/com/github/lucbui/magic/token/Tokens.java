package com.github.lucbui.magic.token;

public class Tokens {
    private final String full;
    private final String prefix;
    private final String command;
    private final String paramString;
    private final String[] params;

    public Tokens(String full, String prefix, String command, String paramString, String[] params) {
        this.full = full;
        this.prefix = prefix;
        this.command = command;
        this.paramString = paramString;
        this.params = params;
    }

    public String getFull() {
        return full;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getCommand() {
        return command;
    }

    public String getParamString() {
        return paramString;
    }

    public String[] getParams() {
        return params;
    }

    public String getParam(int idx) {
        return idx < params.length ? params[idx] : null;
    }
}