package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueenBeeMovementTest {

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
    @DisplayName("queen with one neighbour can slide to two positions")
    void queenWithOneNeighbour() {
        place(white(HivePieceType.QUEEN_BEE), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        assertEquals(2, moves.size());
        // Should be able to slide to the two shared-edge positions
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();
        assertTrue(destinations.contains(new HexCoordinate(0, 1)));
        assertTrue(destinations.contains(new HexCoordinate(1, -1)));
    }

    @Test
    @DisplayName("queen between two pieces forming a gate cannot slide through")
    void queenBlockedByGate() {
        // Queen at origin, pieces on both sides of a direction form a gate
        place(white(HivePieceType.QUEEN_BEE), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(0, 1));   // N
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));   // NE

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // NE direction (1,0) is occupied, N direction (0,1) is occupied
        // The space between them at direction NE is blocked by gate N+NE
        // Queen should not be able to slide to a position where both flanking cells are occupied
        assertFalse(destinations.contains(new HexCoordinate(0, 1)));
        assertFalse(destinations.contains(new HexCoordinate(1, 0)));
    }

    @Test
    @DisplayName("queen cannot slide to position not adjacent to any other piece")
    void queenMustRemainTouchingHive() {
        place(white(HivePieceType.QUEEN_BEE), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Queen slides along the edge — every destination must be adjacent to the ant at (1,0)
        for (HexCoordinate dest : destinations) {
            assertTrue(dest.getNeighbours().contains(new HexCoordinate(1, 0)),
                    dest + " must be adjacent to hive");
        }
    }

    @Test
    @DisplayName("queen alone on board has no moves")
    void queenAloneNoMoves() {
        place(white(HivePieceType.QUEEN_BEE), ORIGIN);

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("queen surrounded on all sides has no moves")
    void queenSurroundedNoMoves() {
        place(white(HivePieceType.QUEEN_BEE), ORIGIN);
        for (HexDirection dir : HexDirection.values()) {
            place(black(HivePieceType.ANT), ORIGIN.add(dir));
        }

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("all queen moves originate from current position")
    void allMovesOriginateFromCurrentPosition() {
        place(white(HivePieceType.QUEEN_BEE), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.stream().allMatch(m -> m.from().equals(ORIGIN)));
    }
}
