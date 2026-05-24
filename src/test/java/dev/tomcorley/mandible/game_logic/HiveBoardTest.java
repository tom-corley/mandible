package dev.tomcorley.mandible.game_logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HiveBoardTest {

    private HiveBoard board;
    private final TestPieceFactory pieces = new TestPieceFactory();

    private HivePiece white(HivePieceType type) {
        return pieces.white(type);
    }

    private HivePiece black(HivePieceType type) {
        return pieces.black(type);
    }

    @BeforeEach
    void setUp() {
        board = new HiveBoard();
        pieces.reset();
    }

    // --- makeMove dispatch ---

    @Nested
    @DisplayName("makeMove")
    class MakeMoveTests {

        @Test
        @DisplayName("PlacePiece updates both grid and pieceLocations")
        void placePieceUpdatesState() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            HexCoordinate coord = new HexCoordinate(0, 0);

            board.makeMove(new PlacePiece(coord, queen));

            assertTrue(board.isPiecePlaced(queen));
            assertTrue(board.getGrid().isCoordinateOccupied(coord));
            assertEquals(coord, board.getPieceLocations().get(queen));
        }

        @Test
        @DisplayName("MovePiece updates pieceLocations to new coordinate")
        void movePieceUpdatesLocation() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            HivePiece ant = black(HivePieceType.ANT);
            HexCoordinate from = new HexCoordinate(0, 0);
            HexCoordinate adjacent = new HexCoordinate(1, 0);
            HexCoordinate dest = new HexCoordinate(1, -1);

            board.makeMove(new PlacePiece(from, queen));
            board.makeMove(new PlacePiece(adjacent, ant));
            board.makeMove(new MovePiece(from, dest));

            assertEquals(dest, board.getPieceLocations().get(queen));
            assertFalse(board.getGrid().isCoordinateOccupied(from));
            assertTrue(board.getGrid().isCoordinateOccupied(dest));
        }

        @Test
        @DisplayName("placing multiple pieces tracks all of them")
        void tracksMultiplePieces() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            HivePiece ant = black(HivePieceType.ANT);

            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), queen));
            board.makeMove(new PlacePiece(new HexCoordinate(1, 0), ant));

            assertEquals(2, board.getPieceLocations().size());
            assertTrue(board.isPiecePlaced(queen));
            assertTrue(board.isPiecePlaced(ant));
        }
    }

    // --- isPiecePlaced ---

    @Nested
    @DisplayName("isPiecePlaced")
    class IsPiecePlacedTests {

        @Test
        @DisplayName("returns false for unplaced piece")
        void unplacedPiece() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            assertFalse(board.isPiecePlaced(queen));
        }

        @Test
        @DisplayName("returns true after piece is placed")
        void afterPlacement() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), queen));
            assertTrue(board.isPiecePlaced(queen));
        }

        @Test
        @DisplayName("different piece instance of same type is not considered placed")
        void differentInstanceNotPlaced() {
            HivePiece queen1 = new HivePiece(PlayerColour.WHITE, HivePieceType.QUEEN_BEE, 1);
            HivePiece queen2 = new HivePiece(PlayerColour.WHITE, HivePieceType.QUEEN_BEE, 2);

            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), queen1));

            assertTrue(board.isPiecePlaced(queen1));
            assertFalse(board.isPiecePlaced(queen2));
        }
    }

    // --- isPieceOnTopOfStack ---

    @Nested
    @DisplayName("isPieceOnTopOfStack")
    class IsOnTopTests {

        @Test
        @DisplayName("single piece at coordinate is on top")
        void singlePieceIsOnTop() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), queen));

            assertTrue(board.isPieceOnTopOfStack(queen));
        }

        @Test
        @DisplayName("unplaced piece is not on top")
        void unplacedNotOnTop() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            assertFalse(board.isPieceOnTopOfStack(queen));
        }

        @Test
        @DisplayName("piece under a beetle is not on top")
        void buriedPieceNotOnTop() {
            HivePiece ant = white(HivePieceType.ANT);
            HivePiece beetle = black(HivePieceType.BEETLE);
            HivePiece anchor = white(HivePieceType.QUEEN_BEE);

            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), ant));
            board.makeMove(new PlacePiece(new HexCoordinate(1, 0), beetle));
            board.makeMove(new PlacePiece(new HexCoordinate(-1, 0), anchor));
            board.makeMove(new MovePiece(new HexCoordinate(1, 0), new HexCoordinate(0, 0)));

            assertFalse(board.isPieceOnTopOfStack(ant));
            assertTrue(board.isPieceOnTopOfStack(beetle));
        }
    }

    // --- getValidMovesForPiece ---

    @Nested
    @DisplayName("getValidMovesForPiece")
    class ValidMovesTests {

        @Test
        @DisplayName("unplaced piece returns empty list")
        void unplacedPieceNoMoves() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            List<MovePiece> moves = board.getValidMovesForPiece(queen);
            assertTrue(moves.isEmpty());
        }

        @Test
        @DisplayName("buried piece returns empty list")
        void buriedPieceNoMoves() {
            HivePiece ant = white(HivePieceType.ANT);
            HivePiece beetle = black(HivePieceType.BEETLE);
            HivePiece anchor = white(HivePieceType.QUEEN_BEE);

            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), ant));
            board.makeMove(new PlacePiece(new HexCoordinate(1, 0), beetle));
            board.makeMove(new PlacePiece(new HexCoordinate(-1, 0), anchor));
            board.makeMove(new MovePiece(new HexCoordinate(1, 0), new HexCoordinate(0, 0)));

            List<MovePiece> moves = board.getValidMovesForPiece(ant);
            assertTrue(moves.isEmpty());
        }

        @Test
        @DisplayName("placed piece with valid moves returns non-empty list")
        void placedPieceHasMoves() {
            HivePiece queen = white(HivePieceType.QUEEN_BEE);
            HivePiece ant = black(HivePieceType.ANT);
            HivePiece spider = white(HivePieceType.SPIDER);

            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), queen));
            board.makeMove(new PlacePiece(new HexCoordinate(1, 0), ant));
            board.makeMove(new PlacePiece(new HexCoordinate(-1, 0), spider));

            // Queen at origin is a bridge — no moves
            // But the end pieces should have moves
            List<MovePiece> spiderMoves = board.getValidMovesForPiece(spider);
            assertFalse(spiderMoves.isEmpty());
        }
    }

    // --- getValidPlacementCoordinates ---

    @Nested
    @DisplayName("getValidPlacementCoordinates")
    class PlacementCoordinatesTests {

        @Test
        @DisplayName("delegates to grid with player colour")
        void delegatesToGrid() {
            Player whitePlayer = new Player(PlayerColour.WHITE, "white", new BotController());

            List<HexCoordinate> coords = board.getValidPlacementCoordinates(whitePlayer);

            // Empty board — only origin
            assertEquals(1, coords.size());
            assertEquals(new HexCoordinate(0, 0), coords.get(0));
        }

        @Test
        @DisplayName("returns different results per colour after pieces placed")
        void colourSpecificResults() {
            Player whitePlayer = new Player(PlayerColour.WHITE, "white", new BotController());
            Player blackPlayer = new Player(PlayerColour.BLACK, "black", new BotController());

            board.makeMove(new PlacePiece(new HexCoordinate(0, 0), white(HivePieceType.QUEEN_BEE)));
            board.makeMove(new PlacePiece(new HexCoordinate(1, 0), black(HivePieceType.ANT)));

            List<HexCoordinate> whiteCoords = board.getValidPlacementCoordinates(whitePlayer);
            List<HexCoordinate> blackCoords = board.getValidPlacementCoordinates(blackPlayer);

            // Different colour restrictions should produce different sets
            assertNotEquals(whiteCoords, blackCoords);
        }
    }
}
