package dev.tomcorley.mandible.game_logic;

public class HiveGame {
    private final HiveBoard board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;
    private HiveGameState state;

    public HiveGame(Player whitePlayer, Player blackPlayer) {
        this.board = new HiveBoard();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentPlayer = whitePlayer;
        this.state = HiveGameState.IN_PROGRESS;
    }

    public void checkWinCondition() {
        boolean whiteWon = checkForWhiteWin();

        boolean blackWon = checkForBlackWin();

        if (whiteWon && blackWon) {
            state = HiveGameState.DRAW;
        } else if (whiteWon) {
            state = HiveGameState.WHITE_WON;
        } else if (blackWon) {
            state = HiveGameState.BLACK_WON;
        } else {
            state = HiveGameState.IN_PROGRESS;
        }
    }

    public boolean checkForWhiteWin() {
        HivePiece blackQueenBee = blackPlayer.getQueenBee();

        // If queen bee is not on the board, return false
        if (!board.getPieceLocations().containsKey(blackQueenBee)) {
            return false;
        }
        
        // Otherwise get its coordinate
        HexCoordinate blackQueenBeeCoordinate = board.getPieceLocations().get(blackQueenBee);

        // Check for 6 occupied neighbours 
        for (HexCoordinate neighbour : blackQueenBeeCoordinate.getNeighbours()) {
            if (!board.getGrid().isCoordinateOccupied(neighbour)) {
                return false;
            }
        }

        return true;
    }   

    public boolean checkForBlackWin() {
        HivePiece whiteQueenBee = whitePlayer.getQueenBee();

        // If queen bee is not on the board, return false
        if (!board.getPieceLocations().containsKey(whiteQueenBee)) {
            return false;
        }

        // Otherwise get its coordinate
        HexCoordinate whiteQueenBeeCoordinate = board.getPieceLocations().get(whiteQueenBee);

        // Check for 6 occupied neighbours 
        for (HexCoordinate neighbour : whiteQueenBeeCoordinate.getNeighbours()) {
            if (!board.getGrid().isCoordinateOccupied(neighbour)) {
                return false;
            }
        }

        return true;
    }

    public void makeMove(HiveMove move) {
        board.makeMove(move);
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

    public HiveBoard getBoard() {
        return board;
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