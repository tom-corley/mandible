package dev.tomcorley.mandible.game_logic;

import java.util.List;

public class Player {
    private final PlayerColour colour;
    private final String username;
    private final PlayerController controller;
    private final List<HivePiece> hand;

    public Player(PlayerColour colour, String username, PlayerController controller) {
        this.colour = colour;
        this.username = username;
        this.controller = controller;
        this.hand = HandFactory.createStandardHand(colour);
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
        return hand;
    }
}