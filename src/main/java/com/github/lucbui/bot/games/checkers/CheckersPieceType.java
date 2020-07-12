package com.github.lucbui.bot.games.checkers;

import java.util.concurrent.atomic.AtomicInteger;

public enum CheckersPieceType {
    MEN,
    KING;

    private static final AtomicInteger ctr = new AtomicInteger(0);

    public CheckersPiece ofColor(CheckersColor color) {
        return new CheckersPiece(this, color, ctr.getAndIncrement());
    }
}
