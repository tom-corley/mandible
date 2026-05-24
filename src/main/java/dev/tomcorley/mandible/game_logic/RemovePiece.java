package dev.tomcorley.mandible.game_logic;

public record RemovePiece(HexCoordinate position) implements HiveMove {
    @Override
    public String toString() {
        return "Removing top piece from stack at " + position;
    }
}
