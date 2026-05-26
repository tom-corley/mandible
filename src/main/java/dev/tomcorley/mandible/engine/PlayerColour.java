package dev.tomcorley.mandible.engine;

public enum PlayerColour {
    WHITE,
    BLACK,
    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}