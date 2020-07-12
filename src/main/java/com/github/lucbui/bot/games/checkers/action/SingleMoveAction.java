package com.github.lucbui.bot.games.checkers.action;

import com.github.lucbui.bot.games.Id;
import com.github.lucbui.bot.games.MoveLegality;
import com.github.lucbui.bot.games.Position;
import com.github.lucbui.bot.games.action.LegalAction;
import com.github.lucbui.bot.games.checkers.*;

public class SingleMoveAction implements LegalAction<Checkers, CheckersGameInstance> {
    public static final String WRONG_COLOR = "WRONG_COLOR";
    public static final String WRONG_DIRECTION = "WRONG_DIRECTION";
    public static final String PIECE_IN_WAY = "PIECE_IN_WAY";
    public static final String PIECE_OFF_BOARD = "PIECE_OFF_BOARD";

    private final Id pieceId;
    private final Orientation direction;

    public SingleMoveAction(Id pieceId, Orientation direction) {
        this.pieceId = pieceId;
        this.direction = direction;
    }

    @Override
    public MoveLegality isLegal(Id playerId, CheckersGameInstance instance) {
        CheckersPlayer player = instance.player(playerId);
        CheckersPiece piece = instance.getBoard().piece(this.pieceId);
        if(player.getColor() != piece.getColor()) {
            return MoveLegality.illegal(WRONG_COLOR, player.getColor(), piece.getColor());
        }

        if(piece.getType() == CheckersPieceType.MEN && !isForwardMovement(piece, this.direction)) {
            return MoveLegality.illegal(WRONG_DIRECTION);
        }

        Position start = instance.getBoard().locationOf(this.pieceId);
        Position end = this.direction.from(start, 1);
        if(!instance.getBoard().isOnBoard(end)) {
            return MoveLegality.illegal(PIECE_OFF_BOARD);
        }
        if(!instance.getBoard().piecesAt(end).isEmpty()) {
            return MoveLegality.illegal(PIECE_IN_WAY);
        }

        return MoveLegality.legal();
    }

    private boolean isForwardMovement(CheckersPiece piece, Orientation orientation) {
        if(piece.getColor() == CheckersColor.RED) {
            return orientation == Orientation.SOUTHEAST || orientation == Orientation.SOUTHWEST;
        } else {
            return orientation == Orientation.NORTHEAST || orientation == Orientation.NORTHWEST;
        }
    }

    @Override
    public MoveLegality performIfLegal(Id player, CheckersGameInstance instance) {
        Position start = instance.getBoard().locationOf(this.pieceId);
        Position end = this.direction.from(start, 1);
        instance.getBoard().move(this.pieceId, end);

        CheckersPiece piece = instance.getBoard().piece(this.pieceId);
        if  (piece.getType() == CheckersPieceType.MEN && (
            (piece.getColor() == CheckersColor.RED && end.getRow() == instance.getBoard().getHeight() - 1) ||
            (piece.getColor() == CheckersColor.BLACK && end.getRow() == 0))) {
            piece.king();
        }

        instance.nextTurn();
        return MoveLegality.legal();
    }
}
