package dev.tomcorley.mandible.game_logic;

public class HivePiece {
    private final PlayerColour colour;
    private final HivePieceType type;

    public HivePiece(PlayerColour colour, HivePieceType type) {
        this.colour = colour;
        this.type = type;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public HivePieceType getType() {
        return type;
    }
}