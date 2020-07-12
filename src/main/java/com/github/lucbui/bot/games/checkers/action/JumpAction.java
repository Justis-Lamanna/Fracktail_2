package com.github.lucbui.bot.games.checkers.action;

import com.github.lucbui.bot.games.Id;
import com.github.lucbui.bot.games.MoveLegality;
import com.github.lucbui.bot.games.Position;
import com.github.lucbui.bot.games.action.LegalAction;
import com.github.lucbui.bot.games.checkers.*;

import java.util.Collections;
import java.util.List;

public class JumpAction implements LegalAction<Checkers, CheckersGameInstance> {
    public static final String WRONG_COLOR = "WRONG_COLOR";
    public static final String WRONG_DIRECTION = "WRONG_DIRECTION";
    public static final String PIECE_IN_WAY = "PIECE_IN_WAY";
    public static final String NO_PIECE_TO_JUMP = "NO_PIECE_TO_JUMP";
    public static final String CANT_JUMP_OWN_PIECE = "CANT_JUMP_OWN_PIECE";
    public static final String PIECE_OFF_BOARD = "PIECE_OFF_BOARD";

    private final Id pieceId;
    private final List<Orientation> jumps;

    public JumpAction(Id pieceId, List<Orientation> jumps) {
        this.pieceId = pieceId;
        this.jumps = Collections.unmodifiableList(jumps);
    }

    public JumpAction(Id pieceId, Orientation jump) {
        this.pieceId = pieceId;
        this.jumps = Collections.singletonList(jump);
    }

    @Override
    public MoveLegality isLegal(Id playerId, CheckersGameInstance instance) {
        CheckersPlayer player = instance.player(playerId);
        CheckersPiece piece = instance.getBoard().piece(this.pieceId);
        if(player.getColor() != piece.getColor()) {
            return MoveLegality.illegal(WRONG_COLOR, player.getColor(), piece.getColor());
        }

        if(piece.getType() == CheckersPieceType.MEN &&
                jumps.stream().anyMatch(o -> !isForwardMovement(piece, o))) {
            return MoveLegality.illegal(WRONG_DIRECTION);
        }

        Position start = instance.getBoard().locationOf(this.pieceId);
        for(Orientation o : jumps) {
            Position end = o.from(start, 2);
            if(!instance.getBoard().isOnBoard(end)) {
                return MoveLegality.illegal(PIECE_OFF_BOARD);
            }
            if(!instance.getBoard().piecesAt(end).isEmpty()) {
                return MoveLegality.illegal(PIECE_IN_WAY);
            }

            Position jumped = o.from(start, 1);
            List<CheckersPiece> jumpedPieces = instance.getBoard().piecesAt(jumped);
            if(jumpedPieces.isEmpty()) {
                return MoveLegality.illegal(NO_PIECE_TO_JUMP);
            } else if(jumpedPieces.get(0).getColor() == player.getColor()) {
                return MoveLegality.illegal(CANT_JUMP_OWN_PIECE);
            }
            start = end;
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
    public MoveLegality performIfLegal(Id playerId, CheckersGameInstance instance) {
        Position start = instance.getBoard().locationOf(this.pieceId);
        for(Orientation o : jumps) {
            Position end = o.from(start, 2);
            Position jumped = o.from(start, 1);
            CheckersPiece jumpedPiece = instance.getBoard().piecesAt(jumped).get(0);
            instance.getBoard().remove(jumpedPiece.id());
            instance.getBoard().move(this.pieceId, end);
            start = end;
        }

        instance.nextTurn();
        return MoveLegality.legal();
    }
}
