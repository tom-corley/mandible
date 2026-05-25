package dev.tomcorley.mandible.game_logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveGame {
    private static final Logger log = LoggerFactory.getLogger(HiveGame.class);
    private final HiveBoard board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;
    private HiveGameState state;
    private int turnCount;
    private final List<HiveMove> moveHistory;
    private final Map<String, Integer> boardStateFrequencySet;

    public HiveGame(Player whitePlayer, Player blackPlayer) {
        this.board = new HiveBoard();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentPlayer = whitePlayer;
        this.state = HiveGameState.IN_PROGRESS;
        this.turnCount = 1;
        this.moveHistory = new ArrayList<>();
        this.boardStateFrequencySet = new HashMap<>();
    }

    public void addBoardStateToFrequencySet(String stateKey) {
        boardStateFrequencySet.put(stateKey, boardStateFrequencySet.getOrDefault(stateKey, 0) + 1);
    }

    public void removeBoardStateFromFrequencySet(String stateKey) {
        boardStateFrequencySet.put(stateKey, boardStateFrequencySet.getOrDefault(stateKey, 0) - 1);
        if (boardStateFrequencySet.get(stateKey) == 0) {
            boardStateFrequencySet.remove(stateKey);
        }
    }

    public void checkWinCondition() {
        if (state != HiveGameState.IN_PROGRESS) {
            return;
        }

        boolean whiteWon = checkForWhiteWin();

        boolean blackWon = checkForBlackWin();

        boolean draw = checkForDraw();

        if (whiteWon && blackWon || draw) {
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

    public boolean checkForDraw() {
        // Check current state key does not have a frequency of 2
        String stateKey = board.toStateKey();
        if (boardStateFrequencySet.containsKey(stateKey) && boardStateFrequencySet.get(stateKey) >= 2) {
            return true;
        }

        return moveHistory.size() >= 5000;
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

        executeMove(move);

        if (currentPlayer == blackPlayer) {
            this.turnCount += 1;
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

        // If it is the fourth turn and they have not placed the queen they must do so
        HivePiece queen = player.getQueenBee();
        if (!board.isPiecePlaced(queen) && this.turnCount >= 4) {
            unplacedPieces = List.of(queen);
        }


        // Combine the pieces and coordinates to create placement moves
        List<PlacePiece> placementMoves = new ArrayList<>();
        for (HivePiece piece : unplacedPieces) {
            for (HexCoordinate coordinate : validCoordinates) {
                placementMoves.add(new PlacePiece(coordinate, piece));
            }
        }

        return placementMoves;
    }

    public void executeMove(HiveMove move) {
        // Clear the locked coordinate set by the previous move
        board.clearLockedCoordinate();

        if (move == null) {
            log.debug("No possible moves, skipping turn for {}", currentPlayer.getColour());
        } else {
            makeMove(move);
        }

        moveHistory.add(move);
        String stateKey = board.toStateKey();
        addBoardStateToFrequencySet(stateKey);
    }

    public void undoMove() {
        if (moveHistory.isEmpty()) {
            return;
        }

        // Decrement the frequency of the current state key
        String stateKey = board.toStateKey();
        removeBoardStateFromFrequencySet(stateKey);

        // Undo the last move
        HiveMove lastMove = moveHistory.remove(moveHistory.size() - 1);
        if (lastMove != null) {
            HiveMove invertedMove = lastMove.invertMove();
            board.makeMove(invertedMove);
        }

        // Restore the pillbug lock present before the last move
        board.clearLockedCoordinate();
        if (!moveHistory.isEmpty()) {
            HiveMove penultimateMove = moveHistory.get(moveHistory.size() - 1);
            if (penultimateMove instanceof MovePiece movePiece) {
                board.lockCoordinate(movePiece.to());
            }
        }

        // Switch to the other player
        this.currentPlayer =
          this.currentPlayer == whitePlayer
            ? blackPlayer
            : whitePlayer;


        // Decrement the turn count if we have gone back to a black turn
        if (this.currentPlayer == blackPlayer) {
            this.turnCount -= 1;
        }
    }

    public List<MovePiece> getValidMoveMoves(Player player) {
        List<MovePiece> moves = new ArrayList<>();
        List<HivePiece> playerPieces = player.getHand();

        // If queen has been placed yet cannot move any pieces
        HivePiece queen = player.getQueenBee();
        if (!board.isPiecePlaced(queen)) {
            return moves;
        }

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

    public List<HiveMove> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }
}