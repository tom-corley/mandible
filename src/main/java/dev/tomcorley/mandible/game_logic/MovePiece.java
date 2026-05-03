package dev.tomcorley.mandible.game_logic;

public record MovePiece(HexCoordinate from, HexCoordinate to) implements HiveMove {
}