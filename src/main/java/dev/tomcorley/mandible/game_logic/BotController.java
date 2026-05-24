package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.Random;

public class BotController implements PlayerController {
    private final Random random;

    public BotController() {
        this.random = new Random();
    }

    public BotController(Random random) {
        this.random = random;
    }

    @Override
    public HiveMove chooseMove(HiveGame game) {
        // Get current player
        Player player = game.getCurrentPlayer();

        // Get valid moves for player
        List<HiveMove> validMoves = game.getValidMovesForPlayer(player);

        // Maybe for now we just choose a random integer between 0 and the number of valid moves
        System.out.println("Number of valid moves: " + validMoves.size());

        if (validMoves.isEmpty()) {
            System.out.println("No valid moves found");
            return null;
        }

        int randomIndex = random.nextInt(validMoves.size());
        HiveMove chosenMove = validMoves.get(randomIndex);
        System.out.println("Chosen move: " + chosenMove);
        return chosenMove;
    }
}
