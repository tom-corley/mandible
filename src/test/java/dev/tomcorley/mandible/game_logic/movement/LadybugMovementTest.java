package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;
import dev.tomcorley.mandible.game_logic.TestPieceFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LadybugMovementTest {

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
    @DisplayName("ladybug always ends on ground level (empty cell)")
    void alwaysLandsOnEmptyCell() {
        // Ladybug at edge of a cluster: climbs up, across, and down
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        for (MovePiece move : moves) {
            assertFalse(grid.isCoordinateOccupied(move.to()),
                    "Ladybug must land on empty cell, not " + move.to());
        }
    }

    @Test
    @DisplayName("ladybug can reach positions a queen cannot")
    void reachesPositionsBeyondQueenRange() {
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Ladybug goes over the top — should reach positions not adjacent to origin
        boolean hasNonAdjacentDest = destinations.stream()
                .anyMatch(d -> !ORIGIN.getNeighbours().contains(d));
        assertTrue(hasNonAdjacentDest, "Ladybug should reach beyond queen range");
    }

    @Test
    @DisplayName("ladybug alone on board has no moves")
    void ladybugAloneNoMoves() {
        place(white(HivePieceType.LADYBUG), ORIGIN);

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("ladybug with one neighbour has moves (climb up, across itself is invalid, so needs 2+ pieces)")
    void withOneNeighbour() {
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // With only one adjacent piece, ladybug climbs up to (1,0) then needs to climb across
        // to another occupied cell — but there is none. So no complete up-across-down path.
        // Unless climb-across allows the same cell? Let's verify:
        // climbAcross from (1,0) checks occupied neighbours of (1,0) in the copy grid (ladybug removed).
        // Only the ant remains at (1,0) — its occupied neighbours? None that are occupied.
        // So no moves.
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("ladybug needs at least two adjacent pieces for a path")
    void needsTwoAdjacentPieces() {
        // Two adjacent pieces: ladybug can climb up onto one, across to the other, down
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertFalse(moves.isEmpty(), "Two neighbours should give the ladybug paths");
    }

    @Test
    @DisplayName("all ladybug moves originate from current position")
    void allMovesOriginateFromCurrentPosition() {
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.stream().allMatch(m -> m.from().equals(ORIGIN)));
    }

    @Test
    @DisplayName("ladybug produces no duplicate destinations")
    void noDuplicateDestinations() {
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        long uniqueCount = moves.stream().map(MovePiece::to).distinct().count();
        assertEquals(moves.size(), uniqueCount);
    }

    @Test
    @DisplayName("ladybug that is a bridge has no moves")
    void bridgeLadybugNoMoves() {
        place(black(HivePieceType.ANT), new HexCoordinate(-1, 0));
        place(white(HivePieceType.LADYBUG), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }
}
