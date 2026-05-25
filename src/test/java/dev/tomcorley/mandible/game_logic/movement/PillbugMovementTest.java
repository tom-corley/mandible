package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;
import dev.tomcorley.mandible.game_logic.TestPieceFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PillbugMovementTest {

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
    @DisplayName("own movement (queen-like)")
    class OwnMovementTests {

        @Test
        @DisplayName("pillbug can slide like a queen")
        void slidesLikeQueen() {
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

            // Queen-like slides
            assertTrue(destinations.contains(new HexCoordinate(0, 1)));
            assertTrue(destinations.contains(new HexCoordinate(1, -1)));
        }

        @Test
        @DisplayName("pillbug alone on board has no moves")
        void aloneNoMoves() {
            place(white(HivePieceType.PILLBUG), ORIGIN);

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            assertTrue(moves.isEmpty());
        }

        @Test
        @DisplayName("bridge pillbug has no moves")
        void bridgeNoMoves() {
            place(black(HivePieceType.ANT), new HexCoordinate(-1, 0));
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            assertTrue(moves.isEmpty());
        }
    }

    @Nested
    @DisplayName("special ability (moving neighbours)")
    class SpecialAbilityTests {

        @Test
        @DisplayName("can move an unstacked movable neighbour to an empty adjacent space")
        void movesNeighbourToEmptySpace() {
            // Pillbug at origin with a movable neighbour
            // Triangle: pillbug(0,0), ant(1,0), queen(0,1)
            // All connected — ant at (1,0) is movable
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(0, 1));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            // Should include moves where the ant at (1,0) is moved to an empty pillbug neighbour
            boolean hasNeighbourMove = moves.stream()
                    .anyMatch(m -> m.from().equals(new HexCoordinate(1, 0)));
            assertTrue(hasNeighbourMove, "Pillbug should be able to move a neighbour");
        }

        @Test
        @DisplayName("neighbour moves go to empty spaces adjacent to pillbug")
        void neighbourMovesToPillbugAdjacent() {
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(0, 1));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            // All neighbour-moving moves should land on empty pillbug-adjacent cells
            List<MovePiece> neighbourMoves = moves.stream()
                    .filter(m -> !m.from().equals(ORIGIN))
                    .toList();

            for (MovePiece move : neighbourMoves) {
                assertTrue(ORIGIN.getNeighbours().contains(move.to()),
                        move.to() + " should be adjacent to pillbug at origin");
                assertFalse(grid.isCoordinateOccupied(move.to()),
                        move.to() + " should be empty");
            }
        }

        @Test
        @DisplayName("cannot move a stacked neighbour")
        void cannotMoveStackedNeighbour() {
            // Stack a beetle on top of a piece adjacent to pillbug
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(white(HivePieceType.BEETLE), new HexCoordinate(0, 1));
            // Move beetle on top of ant
            grid.movePiece(new MovePiece(new HexCoordinate(0, 1), new HexCoordinate(1, 0)));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            // Pillbug should not generate moves from the stacked position (1,0)
            boolean hasStackedNeighbourMove = moves.stream()
                    .anyMatch(m -> m.from().equals(new HexCoordinate(1, 0)));
            assertFalse(hasStackedNeighbourMove, "Cannot move stacked neighbour");
        }

        @Test
        @DisplayName("cannot move a bridge neighbour")
        void cannotMoveBridgeNeighbour() {
            // Pillbug at origin, bridge piece at (1,0) connecting (2,0)
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(white(HivePieceType.ANT), new HexCoordinate(2, 0));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            // (1,0) is a bridge between (0,0) and (2,0) — pillbug can't move it
            boolean hasBridgeMove = moves.stream()
                    .anyMatch(m -> m.from().equals(new HexCoordinate(1, 0)));
            assertFalse(hasBridgeMove, "Cannot move a bridge piece");
        }

        @Test
        @DisplayName("pillbug moves include both own slides and neighbour moves")
        void combinesBothMoveTypes() {
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(0, 1));

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

            boolean hasOwnMove = moves.stream()
                    .anyMatch(m -> m.from().equals(ORIGIN));
            boolean hasNeighbourMove = moves.stream()
                    .anyMatch(m -> !m.from().equals(ORIGIN));

            assertTrue(hasOwnMove, "Should include pillbug's own slides");
            assertTrue(hasNeighbourMove, "Should include neighbour-moving ability");
        }
    }

    @Nested
    @DisplayName("locking")
    class LockingTests {

        // pillbug(0,0), ant(1,0), queen(0,1) — ant is movable, board stays connected
        private final HexCoordinate antCoord = new HexCoordinate(1, 0);

        @BeforeEach
        void setUpBoard() {
            place(white(HivePieceType.PILLBUG), ORIGIN);
            place(black(HivePieceType.ANT), antCoord);
            place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(0, 1));
        }

        @Test
        @DisplayName("locked piece cannot be thrown by pillbug")
        void lockedNeighbourCannotBeThrown() {
            grid.lockCoordinate(antCoord);

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            boolean canThrow = moves.stream().anyMatch(m -> m.from().equals(antCoord));
            assertFalse(canThrow);
        }

        @Test
        @DisplayName("locked piece cannot move itself")
        void lockedPieceCannotMoveSelf() {
            grid.lockCoordinate(antCoord);

            List<MovePiece> moves = grid.getValidMovesForPiece(antCoord);
            assertTrue(moves.isEmpty());
        }

        @Test
        @DisplayName("lock clears — piece can be thrown again after lock is cleared")
        void lockClears() {
            grid.lockCoordinate(antCoord);
            grid.clearLockedCoordinate();

            List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
            boolean canThrow = moves.stream().anyMatch(m -> m.from().equals(antCoord));
            assertTrue(canThrow);
        }
    }
}
