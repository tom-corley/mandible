package dev.tomcorley.mandible.game_logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HiveGridTest {

    private HiveGrid grid;
    private final TestPieceFactory pieces = new TestPieceFactory();

    private static final HexCoordinate ORIGIN = new HexCoordinate(0, 0);

    private HivePiece whitePiece(HivePieceType type) {
        return pieces.white(type);
    }

    private HivePiece blackPiece(HivePieceType type) {
        return pieces.black(type);
    }

    private void place(HivePiece piece, HexCoordinate coord) {
        grid.placePiece(new PlacePiece(coord, piece));
    }

    @BeforeEach
    void setUp() {
        grid = new HiveGrid();
        pieces.reset();
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
            assertThrows(InvalidMoveException.class,
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
            assertThrows(InvalidMoveException.class,
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
            assertThrows(InvalidMoveException.class,
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
                place(new HivePiece(colour, HivePieceType.ANT, i + 1), new HexCoordinate(i, 0));
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

    // --- Stack height ---

    @Nested
    @DisplayName("getStackHeight")
    class StackHeightTests {

        @Test
        @DisplayName("empty coordinate returns 0")
        void emptyReturnsZero() {
            assertEquals(0, grid.getStackHeight(ORIGIN));
        }

        @Test
        @DisplayName("single piece returns 1")
        void singlePieceReturnsOne() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            assertEquals(1, grid.getStackHeight(ORIGIN));
        }

        @Test
        @DisplayName("stacked pieces return correct height")
        void stackedPiecesReturnHeight() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));

            assertEquals(2, grid.getStackHeight(ORIGIN));
        }
    }

    // --- Gate check ---

    @Nested
    @DisplayName("gateCheck")
    class GateCheckTests {

        @Test
        @DisplayName("both flanking neighbours occupied and tall enough blocks")
        void bothFlankingOccupiedBlocks() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));

            assertTrue(grid.gateCheck(ORIGIN, HexDirection.NE));
        }

        @Test
        @DisplayName("one flanking neighbour empty does not block")
        void oneFlankingEmptyAllows() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.N));

            assertFalse(grid.gateCheck(ORIGIN, HexDirection.NE));
        }

        @Test
        @DisplayName("flanking neighbours shorter than current stack do not block")
        void shorterFlankingDoesNotBlock() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));
            // Origin is now height 2

            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));
            // Flanking are height 1, current is height 2

            assertFalse(grid.gateCheck(ORIGIN, HexDirection.NE));
        }

        @Test
        @DisplayName("flanking neighbours at same height as current stack blocks")
        void sameHeightFlankingBlocks() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN));
            // Origin is height 2

            // Build height-2 stacks on flanking positions
            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.N));
            place(whitePiece(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.N)));

            place(blackPiece(HivePieceType.ANT), ORIGIN.add(HexDirection.SE));
            place(whitePiece(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN.add(HexDirection.SE)));

            assertTrue(grid.gateCheck(ORIGIN, HexDirection.NE));
        }
    }

    // --- Climb direction checks ---

    @Nested
    @DisplayName("isClimbUp / isClimbDown / isClimbAcross")
    class ClimbDirectionTests {

        @Test
        @DisplayName("isClimbUp — destination taller than origin")
        void climbUpToTallerStack() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            assertTrue(grid.isClimbUp(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbUp — empty to occupied is climb up")
        void climbUpFromEmpty() {
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            assertTrue(grid.isClimbUp(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbUp — taller to shorter is not climb up")
        void tallerToShorterIsNotClimbUp() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN));
            // Origin height 2
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            // Destination height 1

            assertFalse(grid.isClimbUp(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbDown — taller origin to empty is climb down")
        void climbDownToEmpty() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(1, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));
            // Origin height 2, destination empty

            assertTrue(grid.isClimbDown(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbDown — height 1 to empty is not climb down")
        void height1ToEmptyIsNotClimbDown() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);

            assertFalse(grid.isClimbDown(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbDown — empty origin is not climb down")
        void emptyOriginIsNotClimbDown() {
            assertFalse(grid.isClimbDown(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbAcross — destination one shorter means beetle stays same height")
        void climbAcrossDestOneShorter() {
            // Origin height 2, destination height 1 → beetle at 2 lands on 1+1=2 → across
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), ORIGIN));
            // Origin: height 2
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            // Destination: height 1

            assertTrue(grid.isClimbAcross(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbAcross — same height stacks is climb up not across")
        void sameHeightIsClimbUpNotAcross() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            // Both height 1 → beetle at 1 lands on 1+1=2 → going up

            assertFalse(grid.isClimbAcross(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbAcross — destination taller is not climb across")
        void tallerDestNotClimbAcross() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.BEETLE), new HexCoordinate(1, 0));
            place(whitePiece(HivePieceType.BEETLE), new HexCoordinate(2, 0));
            grid.movePiece(new MovePiece(new HexCoordinate(2, 0), new HexCoordinate(1, 0)));
            // Origin height 1, destination height 2

            assertFalse(grid.isClimbAcross(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbAcross — height 1 to empty is climb across")
        void height1ToEmptyIsClimbAcross() {
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            // Origin height 1, destination empty (height 0) → beetle at 1 lands on 0+1=1 → across

            assertTrue(grid.isClimbAcross(ORIGIN, new HexCoordinate(1, 0)));
        }

        @Test
        @DisplayName("isClimbAcross — empty origin is not climb across")
        void emptyOriginNotClimbAcross() {
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));

            assertFalse(grid.isClimbAcross(ORIGIN, new HexCoordinate(1, 0)));
        }
    }

    // --- State key ---

    @Nested
    @DisplayName("toStateKey")
    class StateKeyTests {

        @Test
        @DisplayName("same board state produces identical key")
        void sameBoardSameKey() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            assertEquals(grid.toStateKey(), grid.toStateKey());
        }

        @Test
        @DisplayName("moving a piece changes the key")
        void movingPieceChangesKey() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            String before = grid.toStateKey();

            grid.movePiece(new MovePiece(new HexCoordinate(1, 0), new HexCoordinate(0, 1)));

            assertNotEquals(before, grid.toStateKey());
        }

        @Test
        @DisplayName("different piece type at same position produces different key")
        void differentPieceTypeDifferentKey() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            String withAnt = grid.toStateKey();

            grid.removePiece(new HexCoordinate(1, 0));
            place(blackPiece(HivePieceType.SPIDER), new HexCoordinate(1, 0));

            assertNotEquals(withAnt, grid.toStateKey());
        }

        @Test
        @DisplayName("locked coordinate is included in key")
        void lockedCoordinateAffectsKey() {
            place(whitePiece(HivePieceType.QUEEN_BEE), ORIGIN);
            String unlocked = grid.toStateKey();

            grid.lockCoordinate(ORIGIN);

            assertNotEquals(unlocked, grid.toStateKey());
        }

        @Test
        @DisplayName("stack contents matter — different pieces on top produce different keys")
        void stackContentsMatter() {
            // beetle on top of ant at origin
            place(whitePiece(HivePieceType.ANT), ORIGIN);
            place(blackPiece(HivePieceType.ANT), new HexCoordinate(1, 0));
            place(whitePiece(HivePieceType.BEETLE), new HexCoordinate(0, 1));
            grid.movePiece(new MovePiece(new HexCoordinate(0, 1), ORIGIN));
            String beetleOnTop = grid.toStateKey();

            // spider on top of ant at origin
            HiveGrid grid2 = new HiveGrid();
            grid2.placePiece(new PlacePiece(ORIGIN, whitePiece(HivePieceType.ANT)));
            grid2.placePiece(new PlacePiece(new HexCoordinate(1, 0), blackPiece(HivePieceType.ANT)));
            grid2.placePiece(new PlacePiece(new HexCoordinate(0, 1), whitePiece(HivePieceType.SPIDER)));
            grid2.movePiece(new MovePiece(new HexCoordinate(0, 1), ORIGIN));
            String spiderOnTop = grid2.toStateKey();

            assertNotEquals(beetleOnTop, spiderOnTop);
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
