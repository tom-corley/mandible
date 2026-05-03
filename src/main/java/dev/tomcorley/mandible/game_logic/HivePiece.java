public class HivePiece {
    private final PlayerColour colour;
    private final HexCoordinate position;
    private final HivePieceType type;

    public HivePiece(PlayerColour colour, HexCoordinate position, HivePieceType type) {
        this.colour = colour;
        this.position = position;
        this.type = type;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public HexCoordinate getPosition() {
        return position;
    }

    public HivePieceType getType() {
        return type;
    }
}