package com.github.lucbui.bot.games.checkers;

import com.github.lucbui.bot.games.Board;
import com.github.lucbui.bot.games.BoardGameInstance;
import com.github.lucbui.bot.games.Copyable;
import com.github.lucbui.bot.games.Position;

import java.util.*;

import static com.github.lucbui.bot.games.checkers.CheckersColor.BLACK;
import static com.github.lucbui.bot.games.checkers.CheckersColor.RED;
import static com.github.lucbui.bot.games.checkers.CheckersPieceType.MEN;

public class CheckersGameInstance
        extends BoardGameInstance<Checkers, CheckersPiece, CheckersPlayer, CheckersGameInstance>
        implements Copyable<CheckersGameInstance> {

    public CheckersGameInstance(CheckersPlayer redPlayer, CheckersPlayer blackPlayer) {
        super(Arrays.asList(redPlayer, blackPlayer), 0, createStartingBoard());
    }

    private CheckersGameInstance(List<CheckersPlayer> players, Board<Checkers, CheckersPiece> board, int turnCounter, Deque<TurnAction> actions) {
        super(players, turnCounter, board, actions);
    }

    public CheckersPlayer getOpponent() {
        return turnCounter == 0 ? players.get(1) : players.get(0);
    }

    public CheckersPlayer getRed() {
        return players.get(0);
    }

    public CheckersPlayer getBlack() {
        return players.get(1);
    }

    @Override
    public CheckersGameInstance copy() {
        return new CheckersGameInstance(players, board, turnCounter, actions);
    }

    private static Board<Checkers, CheckersPiece> createStartingBoard() {
        return new Board<>(8, 8, createPieces());
    }

    private static Map<CheckersPiece, Position> createPieces() {
        Map<CheckersPiece, Position> pieces = new HashMap<>();
        pieces.put(MEN.ofColor(RED), new Position(0, 1));
        pieces.put(MEN.ofColor(RED), new Position(0, 3));
        pieces.put(MEN.ofColor(RED), new Position(0, 5));
        pieces.put(MEN.ofColor(RED), new Position(0, 7));

        pieces.put(MEN.ofColor(RED), new Position(1, 0));
        pieces.put(MEN.ofColor(RED), new Position(1, 2));
        pieces.put(MEN.ofColor(RED), new Position(1, 4));
        pieces.put(MEN.ofColor(RED), new Position(1, 6));

        pieces.put(MEN.ofColor(RED), new Position(2, 1));
        pieces.put(MEN.ofColor(RED), new Position(2, 3));
        pieces.put(MEN.ofColor(RED), new Position(2, 5));
        pieces.put(MEN.ofColor(RED), new Position(2, 7));

        pieces.put(MEN.ofColor(BLACK), new Position(5, 0));
        pieces.put(MEN.ofColor(BLACK), new Position(5, 2));
        pieces.put(MEN.ofColor(BLACK), new Position(5, 4));
        pieces.put(MEN.ofColor(BLACK), new Position(5, 6));

        pieces.put(MEN.ofColor(BLACK), new Position(6, 1));
        pieces.put(MEN.ofColor(BLACK), new Position(6, 3));
        pieces.put(MEN.ofColor(BLACK), new Position(6, 5));
        pieces.put(MEN.ofColor(BLACK), new Position(6, 7));

        pieces.put(MEN.ofColor(BLACK), new Position(7, 0));
        pieces.put(MEN.ofColor(BLACK), new Position(7, 2));
        pieces.put(MEN.ofColor(BLACK), new Position(7, 4));
        pieces.put(MEN.ofColor(BLACK), new Position(7, 6));

        return pieces;
    }

    @Override
    protected String getEmptyGridSpace(int row, int col) {
        if((row & 1) == (col & 1)) {
            return ":white_large_square:";
        } else {
            return ":black_large_square:";
        }
    }

    @Override
    protected String getPieceSpace(List<CheckersPiece> pieces, int row, int col) {
        CheckersPiece checkersPiece = pieces.get(0);
        if(checkersPiece.getColor() == CheckersColor.RED) {
            return checkersPiece.getType() == CheckersPieceType.KING ? ":red_square:" : ":red_circle:";
        } else {
            return checkersPiece.getType() == CheckersPieceType.KING ? ":blue_square:" : ":blue_circle:";
        }
    }

    @Override
    public void updatePlayerState() {
        for(CheckersPlayer p : players) {
            boolean piecesInPlay = getBoard().pieces().stream().anyMatch(piece -> piece.getColor() == p.getColor());
            if(!piecesInPlay) {
                forfeit(p.id());
            }
        }
    }
}
