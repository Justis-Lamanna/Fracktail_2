package com.github.lucbui.bot.games;

import org.apache.commons.lang3.StringUtils;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BoardGameInstance<
        T extends Game,
        P extends Piece<T>,
        U extends Player<T>,
        THIS extends BoardGameInstance<T, P, U, THIS>>
            extends AbstractGameInstance<T, U, THIS> {
    protected Board<T, P> board;

    protected BoardGameInstance(List<U> players, int turnCounter, Board<T, P> board, Deque<TurnAction> actions) {
        super(players, turnCounter, actions);
        this.board = board;
    }

    protected BoardGameInstance(List<U> players, int turnCounter, Board<T, P> board) {
        super(players, turnCounter);
        this.board = board;
    }

    public Board<T, P> getBoard() {
        return board;
    }

    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        int rowHeaderSize = stringSize(board.getHeight());
        sb.append(StringUtils.repeat('❎', rowHeaderSize)).append(" ");
        for(int c = 0; c < board.getWidth(); c++) {
            sb.append(getHeadingForColumn(c)).append(" ");
        }
        sb.append("\n");
        for(int row = 0; row < board.getHeight(); row++) {
            sb.append(getHeadingForRow(row, rowHeaderSize)).append(" ");
            for(int col = 0; col < board.getWidth(); col++) {
                Position p = new Position(row, col);
                List<P> piece = board.piecesAt(p);
                if(piece == null || piece.isEmpty()) {
                    sb.append(getEmptyGridSpace(row, col)).append(" ");
                } else {
                    sb.append(getPieceSpace(piece, row, col)).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    protected String getHeadingForColumn(int colNum) {
        if(colNum > 25) {
            throw new IllegalArgumentException("colNum > 25, please implement your own grid creation feature.");
        }
        return ":regional_indicator_" + (char)(colNum + 'a') + ":";
    }

    protected String getHeadingForRow(int rowNum, int size) {
        return String.format("%0" + size + "d", (rowNum + 1)).chars()
                .map(unicode -> unicode - '0')
                .mapToObj(this::getEmojiFor)
                .collect(Collectors.joining(" "));
    }

    protected abstract String getEmptyGridSpace(int row, int col);

    protected abstract String getPieceSpace(List<P> pieces, int row, int col);

    final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };

    // Requires positive x
    static int stringSize(int x) {
        for (int i=0; ; i++)
            if (x <= sizeTable[i])
                return i+1;
    }

    private String getEmojiFor(int number) {
        switch(number % 10) {
            case 0: return ":zero:";
            case 1: return ":one:";
            case 2: return ":two:";
            case 3: return ":three:";
            case 4: return ":four:";
            case 5: return ":five:";
            case 6: return ":six:";
            case 7: return ":seven:";
            case 8: return ":eight:";
            case 9: return ":nine:";
        }
        throw new IllegalStateException("Should never happen");
    }
}
