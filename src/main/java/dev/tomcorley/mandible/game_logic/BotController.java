package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.Random;

public class BotController implements PlayerController {
    private final Player player;

    public BotController(Player player) {
        this.player = player;
    }

    @Override
    public HiveMove chooseMove(HiveGame game) {

        // Get valid moves for player
        List<HiveMove> validMoves = game.getValidMovesForPlayer(player);

        // Maybe for now we just choose a random integer between 0 and the number of valid moves
        int randomIndex = new Random().nextInt(validMoves.size());
        return validMoves.get(randomIndex);
    }
}
