package dev.tomcorley.mandible.game_logic;

public class MovePiece extends HiveMove {
    private final HexCoordinate from;
    private final HexCoordinate to;

    public MovePiece(HexCoordinate from, HexCoordinate to) {
        this.from = from;
        this.to = to;
    }

    public HexCoordinate getFrom() {
        return from;
    }

    public HexCoordinate getTo() {
        return to;
    }
}