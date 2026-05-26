package dev.tomcorley.mandible.engine;

import dev.tomcorley.mandible.engine.movement.*;

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

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
