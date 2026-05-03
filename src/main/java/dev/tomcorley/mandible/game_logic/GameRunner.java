package dev.tomcorley.mandible.game_logic;

public class GameRunner {
    public void runGame(HiveGame game) {
        while (game.getState() == HiveGameState.IN_PROGRESS) {
            game.advanceTurn();
            game.checkWinCondition();
        }

        if (game.getState() == HiveGameState.WHITE_WON) {
            System.out.println("White won!");
        } else if (game.getState() == HiveGameState.BLACK_WON) {
            System.out.println("Black won!");
        } else {
            System.out.println("Draw!");
        }
    }
}
