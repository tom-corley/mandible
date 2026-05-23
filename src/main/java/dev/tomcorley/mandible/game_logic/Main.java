package dev.tomcorley.mandible.game_logic;

public class Main {
    public static void main(String[] args) {
        HiveGame game = GameFactory.createStandardBotVsBotGame();
        GameRunner runner = new GameRunner();
        runner.runGame(game);
        System.out.println(game.getState());
    }
}