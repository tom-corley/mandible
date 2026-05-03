package dev.tomcorley.mandible.game_logic;

public class Player {
    private final PlayerColour colour;
    private final String username;
    private final PlayerController controller;

    public Player(PlayerColour colour, String username, PlayerController controller) {
        this.colour = colour;
        this.username = username;
        this.controller = controller;
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
}