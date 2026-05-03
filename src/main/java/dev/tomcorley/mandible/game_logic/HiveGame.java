package dev.tomcorley.mandible.game_logic;

public class HiveGame {
    private final HiveGrid grid;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private PlayerColour currentPlayer;

    public HiveGame() {
        this.grid = new HiveGrid();
        this.currentPlayer = PlayerColour.WHITE;
        this.whitePlayer = new Player(PlayerColour.WHITE, "White", null);
        this.blackPlayer = new Player(PlayerColour.BLACK, "Black", null);
    }

    public HiveGrid getGrid() {
        return grid;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public PlayerColour getCurrentPlayer() {
        return currentPlayer;
    }
}