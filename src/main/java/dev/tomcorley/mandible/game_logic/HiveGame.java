package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
       return isQueenSurrounded(blackPlayer);
    }

    public boolean checkForBlackWin() {
        return isQueenSurrounded(whitePlayer);
    }
    
    public boolean isQueenSurrounded(Player player) {
        HivePiece queenBee = player.getQueenBee();

        // If queen bee is not on the board, return false
        if (!board.getPieceLocations().containsKey(queenBee)) {
            return false;
        }
        
        // Otherwise get its coordinate
        HexCoordinate queenBeeCoordinate = board.getPieceLocations().get(queenBee);

        // Check for 6 occupied neighbours 
        for (HexCoordinate neighbour : queenBeeCoordinate.getNeighbours()) {
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
        
        if (move == null) {
            System.out.println("No Possible Moves, Skipping Player's Turn");
            return;
        } else {
            makeMove(move);
        }

        this.currentPlayer = 
          this.currentPlayer == whitePlayer 
            ? blackPlayer 
            : whitePlayer;
    }

    public List<PlacePiece> getValidPlacementMoves(Player player) {
        // Get valid placement coordinates
        List<HexCoordinate> validCoordinates = board.getValidPlacementCoordinates(player);

        // Get pieces in player's hand that are unplaced
        List<HivePiece> unplacedPieces = player.getHand().stream()
            .filter(piece -> !board.isPiecePlaced(piece))
            .collect(Collectors.toList());

        // Combine the pieces and coordinates to create placement moves
        List<PlacePiece> placementMoves = new ArrayList<>();
        for (HivePiece piece : unplacedPieces) {
            for (HexCoordinate coordinate : validCoordinates) {
                placementMoves.add(new PlacePiece(coordinate, piece));
            }
        }

        return placementMoves;
    }

    public List<MovePiece> getValidMoveMoves(Player player) {
        List<MovePiece> moves = new ArrayList<>();
        List<HivePiece> playerPieces = player.getHand();

        for (HivePiece piece : playerPieces) {
            List<MovePiece> pieceMoves = board.getValidMovesForPiece(piece);
            moves.addAll(pieceMoves);
        }

        return moves;
    }

    

    public List<HiveMove> getValidMovesForPlayer(Player player) {
        List<HiveMove> moves = new ArrayList<>();

        // Get all valid placement moves
        List<PlacePiece> placementMoves = getValidPlacementMoves(player);

        // Get all valid move moves
        List<MovePiece> moveMoves = getValidMoveMoves(player);

        // Combine the moves
        moves.addAll(placementMoves);
        moves.addAll(moveMoves);

        return moves;
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