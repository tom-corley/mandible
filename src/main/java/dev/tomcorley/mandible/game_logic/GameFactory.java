package dev.tomcorley.mandible.game_logic;

public class GameFactory {
    public static HiveGame createStandardBotVsBotGame() {
        Player whitePlayer = new Player(PlayerColour.WHITE, "White Bot", new BotController());
        Player blackPlayer = new Player(PlayerColour.BLACK, "Black Bot", new BotController());
        return new HiveGame(whitePlayer, blackPlayer);
    }
}
