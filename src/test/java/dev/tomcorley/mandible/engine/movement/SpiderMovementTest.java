package dev.tomcorley.mandible.engine.movement;

import dev.tomcorley.mandible.engine.*;
import dev.tomcorley.mandible.engine.TestPieceFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpiderMovementTest {

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
    @DisplayName("spider moves exactly 3 steps along a linear hive")
    void movesExactlyThreeSteps() {
        // Linear chain: Spider(0,0) — (1,0) — (2,0) — (3,0)
        // Spider slides around the outside, always 3 edge-steps
        place(white(HivePieceType.SPIDER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.ANT), new HexCoordinate(3, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // Two paths of exactly 3 steps: one "above" and one "below" the chain
        assertEquals(2, moves.size());
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();
        assertTrue(destinations.contains(new HexCoordinate(2, 1)));
        assertTrue(destinations.contains(new HexCoordinate(3, -1)));
    }

    @Test
    @DisplayName("spider cannot stop at fewer than 3 steps")
    void cannotStopEarly() {
        place(white(HivePieceType.SPIDER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.ANT), new HexCoordinate(3, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // 1-step destinations (queen-like) should not appear
        assertFalse(destinations.contains(new HexCoordinate(0, 1)));
        assertFalse(destinations.contains(new HexCoordinate(1, -1)));
        // 2-step destinations should not appear
        assertFalse(destinations.contains(new HexCoordinate(1, 1)));
        assertFalse(destinations.contains(new HexCoordinate(2, -1)));
    }

    @Test
    @DisplayName("spider cannot return to starting position")
    void cannotReturnToStart() {
        place(white(HivePieceType.SPIDER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.ANT), new HexCoordinate(3, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        assertFalse(destinations.contains(ORIGIN));
    }

    @Test
    @DisplayName("spider alone on board has no moves")
    void spiderAloneNoMoves() {
        place(white(HivePieceType.SPIDER), ORIGIN);

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("spider deduplicates moves when multiple paths reach same destination")
    void deduplicatesMoves() {
        // Ring of pieces around the spider — multiple 3-step paths can reach the same cell
        // Use a cluster: spider + 5 pieces forming a partial ring
        place(white(HivePieceType.SPIDER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(1, 1));
        place(black(HivePieceType.ANT), new HexCoordinate(0, 1));
        place(white(HivePieceType.ANT), new HexCoordinate(-1, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        long uniqueDestinations = moves.stream().map(MovePiece::to).distinct().count();

        assertEquals(moves.size(), uniqueDestinations);
    }

    @Test
    @DisplayName("all spider moves originate from current position")
    void allMovesOriginateFromCurrentPosition() {
        place(white(HivePieceType.SPIDER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.ANT), new HexCoordinate(3, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.stream().allMatch(m -> m.from().equals(ORIGIN)));
    }

    @Test
    @DisplayName("spider that is a bridge has no moves")
    void bridgeSpiderNoMoves() {
        // Spider in the middle of a line — removing it splits the hive
        place(black(HivePieceType.ANT), new HexCoordinate(-1, 0));
        place(white(HivePieceType.SPIDER), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }
}
