package dev.tomcorley.mandible.game_logic;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class HiveBoard {
    private final HiveGrid grid;
    private final Map<HivePiece, HexCoordinate> pieceLocations;

    public HiveBoard() {
        this.grid = new HiveGrid();
        this.pieceLocations = new HashMap<>();
    }

    // When a move is made, update locations and grid
    public void makeMove(HiveMove move) {
        if (move instanceof PlacePiece placeMove) {
            placePiece(placeMove);
        } else if (move instanceof MovePiece moveMove) {
            movePiece(moveMove);
        } else if (move instanceof RemovePiece removeMove) {
            removePiece(removeMove);
        } else {
            throw new InvalidMoveException("Invalid move type: " + move.getClass().getSimpleName());
        }
    }

    private void placePiece(PlacePiece placeMove) {
        grid.placePiece(placeMove);
        pieceLocations.put(placeMove.piece(), placeMove.position());
    }

    private void removePiece(RemovePiece removeMove) {
        HivePiece piece = grid.getPiece(removeMove.position());
        grid.removePiece(removeMove);
        pieceLocations.remove(piece);
    }

    private void movePiece(MovePiece moveMove) {
        // Get piece from grid
        HexCoordinate from = moveMove.from();
        HivePiece piece = grid.getPiece(from);

        // Move piece on grid
        grid.movePiece(moveMove);

        // Update piece location on board
        pieceLocations.put(piece, moveMove.to());

        // Lock the destination coordinate
        grid.lockCoordinate(moveMove.to());
    }

    public boolean isCoordinateOccupied(HexCoordinate coordinate) {
        return grid.isCoordinateOccupied(coordinate);
    }

    public int getStackHeight(HexCoordinate coordinate) {
        return grid.getStackHeight(coordinate);
    }

    public List<MovePiece> getValidMovesForPiece(HivePiece piece) {
        List<MovePiece> moves = new ArrayList<>();

        if (!isPiecePlaced(piece)) {
            return moves;
        }

        if (!isPieceOnTopOfStack(piece)) {
            return moves;
        }

        HexCoordinate coordinate = pieceLocations.get(piece);
        
        moves.addAll(grid.getValidMovesForPiece(coordinate));

        return moves;
    }

    public void lockCoordinate(HexCoordinate coordinate) {
        grid.lockCoordinate(coordinate);
    }

    public boolean isPiecePlaced(HivePiece piece) {
        return pieceLocations.containsKey(piece);
    }

    public boolean isPieceOnTopOfStack(HivePiece piece) {
        // If piece is not placed, return false
        if (!isPiecePlaced(piece)) {
            return false;
        }

        // Get the coordinate of the piece
        HexCoordinate coordinate = pieceLocations.get(piece);

        // We need to check if the piece on the top of the stack at that coordinate is our piece
        HivePiece topPiece = grid.getPiece(coordinate);
        return topPiece.equals(piece);
    }

    public List<HexCoordinate> getValidPlacementCoordinates(Player player) {
        PlayerColour colour = player.getColour();
        return grid.getValidPlacementPositions(colour);
    }

    public HiveGrid getGrid() {
        return grid;
    }

    public Map<HivePiece, HexCoordinate> getPieceLocations() {
        return Collections.unmodifiableMap(pieceLocations);
    }

    public void clearLockedCoordinate() {
        grid.clearLockedCoordinate();
    }

    public String toStateKey() {
        return grid.toStateKey();
    }
}
