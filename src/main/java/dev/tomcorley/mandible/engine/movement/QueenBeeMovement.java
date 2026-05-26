package dev.tomcorley.mandible.engine.movement;

import java.util.List;

import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;

public class QueenBeeMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        return HiveMovementUtils.slideAlongOneEdge(coordinate, grid);
    }
}
