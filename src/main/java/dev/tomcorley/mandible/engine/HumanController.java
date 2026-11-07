package dev.tomcorley.mandible.engine;

public class HumanController implements PlayerController {
    @Override
    public HiveMove chooseMove(HiveGame game) {
        return new PassMove();
        // Illustrative example
    }
}
