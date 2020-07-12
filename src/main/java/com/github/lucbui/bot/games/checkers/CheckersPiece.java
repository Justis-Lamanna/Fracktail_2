package com.github.lucbui.bot.games.checkers;

import com.github.lucbui.bot.games.Id;
import com.github.lucbui.bot.games.Piece;

public class CheckersPiece implements Piece<Checkers> {
    private CheckersPieceType type;
    private final CheckersColor color;
    private final Id id;

    public CheckersPiece(CheckersPieceType type, CheckersColor color, int number) {
        this.type = type;
        this.color = color;
        this.id = new Id(color.name() + "-" + number);
    }

    public void king() {
        this.type = CheckersPieceType.KING;
    }

    public CheckersPieceType getType() {
        return type;
    }

    public CheckersColor getColor() {
        return color;
    }

    @Override
    public Id id() {
        return id;
    }
}
