package dev.tomcorley.mandible.game_logic;

public record PlacePiece(HexCoordinate position, HivePiece piece) implements HiveMove {
    @Override
    public String toString() {
        return "Placing " + piece + " at " + position;
    }

    public RemovePiece invertMove() {
        return new RemovePiece(position);
    }
}