package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrasshopperMovementTest {

    private HiveGrid grid;
    private static final HexCoordinate ORIGIN = new HexCoordinate(0, 0);

    private void place(HivePiece piece, HexCoordinate coord) {
        grid.placePiece(new PlacePiece(coord, piece));
    }

    private HivePiece white(HivePieceType type) {
        return new HivePiece(PlayerColour.WHITE, type);
    }

    private HivePiece black(HivePieceType type) {
        return new HivePiece(PlayerColour.BLACK, type);
    }

    @BeforeEach
    void setUp() {
        grid = new HiveGrid();
    }

    @Test
    @DisplayName("grasshopper jumps over one piece to land on other side")
    void jumpsOverOnePiece() {
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));  // NE neighbour

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        assertTrue(moves.stream().anyMatch(m -> m.to().equals(new HexCoordinate(2, 0))));
    }

    @Test
    @DisplayName("grasshopper jumps over multiple pieces in a line")
    void jumpsOverMultiplePieces() {
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.ANT), new HexCoordinate(3, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // Should land at (4,0) — first empty space after the line
        assertTrue(moves.stream().anyMatch(m -> m.to().equals(new HexCoordinate(4, 0))));
        // Should NOT land in the middle of the line
        assertFalse(moves.stream().anyMatch(m -> m.to().equals(new HexCoordinate(2, 0))));
        assertFalse(moves.stream().anyMatch(m -> m.to().equals(new HexCoordinate(3, 0))));
    }

    @Test
    @DisplayName("grasshopper cannot move to empty adjacent space (must jump)")
    void cannotMoveToEmptyAdjacent() {
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Origin's neighbours that are empty should not appear as destinations
        assertFalse(destinations.contains(new HexCoordinate(0, 1)));
        assertFalse(destinations.contains(new HexCoordinate(0, -1)));
        assertFalse(destinations.contains(new HexCoordinate(-1, 0)));
        assertFalse(destinations.contains(new HexCoordinate(-1, 1)));
    }

    @Test
    @DisplayName("grasshopper can jump in multiple directions")
    void jumpsInMultipleDirections() {
        // Neighbours at NE, N, NW — they form a connected arc without the grasshopper
        // (1,0)↔(0,1)↔(-1,1) are all pairwise adjacent
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));   // NE
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));   // N
        place(black(HivePieceType.ANT), new HexCoordinate(-1, 1));  // NW

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        assertEquals(3, moves.size());
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();
        assertTrue(destinations.contains(new HexCoordinate(2, 0)));    // jumped NE
        assertTrue(destinations.contains(new HexCoordinate(0, 2)));    // jumped N
        assertTrue(destinations.contains(new HexCoordinate(-2, 2)));   // jumped NW
    }

    @Test
    @DisplayName("grasshopper with no adjacent pieces has no moves")
    void noAdjacentPiecesNoMoves() {
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("all grasshopper moves originate from current position")
    void allMovesOriginateFromCurrentPosition() {
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.stream().allMatch(m -> m.from().equals(ORIGIN)));
    }

    @Test
    @DisplayName("grasshopper does not land on occupied space")
    void doesNotLandOnOccupied() {
        place(white(HivePieceType.GRASSHOPPER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        assertFalse(destinations.contains(new HexCoordinate(1, 0)));
    }
}
