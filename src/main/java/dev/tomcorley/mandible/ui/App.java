package dev.tomcorley.mandible.ui;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.tomcorley.mandible.game_logic.GameFactory;
import dev.tomcorley.mandible.game_logic.HiveGame;
import dev.tomcorley.mandible.game_logic.HiveGameState;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final int TURN_DELAY_MS = 10;

    public static void main(String[] args) {
        HiveGame game = GameFactory.createExpandedBotVsBotGame();

        BoardPanel panel = new BoardPanel(game);
        JFrame frame = new JFrame("Mandible");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Thread gameThread = new Thread(() -> {
            while (game.getState() == HiveGameState.IN_PROGRESS) {
                game.advanceTurn();
                game.checkWinCondition();
                panel.update();
                try {
                    Thread.sleep(TURN_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            log.info("Game finished: {}", game.getState());
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
