package dev.tomcorley.mandible.game_logic;

import dev.tomcorley.mandible.game_logic.movement.*;

public enum HivePieceType {
    QUEEN_BEE(new QueenBeeMovement()),
    LADYBUG(new LadybugMovement()),
    GRASSHOPPER(new GrasshopperMovement()),
    SPIDER(new SpiderMovement()),
    ANT(new AntMovement()),
    BEETLE(new BeetleMovement()),
    PILLBUG(new PillbugMovement()),
    MOSQUITO(new MosquitoMovement()),
    ;

    private final PieceMovementStrategy movementStrategy;

    HivePieceType(PieceMovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }

    public PieceMovementStrategy getMovementStrategy() {
        return movementStrategy;
    }
}
