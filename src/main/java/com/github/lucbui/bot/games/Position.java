package com.github.lucbui.bot.games;

import java.util.Objects;

public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Relative distance(Position p2) {
        return new Relative(p2.row - row, p2.col - col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row &&
                col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public static class Relative {
        private final int dRow;
        private final int dCol;

        public Relative(int dRow, int dCol) {
            this.dRow = dRow;
            this.dCol = dCol;
        }

        public int getDeltaRow() {
            return dRow;
        }

        public int getDeltaCol() {
            return dCol;
        }
    }
}
