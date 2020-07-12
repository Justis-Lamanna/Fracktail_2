package com.github.lucbui.bot.games;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Board<T extends Game, P extends Piece<T>> {
    private final int width;
    private final int height;

    private final Map<Id, GamePiece<T, P>> idToPieceMap;

    public Board(int width, int height, Map<P, Position> board) {
        this.width = width;
        this.height = height;
        this.idToPieceMap = calculateIdToPieceMap(board);
    }

    private Map<Id, GamePiece<T, P>> calculateIdToPieceMap(Map<P, Position> board) {
        return board
                .keySet()
                .stream()
                .collect(Collectors.toMap(HasId::id, k -> new GamePiece<>(k, board.get(k))));
    }

    private Board(int width, int height, Map<Id, GamePiece<T, P>> idToPieceMap, Void marker) {
        this.width = width;
        this.height = height;
        this.idToPieceMap = idToPieceMap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isOnBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < height &&
                position.getCol() >= 0 && position.getCol() < width;
    }

    public List<P> pieces() {
        return idToPieceMap.values()
                .stream()
                .map(gp -> gp.piece)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public List<P> piecesAt(Position position) {
        return idToPieceMap.values()
                .stream()
                .filter(p -> p.position.equals(position))
                .map(p -> p.piece)
                .collect(Collectors.toList());
    }

    public void move(Id piece, Position toPlace) {
        GamePiece<T, P> p = idToPieceMap.get(piece);
        if(p == null) {
            throw new IllegalArgumentException("No piece at position " + toPlace);
        }
        p.position = toPlace;
    }

    public void remove(Id piece) {
        if(!idToPieceMap.containsKey(piece)) {
            throw new IllegalArgumentException("No piece with ID " + piece.getIdentifier());
        }
        idToPieceMap.remove(piece);
    }

    public void add(P piece, Position toPlace) {
        if(idToPieceMap.containsKey(piece.id())) {
            throw new IllegalArgumentException("Piece already exists with ID " + piece.id().getIdentifier());
        }
        idToPieceMap.put(piece.id(), new GamePiece<>(piece, toPlace));
    }

    public Position locationOf(Id piece) {
        GamePiece<T, P> p = idToPieceMap.get(piece);
        if(p == null) {
            throw new IllegalArgumentException("No piece with ID " + piece.getIdentifier());
        }
        return p.position;
    }

    public P piece(Id piece) {
        GamePiece<T, P> p = idToPieceMap.get(piece);
        if(p == null) {
            throw new IllegalArgumentException("No piece with ID " + piece.getIdentifier());
        }
        return p.piece;
    }

    public Board<T, P> copy() {
        Map<Id, GamePiece<T, P>> boardCopy = idToPieceMap.keySet()
                .stream()
                .collect(Collectors.toMap(Function.identity(), k -> idToPieceMap.get(k).copy()));
        return new Board<>(width, height, boardCopy, null);
    }

    private static class GamePiece<T extends Game, P extends Piece<T>> implements HasId {
        P piece;
        Position position;

        public GamePiece(P piece, Position position) {
            this.piece = piece;
            this.position = position;
        }

        @Override
        public Id id() {
            return piece.id();
        }

        public GamePiece<T, P> copy() {
            return new GamePiece<>(piece, position);
        }
    }
}
