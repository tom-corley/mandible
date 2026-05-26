package dev.tomcorley.mandible.engine.movement;

import java.util.List;

import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;

public interface PieceMovementStrategy {
    List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid);
}
