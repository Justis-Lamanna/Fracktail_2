package com.github.lucbui.bot.games.checkers.action;

import com.github.lucbui.bot.games.Id;
import com.github.lucbui.bot.games.MoveLegality;
import com.github.lucbui.bot.games.Position;
import com.github.lucbui.bot.games.action.LegalAction;
import com.github.lucbui.bot.games.checkers.Checkers;
import com.github.lucbui.bot.games.checkers.CheckersGameInstance;
import com.github.lucbui.bot.games.checkers.CheckersPiece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveAction implements LegalAction<Checkers, CheckersGameInstance> {
    public static final String NO_PIECE_AT_POSITION = "NO_PIECE";
    public static final String ILLEGAL_MOVEMENT = "ILLEGAL_MOVEMENT";

    private final Position start;
    private final List<Position> movements;

    public MoveAction(Position start, List<Position> movements) {
        this.start = start;
        this.movements = movements;
    }

    public MoveAction(Position start, Position end) {
        this.start = start;
        this.movements = Collections.singletonList(end);
    }

    @Override
    public MoveLegality isLegal(Id playerId, CheckersGameInstance instance) {
        List<CheckersPiece> piecesAtStart = instance.getBoard().piecesAt(start);
        if(piecesAtStart.isEmpty()) {
            return MoveLegality.illegal(NO_PIECE_AT_POSITION, start.getRow(), start.getCol());
        }

        if(movements.size() == 1) {
            Position end = movements.get(0);
            Position.Relative relativeDistance = start.distance(end);
            if(!isDiagonal(relativeDistance, 1) && !isDiagonal(relativeDistance, 2)) {
                return MoveLegality.illegal(ILLEGAL_MOVEMENT);
            }
        } else {
            Position one = start;
            for(Position jumpTo : movements) {
                Position.Relative relativeDistance = one.distance(jumpTo);
                if(!isDiagonal(relativeDistance, 2)) {
                    return MoveLegality.illegal(ILLEGAL_MOVEMENT);
                }
                one = jumpTo;
            }
        }

        return MoveLegality.legal();
    }

    @Override
    public MoveLegality performIfLegal(Id playerId, CheckersGameInstance instance) {
        CheckersPiece pieceAtStart = instance.getBoard().piecesAt(start).get(0);

        if(movements.size() == 1) {
            Position end = movements.get(0);
            Position.Relative relativeDistance = start.distance(end);
            if(isDiagonal(relativeDistance, 1)) {
                //Single Move
                return new SingleMoveAction(pieceAtStart.id(), Orientation.fromRelative(relativeDistance))
                        .perform(playerId, instance);
            } else  {
                //Single Jump
                return new JumpAction(pieceAtStart.id(), Orientation.fromRelative(relativeDistance))
                        .perform(playerId, instance);
            }
        } else {
            List<Orientation> jumps = new ArrayList<>();
            Position one = start;
            for(Position jumpTo : movements) {
                jumps.add(Orientation.fromRelative(one.distance(jumpTo)));
                one = jumpTo;
            }

            return new JumpAction(pieceAtStart.id(), jumps)
                    .perform(playerId, instance);
        }
    }

    private boolean isDiagonal(Position.Relative relative, int size) {
        return Math.abs(relative.getDeltaCol()) == size && Math.abs(relative.getDeltaRow()) == size;
    }
}
