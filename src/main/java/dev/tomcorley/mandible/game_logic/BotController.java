package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.Random;

public class BotController implements PlayerController {
    @Override
    public HiveMove chooseMove(HiveGame game) {
        // Get current player
        Player player = game.getCurrentPlayer();

        // Get valid moves for player
        List<HiveMove> validMoves = game.getValidMovesForPlayer(player);

        // Maybe for now we just choose a random integer between 0 and the number of valid moves
        System.out.println("Number of valid moves: " + validMoves.size());

        if (validMoves.size() == 0) {
            System.out.println("No valid moves found");
            return null;
        }

        int randomIndex = new Random().nextInt(validMoves.size());
        HiveMove chosenMove = validMoves.get(randomIndex);
        System.out.println("Chosen move: " + chosenMove);
        return chosenMove;
    }
}
