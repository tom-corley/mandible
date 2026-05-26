package dev.tomcorley.mandible.engine;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotController implements PlayerController {
    private static final Logger log = LoggerFactory.getLogger(BotController.class);
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
        log.debug("Valid moves: {}", validMoves.size());

        if (validMoves.isEmpty()) {
            log.debug("No valid moves, skipping turn");
            return null;
        }

        int randomIndex = random.nextInt(validMoves.size());
        HiveMove chosenMove = validMoves.get(randomIndex);
        log.debug("Chosen move: {}", chosenMove);
        return chosenMove;
    }
}
