package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public interface PieceMovementStrategy {
    List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid);
}
