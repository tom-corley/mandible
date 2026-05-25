package dev.tomcorley.mandible.game_logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        GameRunner runner = new GameRunner();
        runner.runGame(game);
        log.info("Final state: {}", game.getState());
    }
}