package dev.tomcorley.mandible.game_logic;

public class InvalidMoveException extends HiveException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
