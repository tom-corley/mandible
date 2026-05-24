package dev.tomcorley.mandible.game_logic;

public record MovePiece(HexCoordinate from, HexCoordinate to) implements HiveMove {
    @Override
    public String toString() {
        return "Moving" + " from " + from + " to " + to;
    }

    public MovePiece invertMove() {
        return new MovePiece(to, from);
    }
}