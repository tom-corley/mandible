package dev.tomcorley.mandible.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HiveGameTest {

    private HiveGame game;
    private Player white;
    private Player black;

    @BeforeEach
    void setUp() {
        game = GameFactory.createStandardBotVsBotGame();
        white = game.getWhitePlayer();
        black = game.getBlackPlayer();
    }

    private HivePiece pieceFromHand(Player player, HivePieceType type) {
        return player.getHand().stream()
                .filter(p -> p.getType() == type)
                .filter(p -> !game.getBoard().isPiecePlaced(p))
                .findFirst()
                .orElseThrow();
    }

    private void placeAt(Player player, HivePieceType type, HexCoordinate coord) {
        game.makeMove(new PlacePiece(coord, pieceFromHand(player, type)));
    }

    // --- Win condition ---

    @Nested
    @DisplayName("win condition detection")
    class WinConditionTests {

        @Test
        @DisplayName("new game is IN_PROGRESS")
        void newGameInProgress() {
            game.checkWinCondition();
            assertEquals(HiveGameState.IN_PROGRESS, game.getState());
        }

        @Test
        @DisplayName("unplaced queen is not considered surrounded")
        void unplacedQueenNotSurrounded() {
            placeAt(white, HivePieceType.ANT, new HexCoordinate(0, 0));
            game.checkWinCondition();
            assertEquals(HiveGameState.IN_PROGRESS, game.getState());
        }

        @Test
        @DisplayName("queen with empty neighbour is not surrounded")
        void queenWithEmptyNeighbourNotSurrounded() {
            placeAt(white, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(1, 0));

            game.checkWinCondition();
            assertEquals(HiveGameState.IN_PROGRESS, game.getState());
        }

        @Test
        @DisplayName("surrounded black queen means WHITE_WON")
        void surroundedBlackQueenWhiteWins() {
            // Place black queen at origin, surround with 6 pieces
            placeAt(black, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));

            HexCoordinate[] neighbours = {
                    new HexCoordinate(0, 1), new HexCoordinate(1, 0),
                    new HexCoordinate(1, -1), new HexCoordinate(0, -1),
                    new HexCoordinate(-1, 0), new HexCoordinate(-1, 1)
            };
            HivePieceType[] types = {
                    HivePieceType.ANT, HivePieceType.ANT, HivePieceType.ANT,
                    HivePieceType.SPIDER, HivePieceType.SPIDER, HivePieceType.GRASSHOPPER
            };

            for (int i = 0; i < 6; i++) {
                // Alternate colours to have enough pieces — surrounding counts regardless of colour
                Player placer = (i % 2 == 0) ? white : black;
                placeAt(placer, types[i], neighbours[i]);
            }

            game.checkWinCondition();
            assertEquals(HiveGameState.WHITE_WON, game.getState());
        }

        @Test
        @DisplayName("surrounded white queen means BLACK_WON")
        void surroundedWhiteQueenBlackWins() {
            placeAt(white, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));

            HexCoordinate[] neighbours = {
                    new HexCoordinate(0, 1), new HexCoordinate(1, 0),
                    new HexCoordinate(1, -1), new HexCoordinate(0, -1),
                    new HexCoordinate(-1, 0), new HexCoordinate(-1, 1)
            };
            HivePieceType[] types = {
                    HivePieceType.ANT, HivePieceType.ANT, HivePieceType.ANT,
                    HivePieceType.SPIDER, HivePieceType.SPIDER, HivePieceType.GRASSHOPPER
            };

            for (int i = 0; i < 6; i++) {
                Player placer = (i % 2 == 0) ? black : white;
                placeAt(placer, types[i], neighbours[i]);
            }

            game.checkWinCondition();
            assertEquals(HiveGameState.BLACK_WON, game.getState());
        }

        @Test
        @DisplayName("both queens surrounded is DRAW")
        void bothSurroundedIsDraw() {
            // Place both queens adjacent, then surround both
            placeAt(white, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.QUEEN_BEE, new HexCoordinate(1, 0));

            // Shared neighbours of (0,0) and (1,0): (0,1) and (1,-1)
            // Unique to (0,0): (-1,0), (-1,1), (0,-1)
            // Unique to (1,0): (2,0), (1,1), (2,-1)
            // Wait — let me recalculate with the direction offsets:
            // Neighbours of (0,0): (0,1), (1,0), (1,-1), (0,-1), (-1,0), (-1,1)
            // Neighbours of (1,0): (1,1), (2,0), (2,-1), (1,-1), (0,0), (0,1)
            // Shared (excluding the queens themselves): (0,1), (1,-1)
            // Unique to white queen: (-1,0), (-1,1), (0,-1)
            // Unique to black queen: (1,1), (2,0), (2,-1)
            // Need to fill: (0,1), (1,-1), (-1,0), (-1,1), (0,-1), (1,1), (2,0), (2,-1)

            HexCoordinate[] fills = {
                    new HexCoordinate(0, 1), new HexCoordinate(1, -1),
                    new HexCoordinate(-1, 0), new HexCoordinate(-1, 1),
                    new HexCoordinate(0, -1), new HexCoordinate(1, 1),
                    new HexCoordinate(2, 0), new HexCoordinate(2, -1)
            };
            HivePieceType[] types = {
                    HivePieceType.ANT, HivePieceType.ANT, HivePieceType.ANT,
                    HivePieceType.GRASSHOPPER, HivePieceType.GRASSHOPPER, HivePieceType.GRASSHOPPER,
                    HivePieceType.SPIDER, HivePieceType.SPIDER
            };

            for (int i = 0; i < fills.length; i++) {
                Player placer = (i % 2 == 0) ? white : black;
                placeAt(placer, types[i], fills[i]);
            }

            game.checkWinCondition();
            assertEquals(HiveGameState.DRAW, game.getState());
        }

        @Test
        @DisplayName("checkWinCondition does not reset a finished game back to IN_PROGRESS")
        void finishedGameNotReset() {
            placeAt(black, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));
            HexCoordinate[] neighbours = {
                    new HexCoordinate(0, 1), new HexCoordinate(1, 0),
                    new HexCoordinate(1, -1), new HexCoordinate(0, -1),
                    new HexCoordinate(-1, 0), new HexCoordinate(-1, 1)
            };
            HivePieceType[] types = {
                    HivePieceType.ANT, HivePieceType.ANT, HivePieceType.ANT,
                    HivePieceType.SPIDER, HivePieceType.SPIDER, HivePieceType.GRASSHOPPER
            };
            for (int i = 0; i < 6; i++) {
                Player placer = (i % 2 == 0) ? white : black;
                placeAt(placer, types[i], neighbours[i]);
            }

            game.checkWinCondition();
            assertEquals(HiveGameState.WHITE_WON, game.getState());

            // Second call must not change the state even though the board hasn't changed
            game.checkWinCondition();
            assertEquals(HiveGameState.WHITE_WON, game.getState());
        }

        @Test
        @DisplayName("five of six neighbours occupied is not a win")
        void fiveNeighboursNotSurrounded() {
            placeAt(black, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));

            HexCoordinate[] fiveNeighbours = {
                    new HexCoordinate(0, 1), new HexCoordinate(1, 0),
                    new HexCoordinate(1, -1), new HexCoordinate(0, -1),
                    new HexCoordinate(-1, 0)
            };
            HivePieceType[] types = {
                    HivePieceType.ANT, HivePieceType.ANT, HivePieceType.ANT,
                    HivePieceType.SPIDER, HivePieceType.SPIDER
            };

            for (int i = 0; i < 5; i++) {
                Player placer = (i % 2 == 0) ? white : black;
                placeAt(placer, types[i], fiveNeighbours[i]);
            }

            game.checkWinCondition();
            assertEquals(HiveGameState.IN_PROGRESS, game.getState());
        }
    }

    // --- Placement rules ---

    @Nested
    @DisplayName("placement move generation")
    class PlacementTests {

        @Test
        @DisplayName("first move offers origin as only placement coordinate")
        void firstMoveIsOriginOnly() {
            List<PlacePiece> moves = game.getValidPlacementMoves(white);
            List<HexCoordinate> coords = moves.stream().map(PlacePiece::position).distinct().toList();
            assertEquals(1, coords.size());
            assertEquals(new HexCoordinate(0, 0), coords.get(0));
        }

        @Test
        @DisplayName("first move offers all 11 unplaced pieces")
        void firstMoveOffersAllPieces() {
            List<PlacePiece> moves = game.getValidPlacementMoves(white);
            assertEquals(11, moves.size());
        }

        @Test
        @DisplayName("second move allows any of 6 neighbours of first piece")
        void secondMoveAnyNeighbour() {
            placeAt(white, HivePieceType.ANT, new HexCoordinate(0, 0));

            List<PlacePiece> moves = game.getValidPlacementMoves(black);
            List<HexCoordinate> coords = moves.stream().map(PlacePiece::position).distinct().toList();
            assertEquals(6, coords.size());
        }

        @Test
        @DisplayName("third move (white again) only offers positions adjacent to white, not black")
        void thirdMoveOnlyFriendlyAdjacent() {
            placeAt(white, HivePieceType.ANT, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(1, 0));

            List<PlacePiece> moves = game.getValidPlacementMoves(white);
            List<HexCoordinate> coords = moves.stream().map(PlacePiece::position).distinct().toList();

            for (HexCoordinate coord : coords) {
                boolean touchesBlack = coord.getNeighbours().stream()
                        .anyMatch(n -> game.getBoard().getGrid().isCoordinateOccupied(n) &&
                                game.getBoard().getGrid().getPiece(n).getColour() == PlayerColour.BLACK);
                assertFalse(touchesBlack, coord + " should not be adjacent to a black piece");
            }
        }

        @Test
        @DisplayName("already-placed piece is not offered again")
        void placedPieceNotOfferedAgain() {
            HivePiece ant = pieceFromHand(white, HivePieceType.ANT);
            game.makeMove(new PlacePiece(new HexCoordinate(0, 0), ant));

            List<PlacePiece> moves = game.getValidPlacementMoves(white);
            boolean offersPlacedPiece = moves.stream().anyMatch(m -> m.piece() == ant);
            assertFalse(offersPlacedPiece);
        }

        @Test
        @DisplayName("remaining unplaced count decreases after placement")
        void unplacedCountDecreases() {
            int before = game.getValidPlacementMoves(white).stream()
                    .map(PlacePiece::piece).distinct().toList().size();

            placeAt(white, HivePieceType.ANT, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(1, 0));

            int after = game.getValidPlacementMoves(white).stream()
                    .map(PlacePiece::piece).distinct().toList().size();

            assertEquals(before - 1, after);
        }
    }

    // --- Queen-by-turn-4 rule ---
    // turnCount only advances via advanceTurn(), which requires a controller.
    // These tests use a scripted controller to play specific moves through the full turn lifecycle.

    @Nested
    @DisplayName("queen-by-turn-4 enforcement")
    class QueenByFourTests {

        @Test
        @DisplayName("on turn 4 without queen placed, only queen placements offered")
        void turn4ForcesQueen() {
            // We need to build the move list using actual piece references from each player's hand.
            // Create players first to get their hands, then script the moves.
            List<HiveMove> wMoves = new ArrayList<>();
            List<HiveMove> bMoves = new ArrayList<>();
            Player w = new Player(PlayerColour.WHITE, "w", g -> wMoves.remove(0));
            Player b = new Player(PlayerColour.BLACK, "b", g -> bMoves.remove(0));

            List<HivePiece> wAnts = w.getHand().stream().filter(p -> p.getType() == HivePieceType.ANT).toList();
            List<HivePiece> bAnts = b.getHand().stream().filter(p -> p.getType() == HivePieceType.ANT).toList();
            List<HivePiece> wSpiders = w.getHand().stream().filter(p -> p.getType() == HivePieceType.SPIDER).toList();
            List<HivePiece> bSpiders = b.getHand().stream().filter(p -> p.getType() == HivePieceType.SPIDER).toList();

            // Turn 1
            wMoves.add(new PlacePiece(new HexCoordinate(0, 0), wAnts.get(0)));
            bMoves.add(new PlacePiece(new HexCoordinate(1, 0), bAnts.get(0)));
            // Turn 2
            wMoves.add(new PlacePiece(new HexCoordinate(-1, 0), wAnts.get(1)));
            bMoves.add(new PlacePiece(new HexCoordinate(2, 0), bAnts.get(1)));
            // Turn 3
            wMoves.add(new PlacePiece(new HexCoordinate(-2, 0), wSpiders.get(0)));
            bMoves.add(new PlacePiece(new HexCoordinate(3, 0), bSpiders.get(0)));

            HiveGame g = new HiveGame(w, b);
            for (int i = 0; i < 6; i++) {
                g.advanceTurn();
            }

            // Now it's turn 4, white to move, queen not yet placed
            List<PlacePiece> whiteMoves = g.getValidPlacementMoves(w);
            assertFalse(whiteMoves.isEmpty());
            assertTrue(whiteMoves.stream().allMatch(m -> m.piece().getType() == HivePieceType.QUEEN_BEE),
                    "All placement moves on turn 4 must be queen placements");
        }

        @Test
        @DisplayName("on turn 4 with queen already placed, all pieces available")
        void turn4QueenAlreadyPlaced() {
            List<HiveMove> wMoves = new ArrayList<>();
            List<HiveMove> bMoves = new ArrayList<>();
            Player w = new Player(PlayerColour.WHITE, "w", g -> wMoves.remove(0));
            Player b = new Player(PlayerColour.BLACK, "b", g -> bMoves.remove(0));

            HivePiece wQueen = w.getQueenBee();
            List<HivePiece> bAnts = b.getHand().stream().filter(p -> p.getType() == HivePieceType.ANT).toList();
            List<HivePiece> wAnts = w.getHand().stream().filter(p -> p.getType() == HivePieceType.ANT).toList();
            List<HivePiece> bSpiders = b.getHand().stream().filter(p -> p.getType() == HivePieceType.SPIDER).toList();

            // Turn 1: white plays queen early
            wMoves.add(new PlacePiece(new HexCoordinate(0, 0), wQueen));
            bMoves.add(new PlacePiece(new HexCoordinate(1, 0), bAnts.get(0)));
            // Turn 2
            wMoves.add(new PlacePiece(new HexCoordinate(-1, 0), wAnts.get(0)));
            bMoves.add(new PlacePiece(new HexCoordinate(2, 0), bAnts.get(1)));
            // Turn 3
            wMoves.add(new PlacePiece(new HexCoordinate(-2, 0), wAnts.get(1)));
            bMoves.add(new PlacePiece(new HexCoordinate(3, 0), bSpiders.get(0)));

            HiveGame g = new HiveGame(w, b);
            for (int i = 0; i < 6; i++) {
                g.advanceTurn();
            }

            // Turn 4, white queen already placed — should have multiple piece types available
            List<PlacePiece> whiteMoves = g.getValidPlacementMoves(w);
            long distinctTypes = whiteMoves.stream()
                    .map(m -> m.piece().getType()).distinct().count();
            assertTrue(distinctTypes > 1, "Multiple piece types should be available");
        }
    }

    // --- Movement gating ---

    @Nested
    @DisplayName("movement gating on queen placement")
    class MovementGatingTests {

        @Test
        @DisplayName("cannot generate move-moves before own queen is placed")
        void noMovesBeforeQueenPlaced() {
            placeAt(white, HivePieceType.ANT, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(1, 0));

            List<MovePiece> moveMoves = game.getValidMoveMoves(white);
            assertTrue(moveMoves.isEmpty());
        }

        @Test
        @DisplayName("can generate move-moves after own queen is placed")
        void movesAvailableAfterQueenPlaced() {
            placeAt(white, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(1, 0));
            placeAt(white, HivePieceType.ANT, new HexCoordinate(-1, 0));

            List<MovePiece> moveMoves = game.getValidMoveMoves(white);
            assertFalse(moveMoves.isEmpty());
        }

        @Test
        @DisplayName("black cannot move before black queen is placed, even if white queen is placed")
        void blackCannotMoveWithoutBlackQueen() {
            placeAt(white, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(1, 0));

            List<MovePiece> blackMoves = game.getValidMoveMoves(black);
            assertTrue(blackMoves.isEmpty());
        }
    }

    // --- Undo ---

    @Nested
    @DisplayName("undoMove")
    class UndoTests {

        @Test
        @DisplayName("undo on empty history is a no-op")
        void undoEmptyHistoryNoOp() {
            assertDoesNotThrow(() -> game.undoMove());
        }

        @Test
        @DisplayName("undo a placement removes the piece from the board")
        void undoPlacementRemovesPiece() {
            HivePiece queen = pieceFromHand(white, HivePieceType.QUEEN_BEE);
            HexCoordinate coord = new HexCoordinate(0, 0);
            game.executeMove(new PlacePiece(coord, queen));

            game.undoMove();

            assertFalse(game.getBoard().isPiecePlaced(queen));
            assertFalse(game.getBoard().getGrid().isCoordinateOccupied(coord));
            assertEquals(0, game.getMoveHistory().size());
        }

        @Test
        @DisplayName("undo a move restores piece to source position")
        void undoMoveRestoresPosition() {
            HivePiece queen = pieceFromHand(white, HivePieceType.QUEEN_BEE);
            HivePiece ant = pieceFromHand(black, HivePieceType.ANT);
            HexCoordinate origin = new HexCoordinate(0, 0);
            HexCoordinate adjacent = new HexCoordinate(1, 0);
            HexCoordinate dest = new HexCoordinate(1, -1);

            game.executeMove(new PlacePiece(origin, queen));
            game.executeMove(new PlacePiece(adjacent, ant));
            game.executeMove(new MovePiece(origin, dest));

            game.undoMove();

            assertEquals(origin, game.getBoard().getPieceLocations().get(queen));
            assertTrue(game.getBoard().getGrid().isCoordinateOccupied(origin));
            assertFalse(game.getBoard().getGrid().isCoordinateOccupied(dest));
        }

        @Test
        @DisplayName("undo a pass leaves board state unchanged")
        void undoPassLeavesBoardUnchanged() {
            HivePiece queen = pieceFromHand(white, HivePieceType.QUEEN_BEE);
            game.executeMove(new PlacePiece(new HexCoordinate(0, 0), queen));

            game.executeMove(null);
            game.undoMove();

            assertTrue(game.getBoard().isPiecePlaced(queen));
            assertEquals(1, game.getMoveHistory().size());
        }

        @Test
        @DisplayName("undo restores currentPlayer to the player who made the last move")
        void undoRestoresCurrentPlayer() {
            assertEquals(white, game.getCurrentPlayer());
            game.advanceTurn();
            assertEquals(black, game.getCurrentPlayer());

            game.undoMove();

            assertEquals(white, game.getCurrentPlayer());
        }

        @Test
        @DisplayName("undo restores pillbug lock to previous move's destination")
        void undoRestoresPillbugLock() {
            HivePiece queen = pieceFromHand(white, HivePieceType.QUEEN_BEE);
            HivePiece ant = pieceFromHand(black, HivePieceType.ANT);
            HivePiece spider = pieceFromHand(white, HivePieceType.SPIDER);

            HexCoordinate qOrigin = new HexCoordinate(0, 0);
            HexCoordinate aOrigin = new HexCoordinate(1, 0);
            HexCoordinate sOrigin = new HexCoordinate(-1, 0);
            HexCoordinate qDest = new HexCoordinate(1, -1);
            HexCoordinate aDest = new HexCoordinate(0, 0);

            game.executeMove(new PlacePiece(qOrigin, queen));
            game.executeMove(new PlacePiece(aOrigin, ant));
            game.executeMove(new PlacePiece(sOrigin, spider));
            game.executeMove(new MovePiece(qOrigin, qDest));  // locks qDest
            game.executeMove(new MovePiece(aOrigin, aDest));  // locks aDest, unlocks qDest

            game.undoMove();

            assertTrue(game.getBoard().getGrid().isCoordinateLocked(qDest));
            assertFalse(game.getBoard().getGrid().isCoordinateLocked(aDest));
        }
    }

    // --- Draw detection ---

    @Nested
    @DisplayName("draw detection")
    class DrawDetectionTests {

        @Test
        @DisplayName("fresh game is not a draw")
        void freshGameNotDraw() {
            assertFalse(game.checkForDraw());
        }

        @Test
        @DisplayName("same board state once is not a draw")
        void sameStateOnceNotDraw() {
            game.executeMove(null);
            assertFalse(game.checkForDraw());
        }

        @Test
        @DisplayName("same board state twice is not a draw")
        void sameStateTwiceNotDraw() {
            game.executeMove(null);
            game.executeMove(null);
            assertFalse(game.checkForDraw());
        }

        @Test
        @DisplayName("same board state three times is a draw")
        void sameStateThreeTimesDraw() {
            game.executeMove(null);
            game.executeMove(null);
            game.executeMove(null);
            assertTrue(game.checkForDraw());
        }

        @Test
        @DisplayName("undo decrements state frequency — no false draw after undo and replay")
        void undoDecrementsFrequency() {
            game.executeMove(null);
            game.executeMove(null); // seen twice
            game.undoMove();        // back to once
            game.executeMove(null); // back to twice — not a draw yet
            assertFalse(game.checkForDraw());
        }
    }

    // --- Combined valid moves ---

    @Nested
    @DisplayName("getValidMovesForPlayer")
    class CombinedMovesTests {

        @Test
        @DisplayName("first turn has only placement moves, no move-moves")
        void firstTurnOnlyPlacements() {
            List<HiveMove> moves = game.getValidMovesForPlayer(white);
            assertTrue(moves.stream().allMatch(m -> m instanceof PlacePiece));
        }

        @Test
        @DisplayName("after queen placed with pieces on board, both place and move are available")
        void bothPlaceAndMoveAvailable() {
            placeAt(white, HivePieceType.QUEEN_BEE, new HexCoordinate(0, 0));
            placeAt(black, HivePieceType.QUEEN_BEE, new HexCoordinate(1, 0));
            placeAt(white, HivePieceType.ANT, new HexCoordinate(-1, 0));
            placeAt(black, HivePieceType.ANT, new HexCoordinate(2, 0));

            List<HiveMove> moves = game.getValidMovesForPlayer(white);
            boolean hasPlacements = moves.stream().anyMatch(m -> m instanceof PlacePiece);
            boolean hasMovements = moves.stream().anyMatch(m -> m instanceof MovePiece);

            assertTrue(hasPlacements);
            assertTrue(hasMovements);
        }
    }
}
