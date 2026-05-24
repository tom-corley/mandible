package dev.tomcorley.mandible.game_logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class GameFactoryTest {

    @Test
    @DisplayName("creates a game with white and black players")
    void createsGameWithBothPlayers() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();

        assertNotNull(game.getWhitePlayer());
        assertNotNull(game.getBlackPlayer());
    }

    @Test
    @DisplayName("white player has WHITE colour")
    void whitePlayerIsWhite() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertEquals(PlayerColour.WHITE, game.getWhitePlayer().getColour());
    }

    @Test
    @DisplayName("black player has BLACK colour")
    void blackPlayerIsBlack() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertEquals(PlayerColour.BLACK, game.getBlackPlayer().getColour());
    }

    @Test
    @DisplayName("white moves first")
    void whiteMovesFirst() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertSame(game.getWhitePlayer(), game.getCurrentPlayer());
    }

    @Test
    @DisplayName("game starts IN_PROGRESS")
    void gameStartsInProgress() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertEquals(HiveGameState.IN_PROGRESS, game.getState());
    }

    @Test
    @DisplayName("board starts empty")
    void boardStartsEmpty() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertTrue(game.getBoard().getPieceLocations().isEmpty());
    }

    @Test
    @DisplayName("both players have standard hand of 11 pieces")
    void standardHandSize() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertEquals(11, game.getWhitePlayer().getHand().size());
        assertEquals(11, game.getBlackPlayer().getHand().size());
    }

    @Test
    @DisplayName("each player has exactly one queen bee")
    void oneQueenEach() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();

        long whiteQueens = game.getWhitePlayer().getHand().stream()
                .filter(p -> p.getType() == HivePieceType.QUEEN_BEE)
                .count();
        long blackQueens = game.getBlackPlayer().getHand().stream()
                .filter(p -> p.getType() == HivePieceType.QUEEN_BEE)
                .count();

        assertEquals(1, whiteQueens);
        assertEquals(1, blackQueens);
    }

    @Test
    @DisplayName("each player has 3 ants, 3 grasshoppers, 2 spiders, 2 beetles")
    void correctPieceDistribution() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();

        for (Player player : new Player[]{game.getWhitePlayer(), game.getBlackPlayer()}) {
            var hand = player.getHand();
            assertEquals(3, hand.stream().filter(p -> p.getType() == HivePieceType.ANT).count());
            assertEquals(3, hand.stream().filter(p -> p.getType() == HivePieceType.GRASSHOPPER).count());
            assertEquals(2, hand.stream().filter(p -> p.getType() == HivePieceType.SPIDER).count());
            assertEquals(2, hand.stream().filter(p -> p.getType() == HivePieceType.BEETLE).count());
        }
    }

    @Test
    @DisplayName("both players have BotController")
    void bothPlayersHaveBotController() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertInstanceOf(BotController.class, game.getWhitePlayer().getController());
        assertInstanceOf(BotController.class, game.getBlackPlayer().getController());
    }

    @Test
    @DisplayName("white and black players are different instances")
    void playersAreDifferentInstances() {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        assertNotSame(game.getWhitePlayer(), game.getBlackPlayer());
    }

    @Test
    @DisplayName("each call creates a fresh independent game")
    void freshGameEachCall() {
        HiveGame game1 = GameFactory.createStandardBotVsBotGame();
        HiveGame game2 = GameFactory.createStandardBotVsBotGame();
        assertNotSame(game1, game2);
        assertNotSame(game1.getBoard(), game2.getBoard());
    }
}
