package dev.tomcorley.mandible.game_logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    @DisplayName("player has the assigned colour")
    void hasColour() {
        Player player = new Player(PlayerColour.WHITE, "Alice", new BotController());
        assertEquals(PlayerColour.WHITE, player.getColour());
    }

    @Test
    @DisplayName("player has the assigned username")
    void hasUsername() {
        Player player = new Player(PlayerColour.BLACK, "Bob", new BotController());
        assertEquals("Bob", player.getUsername());
    }

    @Test
    @DisplayName("player has the assigned controller")
    void hasController() {
        BotController controller = new BotController();
        Player player = new Player(PlayerColour.WHITE, "Alice", controller);
        assertSame(controller, player.getController());
    }

    @Test
    @DisplayName("player hand is populated on construction")
    void handPopulated() {
        Player player = new Player(PlayerColour.WHITE, "Alice", new BotController());
        assertNotNull(player.getHand());
        assertFalse(player.getHand().isEmpty());
    }

    @Test
    @DisplayName("hand pieces match player colour")
    void handMatchesColour() {
        Player player = new Player(PlayerColour.BLACK, "Bob", new BotController());
        assertTrue(player.getHand().stream().allMatch(p -> p.getColour() == PlayerColour.BLACK));
    }

    @Test
    @DisplayName("getQueenBee returns the queen bee from the hand")
    void getQueenBee() {
        Player player = new Player(PlayerColour.WHITE, "Alice", new BotController());
        HivePiece queen = player.getQueenBee();

        assertNotNull(queen);
        assertEquals(HivePieceType.QUEEN_BEE, queen.getType());
        assertEquals(PlayerColour.WHITE, queen.getColour());
    }

    @Test
    @DisplayName("getQueenBee returns the same instance from the hand")
    void queenBeeIsSameInstance() {
        Player player = new Player(PlayerColour.WHITE, "Alice", new BotController());
        HivePiece queen = player.getQueenBee();

        assertTrue(player.getHand().contains(queen));
        assertSame(queen, player.getQueenBee());
    }

    @Test
    @DisplayName("two players have independent hands")
    void independentHands() {
        Player white = new Player(PlayerColour.WHITE, "w", new BotController());
        Player black = new Player(PlayerColour.BLACK, "b", new BotController());

        assertNotSame(white.getHand(), black.getHand());
        assertNotSame(white.getQueenBee(), black.getQueenBee());
    }

    @Test
    @DisplayName("constructor with custom hand uses that hand")
    void customHandConstructor() {
        List<HivePiece> expandedHand = HandFactory.createExpandedHand(PlayerColour.WHITE);
        Player player = new Player(PlayerColour.WHITE, "Alice", new BotController(), expandedHand);

        assertEquals(expandedHand, player.getHand());
        assertEquals(14, player.getHand().size());
    }

    @Test
    @DisplayName("default constructor uses standard hand")
    void defaultConstructorUsesStandardHand() {
        Player player = new Player(PlayerColour.WHITE, "Alice", new BotController());
        assertEquals(11, player.getHand().size());
    }
}
