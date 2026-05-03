package dev.tomcorley.mandible.game_logic;

public class PlacePiece extends HiveMove {
    private final HexCoordinate position;
    private final HivePiece piece;

    public PlacePiece(HexCoordinate position, HivePiece piece) {
        this.position = position;
        this.piece = piece;
    }

    public HexCoordinate getPosition() {
        return position;
    }

    public HivePiece getPiece() {
        return piece;
    }
}