package com.github.lucbui.bot.games;

import java.util.Optional;

public class MoveLegality {
    private final boolean validMove;
    private String message;
    private Object[] params;

    protected MoveLegality(boolean validMove, String message, Object[] params) {
        this.validMove = validMove;
        this.message = message;
        this.params = params;
    }

    public static MoveLegality illegal(String message, Object... params) {
        return new MoveLegality(false, message, params);
    }

    public static MoveLegality legal() {
        return new MoveLegality(true, null, new Object[0]);
    }

    public static MoveLegality legalIf(boolean legal, String illegalMsg, Object... paramsIfIllegal) {
        return legal ? legal() : illegal(illegalMsg, paramsIfIllegal);
    }

    public boolean isValidMove() {
        return validMove;
    }

    public Optional<String> getMessages() {
        return Optional.ofNullable(message);
    }

    public Object[] getParams() {
        return params;
    }
}
