package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeetleMovementTest {

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

    @Nested
    @DisplayName("ground beetle (not on a stack)")
    class GroundBeetleTests {

        @Test
        @DisplayName("can slide like a queen to adjacent empty spaces")
        void canSlide() {
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // Slide destinations (same as queen)
            assertTrue(destinations.contains(new HexCoordinate(0, 1)));
            assertTrue(destinations.contains(new HexCoordinate(1, -1)));
        }

        @Test
        @DisplayName("can climb onto adjacent occupied piece")
        void canClimbUp() {
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // Can climb onto the ant
            assertTrue(destinations.contains(new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("has both slide and climb moves with one neighbour")
        void slideAndClimbCombined() {
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            // 2 slide positions + 1 climb = 3 total
            assertEquals(3, moves.size());
        }

        @Test
        @DisplayName("alone on board has no moves")
        void aloneNoMoves() {
            place(white(HivePieceType.BEETLE), ORIGIN);

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            assertTrue(moves.isEmpty());
        }

        @Test
        @DisplayName("bridge beetle has no moves")
        void bridgeNoMoves() {
            place(black(HivePieceType.ANT), new HexCoordinate(-1, 0));
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            assertTrue(moves.isEmpty());
        }

        @Test
        @DisplayName("can climb onto any of multiple adjacent pieces")
        void climbsOntoMultipleNeighbours() {
            // Beetle with two adjacent pieces — connected arc so beetle is movable
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // Can climb onto both neighbours
            assertTrue(destinations.contains(new HexCoordinate(1, 0)));
            assertTrue(destinations.contains(new HexCoordinate(0, 1)));
        }
    }

    @Nested
    @DisplayName("stacked beetle (on top of another piece)")
    class StackedBeetleTests {

        private void stackBeetleOnOrigin() {
            // Place ant at origin, beetle adjacent, then move beetle on top of ant
            place(black(HivePieceType.ANT), ORIGIN);
            place(white(HivePieceType.BEETLE), new HexCoordinate(1, 0));
            // Need a third piece so the hive stays connected when beetle leaves (1,0)
            place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(-1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));
        }

        @Test
        @DisplayName("stacked beetle can climb down to empty neighbours")
        void canClimbDown() {
            stackBeetleOnOrigin();

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // Can climb down to some empty neighbour positions
            boolean hasEmptyDestination = destinations.stream()
                    .anyMatch(d -> !grid.isCoordinateOccupied(d));
            assertTrue(hasEmptyDestination);
        }

        @Test
        @DisplayName("stacked beetle can climb across to occupied neighbour")
        void canClimbAcross() {
            stackBeetleOnOrigin();

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // (-1,0) is occupied — beetle can climb across to it
            assertTrue(destinations.contains(new HexCoordinate(-1, 0)));
        }

        @Test
        @DisplayName("stacked beetle does not use slide rules")
        void noSlideRulesWhenStacked() {
            stackBeetleOnOrigin();

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            // Stacked beetle should be able to reach all 6 neighbours
            // (climb across occupied + climb down to empty)
            // Unless something specific blocks it
            assertTrue(moves.size() >= 4, "Stacked beetle should have many move options");
        }

        @Test
        @DisplayName("stacked beetle is always movable (never a bridge)")
        void stackedAlwaysMovable() {
            stackBeetleOnOrigin();

            // The beetle is on top of a stack — removing it doesn't break the hive
            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            assertFalse(moves.isEmpty());
        }

        @Test
        @DisplayName("beetle on top is the piece returned by getPiece")
        void topOfStack() {
            place(black(HivePieceType.ANT), ORIGIN);
            HivePiece beetle = white(HivePieceType.BEETLE);
            place(beetle, new HexCoordinate(1, 0));
            place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(-1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));

            assertSame(beetle, grid.getPiece(ORIGIN));
            assertEquals(2, grid.getGrid().get(ORIGIN).size());
        }
    }
}
