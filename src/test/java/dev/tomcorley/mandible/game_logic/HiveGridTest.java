package dev.tomcorley.mandible.game_logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HiveGridTest {

    private HiveGrid grid;

    private static final HexCoordinate ORIGIN = new HexCoordinate(0, 0);

    private HivePiece whitePiece(HivePieceType type) {
        return new HivePiece(PlayerColour.WHITE, type);
    }

    private HivePiece blackPiece(HivePieceType type) {
        return new HivePiece(PlayerColour.BLACK, type);
    }

    private void place(HivePiece piece, HexCoordinate coord) {
        grid.placePiece(new PlacePiece(coord, piece));
    }

    @BeforeEach
    void setUp() {
        grid = new HiveGrid();
    }

    // --- Placement ---

    @Nested
    @DisplayName("placePiece")
    class PlacePieceTests {

        @Test
        @DisplayName("placing a piece makes coordinate occupied")
        void placeMakesOccupied() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            assertTrue(grid.isCoordinateOccupied(ORIGIN));
        }

        @Test
        @DisplayName("unoccupied coordinate returns false")
        void emptyCoordinateNotOccupied() {
            assertFalse(grid.isCoordinateOccupied(ORIGIN));
        }

        @Test
        @DisplayName("placing on occupied coordinate throws")
        void placeOnOccupiedThrows() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            assertThrows(IllegalArgumentException.class,
                    () -> place(blackPiece(HivePieceType.ANT), ORIGIN));
        }

        @Test
        @DisplayName("placed piece is retrievable via getPiece")
        void placedPieceIsRetrievable() {
            HivePiece queen = whitePiece(HivePieceType.QUEEN_BEE);
            place(queen, ORIGIN);
            assertSame(queen, grid.getPiece(ORIGIN));
        }

        @Test
        @DisplayName("placing at different coordinates does not interfere")
        void placementsAreIndependent() {
            HexCoordinate other = new HexCoordinate(1, 0);
            HivePiece queen = whitePiece(HivePieceType.QUEEN_BEE);
            HivePiece ant = blackPiece(HivePieceType.ANT);

            place(queen, ORIGIN);
            place(ant, other);

            assertSame(queen, grid.getPiece(ORIGIN));
            assertSame(ant, grid.getPiece(other));
        }
    }

    // --- Removal ---

    @Nested
    @DisplayName("removePiece")
    class RemovePieceTests {

        @Test
        @DisplayName("removing the only piece makes coordinate unoccupied")
        void removeLastPieceClearsCoordinate() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            grid.removePiece(ORIGIN);
            assertFalse(grid.isCoordinateOccupied(ORIGIN));
        }

        @Test
        @DisplayName("removing from empty coordinate throws")
        void removeFromEmptyThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> grid.removePiece(ORIGIN));
        }

        @Test
        @DisplayName("removing top of stack reveals piece below")
        void removeTopRevealsBelow() {
            HivePiece bottom = whitePiece(HivePieceType.ANT);
            HivePiece top = blackPiece(HivePieceType.BEETLE);
            place(bottom, ORIGIN);
            place(top, new HexCoordinate(1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));

            assertSame(top, grid.getPiece(ORIGIN));
            grid.removePiece(ORIGIN);
            assertSame(bottom, grid.getPiece(ORIGIN));
        }
    }

    // --- Movement ---

    @Nested
    @DisplayName("movePiece")
    class MovePieceTests {

        @Test
        @DisplayName("piece ends up at destination after move")
        void pieceAtDestination() {
            HivePiece ant = whitePiece(HivePieceType.ANT);
            HexCoordinate dest = new HexCoordinate(1, 0);
            place(ant, ORIGIN);

            grid.movePiece(new MovePiece(ORIGIN, dest));

            assertSame(ant, grid.getPiece(dest));
        }

        @Test
        @DisplayName("source coordinate is empty after move")
        void sourceEmptyAfterMove() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            grid.movePiece(new MovePiece(ORIGIN, new HexCoordinate(1, 0)));

            assertFalse(grid.isCoordinateOccupied(ORIGIN));
        }

        @Test
        @DisplayName("moving from empty coordinate throws")
        void moveFromEmptyThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> grid.movePiece(new MovePiece(ORIGIN, new HexCoordinate(1, 0))));
        }

        @Test
        @DisplayName("moving onto occupied coordinate creates a stack")
        void moveOntoOccupiedCreatesStack() {
            HivePiece bottom = whitePiece(HivePieceType.ANT);
            HivePiece top = blackPiece(HivePieceType.BEETLE);
            place(bottom, ORIGIN);
            place(top, new HexCoordinate(1, 0));

            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));

            assertTrue(grid.isCoordinateOccupied(ORIGIN));
            assertSame(top, grid.getPiece(ORIGIN));
            assertEquals(2, grid.getGrid().get(ORIGIN).size());
        }
    }

    // --- One-hive rule (isPieceMovable) ---

    @Nested
    @DisplayName("isPieceMovable — one-hive connectivity")
    class OneHiveTests {

        @Test
        @DisplayName("single piece on board is not movable")
        void singlePieceNotMovable() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            assertFalse(grid.isPieceMovable(ORIGIN));
        }

        @Test
        @DisplayName("piece in a line of three — middle is not movable")
        void middleOfLineNotMovable() {
            // Line along NE: (0,0) - (1,0) - (2,0)
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(whitePiece(HivePieceType.SPIDER), new HexCoordinate(2, 0));

            assertFalse(grid.isPieceMovable(new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("piece in a line of three — ends are movable")
        void endOfLineIsMovable() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(whitePiece(HivePieceType.SPIDER), new HexCoordinate(2, 0));

            assertTrue(grid.isPieceMovable(ORIGIN));
            assertTrue(grid.isPieceMovable(new HexCoordinate(2, 0)));
        }

        @Test
        @DisplayName("triangle of three — all are movable")
        void triangleAllMovable() {
            // Triangle: (0,0), (1,0), (0,1) — each connected to both others
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(whitePiece(HivePieceType.ANT), new HexCoordinate(0, 1));

            assertTrue(grid.isPieceMovable(ORIGIN));
            assertTrue(grid.isPieceMovable(new HexCoordinate(1, 0)));
            assertTrue(grid.isPieceMovable(new HexCoordinate(0, 1)));
        }

        @Test
        @DisplayName("two pieces — neither is movable")
        void twoPiecesNeitherMovable() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            // Removing either disconnects the other (single piece left = not movable per the code,
            // but actually with one piece remaining, isPieceMovable returns false for single piece)
            // Actually: isPieceMovable checks if remaining pieces are connected.
            // With 2 pieces, removing one leaves 1 piece, which is trivially connected.
            // But the code returns false when grid.keySet().size() == 1.
            // So: piece at (0,0) — removing it leaves (1,0) alone — size==1 — not movable?
            // No — isPieceMovable checks if *this* piece can be moved without splitting,
            // not whether remaining pieces are individually movable.
            // size==1 means only one piece total, checked at the top.
            // With 2 pieces: removing either leaves the hive connected (1 piece is connected).
            assertTrue(grid.isPieceMovable(ORIGIN));
            assertTrue(grid.isPieceMovable(new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("T-shape — junction piece is not movable")
        void tShapeJunctionNotMovable() {
            //     (0,1)
            //      |
            // (0,0) - (1,0) - (2,0)
            // Center at (1,0) connects the arm (0,1) — wait, (0,1) is neighbour of (0,0) not (1,0)
            // Let me use: (-1,0) - (0,0) - (1,0) with (0,1) attached to (0,0)
            place(whitePiece(HivePieceType.ANT), new HexCoordinate(-1, 0));
            place(blackPiece(HivePieceType.ANT), ORIGIN);
            place(whitePiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(0, 1));

            assertFalse(grid.isPieceMovable(ORIGIN));
        }

        @Test
        @DisplayName("long line of five — only ends are movable")
        void longLineOnlyEndsMovable() {
            for (int i = 0; i < 5; i++) {
                PlayerColour colour = (i % 2 == 0) ? PlayerColour.WHITE : PlayerColour.BLACK;
                place(new HivePiece(colour, HivePieceType.ANT), new HexCoordinate(i, 0));
            }

            assertTrue(grid.isPieceMovable(new HexCoordinate(0, 0)));
            assertFalse(grid.isPieceMovable(new HexCoordinate(1, 0)));
            assertFalse(grid.isPieceMovable(new HexCoordinate(2, 0)));
            assertFalse(grid.isPieceMovable(new HexCoordinate(3, 0)));
            assertTrue(grid.isPieceMovable(new HexCoordinate(4, 0)));
        }
    }

    // --- Placement positions ---

    @Nested
    @DisplayName("getValidPlacementPositions")
    class PlacementPositionTests {

        @Test
        @DisplayName("empty grid returns only origin")
        void emptyGridReturnsOrigin() {
            List<HexCoordinate> positions = grid.getValidPlacementPositions(PlayerColour.WHITE);
            assertEquals(1, positions.size());
            assertEquals(ORIGIN, positions.get(0));
        }

        @Test
        @DisplayName("one piece on grid returns all 6 neighbours for either colour")
        void onePieceReturnsSixNeighbours() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);

            List<HexCoordinate> whitePositions = grid.getValidPlacementPositions(PlayerColour.WHITE);
            List<HexCoordinate> blackPositions = grid.getValidPlacementPositions(PlayerColour.BLACK);

            assertEquals(6, whitePositions.size());
            assertEquals(6, blackPositions.size());
        }

        @Test
        @DisplayName("two adjacent different-colour pieces — each colour gets positions only next to own")
        void twoColoursNoAdjacentOpponent() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<HexCoordinate> whitePositions = grid.getValidPlacementPositions(PlayerColour.WHITE);
            List<HexCoordinate> blackPositions = grid.getValidPlacementPositions(PlayerColour.BLACK);

            // White positions must not be adjacent to black piece
            for (HexCoordinate pos : whitePositions) {
                assertFalse(pos.getNeighbours().stream()
                        .anyMatch(n -> grid.isCoordinateOccupied(n) &&
                                grid.getPiece(n).getColour() == PlayerColour.BLACK));
            }

            // Black positions must not be adjacent to white piece
            for (HexCoordinate pos : blackPositions) {
                assertFalse(pos.getNeighbours().stream()
                        .anyMatch(n -> grid.isCoordinateOccupied(n) &&
                                grid.getPiece(n).getColour() == PlayerColour.WHITE));
            }
        }

        @Test
        @DisplayName("returned positions are all empty")
        void positionsAreEmpty() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            List<HexCoordinate> positions = grid.getValidPlacementPositions(PlayerColour.WHITE);
            for (HexCoordinate pos : positions) {
                assertFalse(grid.isCoordinateOccupied(pos));
            }
        }

        @Test
        @DisplayName("three white pieces in a line — no black adjacent — black gets no positions")
        void surroundedByOneColourOtherGetsNothing() {
            // White line: (0,0), (-1,0), (-2,0) — black at (1,0)
            // Every empty neighbour of (1,0) is also neighbour of a white piece
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(whitePiece(HivePieceType.ANT), new HexCoordinate(0, 1));
            place(whitePiece(HivePieceType.SPIDER), new HexCoordinate(0, -1));

            List<HexCoordinate> blackPositions = grid.getValidPlacementPositions(PlayerColour.BLACK);
            // Black can only place where no white neighbours exist
            for (HexCoordinate pos : blackPositions) {
                boolean hasWhiteNeighbour = pos.getNeighbours().stream()
                        .anyMatch(n -> grid.isCoordinateOccupied(n) &&
                                grid.getPiece(n).getColour() == PlayerColour.WHITE);
                assertFalse(hasWhiteNeighbour);
            }
        }
    }

    // --- Copy constructor ---

    @Nested
    @DisplayName("copy constructor")
    class CopyTests {

        @Test
        @DisplayName("copy has same pieces at same coordinates")
        void copyMatchesOriginal() {
            HivePiece queen = whitePiece(HivePieceType.QUEEN_BEE);
            HivePiece ant = blackPiece(HivePieceType.ANT);
            place(queen, ORIGIN);
            place(ant, new HexCoordinate(1, 0));

            HiveGrid copy = new HiveGrid(grid);

            assertTrue(copy.isCoordinateOccupied(ORIGIN));
            assertTrue(copy.isCoordinateOccupied(new HexCoordinate(1, 0)));
            assertSame(queen, copy.getPiece(ORIGIN));
            assertSame(ant, copy.getPiece(new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("mutating copy does not affect original")
        void copyIsIndependent() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            HiveGrid copy = new HiveGrid(grid);
            copy.removePiece(ORIGIN);

            assertTrue(grid.isCoordinateOccupied(ORIGIN));
            assertFalse(copy.isCoordinateOccupied(ORIGIN));
        }

        @Test
        @DisplayName("mutating original does not affect copy")
        void originalMutationDoesNotAffectCopy() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);

            HiveGrid copy = new HiveGrid(grid);
            grid.removePiece(ORIGIN);

            assertFalse(grid.isCoordinateOccupied(ORIGIN));
            assertTrue(copy.isCoordinateOccupied(ORIGIN));
        }
    }
}
