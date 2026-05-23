package dev.tomcorley.mandible.ui;

import dev.tomcorley.mandible.game_logic.*;
import javax.swing.*;

public class App {

    private static final int TURN_DELAY_MS = 1000;

    public static void main(String[] args) {
        HiveGame game = GameFactory.createStandardBotVsBotGame();

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
            System.out.println(game.getState());
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
