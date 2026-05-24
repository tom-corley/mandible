package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.Collections;

public class Player {
    private final PlayerColour colour;
    private final String username;
    private final PlayerController controller;
    private final List<HivePiece> hand;

    public Player(PlayerColour colour, String username, PlayerController controller) {
        this(colour, username, controller, HandFactory.createStandardHand(colour));
    }

    public Player(PlayerColour colour, String username, PlayerController controller, List<HivePiece> hand) {
        this.colour = colour;
        this.username = username;
        this.controller = controller;
        this.hand = hand;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public String getUsername() {
        return username;
    }

    public PlayerController getController() {
        return controller;
    }

    public List<HivePiece> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public HivePiece getQueenBee() {
        return hand.stream()
            .filter(piece -> piece.getType() == HivePieceType.QUEEN_BEE)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Queen bee not found in hand"));
    }
}