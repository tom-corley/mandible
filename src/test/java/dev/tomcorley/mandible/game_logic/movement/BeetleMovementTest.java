package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;
import dev.tomcorley.mandible.game_logic.TestPieceFactory;

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

        @Test
        @DisplayName("climb-across targets where beetle stays at same height")
        void climbAcrossKeepsSameHeight() {
            // Origin: height 2 (ant + beetle), (-1,0): height 1 (queen)
            // Beetle at height 2 → lands on height 1+1=2 → same height → climb across
            stackBeetleOnOrigin();

            List<MovePiece> climbAcross = BeetleMovement.getValidClimbAcrossMoves(ORIGIN, grid);
            List<HexCoordinate> destinations = climbAcross.stream().map(MovePiece::to).toList();

            assertTrue(destinations.contains(new HexCoordinate(-1, 0)));
        }

        @Test
        @DisplayName("climb-across blocked by gate")
        void climbAcrossBlockedByGate() {
            // Beetle on stack of 2 at origin, with two height-2 flanking stacks forming a gate
            place(black(HivePieceType.ANT), ORIGIN);
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN));
            // Origin: height 2

            // Target at NE: need a height-1 stack (climb-across from height 2 needs dest that results in same height)
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.NE));

            // Flanking stacks at N and SE, both height 2
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.N)));

            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.SE)));

            List<MovePiece> moves = BeetleMovement.getValidClimbAcrossMoves(ORIGIN, grid);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // NE should be blocked by the gate formed by N and SE
            assertFalse(destinations.contains(ORIGIN.add(HexDirection.NE)));
        }

        @Test
        @DisplayName("climb-up allowed when gate height is below travel height")
        void climbUpAllowedWhenGateBelowTravelHeight() {
            // Gate (N and SE) at height 1. Beetle (x=1) climbing to NE (z=1): travel height = 2.
            // Gate 1 < travel 2 → allowed.
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.NE));

            List<MovePiece> climbUps = BeetleMovement.getValidClimbUpMoves(ORIGIN, grid);
            List<HexCoordinate> destinations = climbUps.stream().map(MovePiece::to).toList();

            assertTrue(destinations.contains(ORIGIN.add(HexDirection.NE)));
        }

        @Test
        @DisplayName("climb-up blocked when gate height equals travel height")
        void climbUpBlockedByGate() {
            // Beetle at ORIGIN (x=1), NE neighbor at height 1 → travel height = max(1,2) = 2.
            // Gate: N and SE each at height 2. Gate 2 >= travel 2 → BLOCKED.
            place(white(HivePieceType.BEETLE), ORIGIN);
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.NE));  // climb target

            // Build N to height 2
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.N)));

            // Build SE to height 2
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.SE)));

            List<MovePiece> moves = BeetleMovement.getValidClimbUpMoves(ORIGIN, grid);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            assertFalse(destinations.contains(ORIGIN.add(HexDirection.NE)),
                "Climb-up to NE must be blocked: N and SE gate at height 2 equals travel height 2");
            // Climbing to N (travel height 3) still allowed — different gate pieces
            assertTrue(destinations.contains(ORIGIN.add(HexDirection.N)));
        }

        @Test
        @DisplayName("climb-down allowed when gate height is below origin height")
        void climbDownAllowedWhenGateBelowOriginHeight() {
            // Gate (N and SE) at height 1. Beetle (x=2) descending to empty NE: travel height = 2.
            // Gate 1 < travel 2 → allowed.
            place(black(HivePieceType.ANT), ORIGIN);
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(-1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN));
            // Origin: height 2

            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));

            List<MovePiece> climbDowns = BeetleMovement.getValidClimbDownMoves(ORIGIN, grid);
            List<HexCoordinate> destinations = climbDowns.stream().map(MovePiece::to).toList();

            assertTrue(destinations.contains(ORIGIN.add(HexDirection.NE)));
        }

        @Test
        @DisplayName("climb-down blocked when gate height equals origin height")
        void climbDownBlockedByGate() {
            // Beetle at ORIGIN (x=2), NE empty → travel height = max(2,1) = 2.
            // Gate: N and SE each at height 2. Gate 2 >= travel 2 → BLOCKED.
            place(black(HivePieceType.ANT), ORIGIN);
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN));  // ORIGIN: height 2

            // Build N to height 2
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.N)));

            // Build SE to height 2
            place(black(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));
            place(white(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.SE)));

            // NE is empty. NW is also empty and has no height-2 gate.
            List<MovePiece> moves = BeetleMovement.getValidClimbDownMoves(ORIGIN, grid);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            assertFalse(destinations.contains(ORIGIN.add(HexDirection.NE)),
                "Climb-down to NE must be blocked: N and SE gate at height 2 equals origin height 2");
            assertTrue(destinations.contains(ORIGIN.add(HexDirection.NW)),
                "Climb-down to NW is allowed: no height-2 gate in that direction");
        }

        @Test
        @DisplayName("climb-down includes empty neighbours")
        void climbDownToEmpty() {
            stackBeetleOnOrigin();

            List<MovePiece> climbDowns = BeetleMovement.getValidClimbDownMoves(ORIGIN, grid);

            assertFalse(climbDowns.isEmpty());
            for (MovePiece move : climbDowns) {
                assertTrue(grid.getStackHeight(move.to()) < grid.getStackHeight(ORIGIN));
            }
        }

        @Test
        @DisplayName("climb-up only targets taller destinations")
        void climbUpOnlyToTaller() {
            stackBeetleOnOrigin();
            // Origin: height 2. (-1,0): height 1.

            List<MovePiece> climbUps = BeetleMovement.getValidClimbUpMoves(ORIGIN, grid);

            for (MovePiece move : climbUps) {
                assertTrue(grid.getStackHeight(move.to()) >= grid.getStackHeight(ORIGIN));
            }
        }
    }
}
