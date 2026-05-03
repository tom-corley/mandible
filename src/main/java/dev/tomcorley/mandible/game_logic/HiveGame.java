package dev.tomcorley.mandible.game_logic;

public class HiveGame {
    private final HiveGrid grid;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;
    private HiveGameState state;

    public HiveGame(Player whitePlayer, Player blackPlayer) {
        this.grid = new HiveGrid();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentPlayer = whitePlayer;
        this.state = HiveGameState.IN_PROGRESS;
    }

    public void checkWinCondition() {
        // TODO: check for white queen having 6 neighbours for black win, or vice versa
        // Set game state accordingly
    }

    public void makeMove(HiveMove move) {
        // TODO: implement this
    }

    public void advanceTurn() {
        HiveMove move = currentPlayer.getController().chooseMove(this);
        makeMove(move);

        this.currentPlayer = 
          this.currentPlayer == whitePlayer 
            ? blackPlayer 
            : whitePlayer;
    }

    public HiveGameState getState() {
        return state;
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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}