package dev.tomcorley.mandible.engine;

public class GameFactory {
    public static HiveGame createStandardBotVsBotGame() {
        Player whitePlayer = new Player(PlayerColour.WHITE, "White Bot", new BotController());
        Player blackPlayer = new Player(PlayerColour.BLACK, "Black Bot", new BotController());
        return new HiveGame(whitePlayer, blackPlayer);
    }

    public static HiveGame createExpandedBotVsBotGame() {
        Player whitePlayer = new Player(PlayerColour.WHITE, "White Bot", new BotController(),
                HandFactory.createExpandedHand(PlayerColour.WHITE));
        Player blackPlayer = new Player(PlayerColour.BLACK, "Black Bot", new BotController(),
                HandFactory.createExpandedHand(PlayerColour.BLACK));
        return new HiveGame(whitePlayer, blackPlayer);
    }
}
