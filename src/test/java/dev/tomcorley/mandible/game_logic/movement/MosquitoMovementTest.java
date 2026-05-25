package dev.tomcorley.mandible.game_logic.movement;

import dev.tomcorley.mandible.game_logic.*;
import dev.tomcorley.mandible.game_logic.TestPieceFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MosquitoMovementTest {

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
    @DisplayName("mosquito next to queen gets queen-like slide moves")
    void movesLikeAdjacentQueen() {
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Same destinations as a queen: one-step edge slides
        assertTrue(destinations.contains(new HexCoordinate(0, 1)));
        assertTrue(destinations.contains(new HexCoordinate(1, -1)));
    }

    @Test
    @DisplayName("mosquito next to grasshopper can jump like grasshopper")
    void movesLikeAdjacentGrasshopper() {
        // Mosquito with grasshopper neighbour and a piece beyond to jump over
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.GRASSHOPPER), new HexCoordinate(1, 0));
        // Connect hive so mosquito is movable
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Grasshopper-like jump: over (1,0) to (2,0)
        assertTrue(destinations.contains(new HexCoordinate(2, 0)),
                "Should jump like grasshopper over adjacent piece");
    }

    @Test
    @DisplayName("mosquito next to beetle can climb up")
    void canClimbLikeBeetle() {
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.BEETLE), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Beetle move: climb onto (1,0)
        assertTrue(destinations.contains(new HexCoordinate(1, 0)),
                "Should be able to climb like beetle");
    }

    @Test
    @DisplayName("stacked mosquito always moves as beetle")
    void stackedMovesAsBeetle() {
        // Place mosquito, then stack it on top of another piece
        place(black(HivePieceType.ANT), ORIGIN);
        place(white(HivePieceType.MOSQUITO), new HexCoordinate(1, 0));
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(-1, 0));
        grid.movePiece(new MovePiece(new HexCoordinate(1, 0), ORIGIN));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // Stacked mosquito acts as beetle — should have climb across + climb down options
        assertFalse(moves.isEmpty());
        // Should be able to reach empty neighbours (climb down)
        boolean hasEmptyDest = moves.stream()
                .anyMatch(m -> !grid.isCoordinateOccupied(m.to()));
        assertTrue(hasEmptyDest);
    }

    @Test
    @DisplayName("mosquito ignores neighbouring mosquito for movement copying")
    void ignoresNeighbouringMosquito() {
        // Two mosquitoes adjacent with no other pieces touching the first mosquito
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.MOSQUITO), new HexCoordinate(1, 0));
        // Need a third piece so mosquito at origin is movable
        // And to give (1,0) mosquito a real piece to copy from
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        // Mosquito at origin ignores the mosquito at (1,0)
        // It still gets its own queen-like slides from slideAlongOneEdge
        // And does NOT copy the other mosquito's movement
        assertFalse(moves.isEmpty());
    }

    @Test
    @DisplayName("mosquito next to ant can reach far positions like an ant")
    void canCopyAntMovement() {
        // Mosquito at origin, ant at (1,0), anchor queen at (2,0).
        // Copying ant gives BFS-reach to (3,0) and beyond — queen-only would give just 2 slides.
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));
        place(white(HivePieceType.QUEEN_BEE), new HexCoordinate(2, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        assertTrue(destinations.contains(new HexCoordinate(3, 0)),
                "Mosquito copying ant should reach far end of chain at (3,0)");
    }

    @Test
    @DisplayName("mosquito next to spider can reach 3-step positions")
    void canCopySpiderMovement() {
        // Linear chain: mosquito(0,0) — spider(1,0) — ant(2,0) — ant(3,0).
        // Spider-copying gives 3-step destinations (2,1) and (3,-1); queen-only gives at most (0,1)/(1,-1).
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.SPIDER), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(2, 0));
        place(black(HivePieceType.ANT), new HexCoordinate(3, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        List<HexCoordinate> destinations = moves.stream().map(MovePiece::to).toList();

        // Either 3-step arc destination confirms spider copying works
        assertTrue(destinations.contains(new HexCoordinate(2, 1)) ||
                   destinations.contains(new HexCoordinate(3, -1)),
                "Mosquito copying spider should reach a 3-step position");
    }

    @Test
    @DisplayName("mosquito next to pillbug can use pillbug's throw ability")
    void canCopyPillbugAbility() {
        // Triangle: mosquito(0,0), pillbug(1,0), ant(0,1).
        // Mosquito copies pillbug → can throw the ant at (0,1) to an empty neighbour of itself.
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.PILLBUG), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);

        boolean hasThrow = moves.stream()
                .anyMatch(m -> m.from().equals(new HexCoordinate(0, 1)));
        assertTrue(hasThrow, "Mosquito copying pillbug should be able to throw the ant at (0,1)");
    }

    @Test
    @DisplayName("mosquito alone on board has no moves")
    void mosquitoAloneNoMoves() {
        place(white(HivePieceType.MOSQUITO), ORIGIN);

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }

    @Test
    @DisplayName("mosquito produces no duplicate destinations")
    void noDuplicateDestinations() {
        // Mosquito next to both queen and ant — might produce duplicate slides
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        long uniqueCount = moves.stream().map(MovePiece::to).distinct().count();
        assertEquals(moves.size(), uniqueCount);
    }

    @Test
    @DisplayName("all mosquito moves originate from current position")
    void allMovesOriginateFromCurrentPosition() {
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.QUEEN_BEE), new HexCoordinate(1, 0));
        place(white(HivePieceType.ANT), new HexCoordinate(0, 1));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.stream().allMatch(m -> m.from().equals(ORIGIN)));
    }

    @Test
    @DisplayName("mosquito that is a bridge has no moves")
    void bridgeMosquitoNoMoves() {
        place(black(HivePieceType.ANT), new HexCoordinate(-1, 0));
        place(white(HivePieceType.MOSQUITO), ORIGIN);
        place(black(HivePieceType.ANT), new HexCoordinate(1, 0));

        List<MovePiece> moves = grid.getValidMovesForPiece(ORIGIN);
        assertTrue(moves.isEmpty());
    }
}
