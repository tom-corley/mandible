package dev.tomcorley.mandible.game_logic;

public enum PlayerColour {
    WHITE,
    BLACK,
    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}