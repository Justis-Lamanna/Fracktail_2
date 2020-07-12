package com.github.lucbui.bot.games.checkers.action;

import com.github.lucbui.bot.games.Position;

public enum Orientation {
    NORTHEAST(-1, 1),
    SOUTHEAST(1, 1),
    NORTHWEST(-1, -1),
    SOUTHWEST(1, -1);

    private final int rowMultiplier;
    private final int colMultiplier;

    Orientation(int rowMultiplier, int colMultiplier) {
        this.rowMultiplier = rowMultiplier;
        this.colMultiplier = colMultiplier;
    }

    public Position from(Position start, int numSpaces) {
        return new Position(
                start.getRow() + (numSpaces * rowMultiplier),
                start.getCol() + (numSpaces * colMultiplier)
        );
    }

    public static Orientation fromRelative(Position.Relative relative) {
        int dR = relative.getDeltaRow();
        int dC = relative.getDeltaCol();
        if(dR == 0 || dC == 0) {
            throw new IllegalArgumentException("Position points cardinally (N, S, E, or W)");
        }
        if(dR < 0) {
            if(dC < 0) {
                return Orientation.NORTHWEST;
            } else {
                return Orientation.NORTHEAST;
            }
        } else {
            if(dC < 0) {
                return Orientation.SOUTHWEST;
            } else {
                return Orientation.SOUTHEAST;
            }
        }
    }
}
