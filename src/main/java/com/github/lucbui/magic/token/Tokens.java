package com.github.lucbui.magic.token;

/**
 * Class encapsulating message tokens
 */
public class Tokens {
    private final String full;
    private final String prefix;
    private final String command;
    private final String paramString;
    private final String[] params;

    /**
     * Create the tokens
     * @param full The full message
     * @param prefix The prefix
     * @param command The command
     * @param paramString All parameters, as a string
     * @param params The params, broken up into an array.
     */
    public Tokens(String full, String prefix, String command, String paramString, String[] params) {
        this.full = full;
        this.prefix = prefix;
        this.command = command;
        this.paramString = paramString;
        this.params = params;
    }

    /**
     *
     * @return the full message
     */
    public String getFull() {
        return full;
    }

    /**
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @return The command
     */
    public String getCommand() {
        return command;
    }

    /**
     *
     * @return The params, as a string
     */
    public String getParamString() {
        return paramString;
    }

    /**
     *
     * @return The params, as an array.
     */
    public String[] getParams() {
        return params;
    }

    /**
     * Get the parameter at a given index.
     * If the index exceeds bounds, null is returned.
     * @param idx The index to retrieve
     * @return The parameter, or null.
     */
    public String getParam(int idx) {
        return idx < params.length ? params[idx] : null;
    }
}
