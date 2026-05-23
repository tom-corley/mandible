package dev.tomcorley.mandible.game_logic;

import java.util.Map;
import java.util.HashMap;

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
        } else {
            throw new IllegalArgumentException("Invalid move");
        }
    }

    private void placePiece(PlacePiece placeMove) {
        grid.placePiece(placeMove);
        pieceLocations.put(placeMove.piece(), placeMove.position());
    }

    private void movePiece(MovePiece moveMove) {
        // Get piece from grid
        HexCoordinate from = moveMove.from();
        HivePiece piece = grid.getPiece(from);

        // Move piece on grid
        grid.movePiece(moveMove);

        // Update piece location on board
        pieceLocations.put(piece, moveMove.to());
    }

    public HiveGrid getGrid() {
        return grid;
    }

    public Map<HivePiece, HexCoordinate> getPieceLocations() {
        return pieceLocations;
    }
}
