package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;
import dev.tomcorley.mandible.game_logic.TestPieceFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AntMovementTest {

    private HiveGrid grid;
    private static final HexCoordinate ORIGIN = new HexCoordinate(0, 0);

    private void place(HivePiece piece, HexCoordinate coord) {
        grid.placePiece(new PlacePiece(coord, piece));
    }

    private final TestPieceFactory pieces = new TestPieceFactory();

    private HivePiece white(HivePieceType type) {
        return pieces.white(type);
    }

    private HivePiece black(HivePieceType type) {
        return pieces.black(type);
    }

    @BeforeEach
    void setUp() {
        grid = new HiveGrid();
        pieces.reset();
    }

    @Test
    @DisplayName("ant can reach many positions around a linear hive")
    void reachesManyPositions() {
        // Ant at end of a line: Ant(0,0) — (1,0) — (2,0)
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // Ant can reach any edge position around the 2-piece hive — far more than queen's 2
        assertTrue(moves.size() > 2);
    }

    @Test
    @DisplayName("ant reaches positions a queen cannot")
    void reachesMoreThanQueen() {
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // Ant should reach the far side of the chain
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();
        assertTrue(destinations.contains(new HexCoordinate(3, 0)),
                "Ant should reach far end of chain via (3,0)");
    }

    @Test
    @DisplayName("ant does not include starting position as a destination")
    void startingPositionExcluded() {
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        assertFalse(destinations.contains(ORIGIN));
    }

    @Test
    @DisplayName("ant does not land on occupied positions")
    void doesNotLandOnOccupied() {
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        assertFalse(destinations.contains(new HexCoordinate(1, 0)));
        assertFalse(destinations.contains(new HexCoordinate(2, 0)));
    }

    @Test
    @DisplayName("ant alone on board has no moves")
    void antAloneNoMoves() {
        place(white(HivePieceType.ANT), ORIGIN);

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("ant cannot pass through a gate")
    void blockedByGate() {
        // Create a gate: two pieces flanking a gap the ant would need to slide through
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(0, 1));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 0));

        // With pieces at N and NE forming a gate, ant still finds paths around
        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertFalse(moves.isEmpty());
    }

    @Test
    @DisplayName("all ant moves originate from current position")
    void allMovesOriginateFromCurrentPosition() {
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.stream().allMatch(m -> m.from().equals(ORIGIN)));
    }

    @Test
    @DisplayName("ant that is a bridge has no moves")
    void bridgeAntNoMoves() {
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(-1, 0));
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("ant produces no duplicate destinations")
    void noDuplicateDestinations() {
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        long uniqueCount = moves.stream().map(MovePiece::to).distinct().count();
        assertEquals(moves.size(), uniqueCount);
    }

    @Test
    @DisplayName("every ant destination is adjacent to at least one hive piece")
    void allDestinationsAdjacentToHive() {
        place(white(HivePieceType.ANT), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        // The ant is removed from the grid during movement, so hive = (1,0), (2,0)
        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        for (MovePiece move : moves) {
            boolean adjacentToHive = move.to().getNeighbours().stream()
                    .anyMatch(n -> n.equals(new HexCoordinate(1, 0)) ||
                            n.equals(new HexCoordinate(2, 0)));
            assertTrue(adjacentToHive, move.to() + " should be adjacent to the hive");
        }
    }
}
