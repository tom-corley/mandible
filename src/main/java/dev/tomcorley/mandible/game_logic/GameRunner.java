package dev.tomcorley.mandible.game_logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRunner {
    private static final Logger log = LoggerFactory.getLogger(GameRunner.class);

    public void runGame(HiveGame game) {
        while (game.getState() == HiveGameState.IN_PROGRESS) {
            game.advanceTurn();
            game.checkWinCondition();
        }

        log.info("Game finished: {}", game.getState());
    }
}
