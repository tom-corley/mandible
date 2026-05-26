package dev.tomcorley.mandible.engine;

public class HivePiece {
    private final PlayerColour colour;
    private final HivePieceType type;
    private final String id;

    public HivePiece(PlayerColour colour, HivePieceType type, int index) {
        this.colour = colour;
        this.type = type;
        this.id = colour + "_" + type + "_" + index;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public HivePieceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return colour.toString() + " " + type.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof HivePiece h)) return false;
        return this.id.equals(h.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}