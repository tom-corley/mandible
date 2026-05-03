package dev.tomcorley.mandible.game_logic;

public record PlacePiece(HexCoordinate position, HivePiece piece) implements HiveMove {
}