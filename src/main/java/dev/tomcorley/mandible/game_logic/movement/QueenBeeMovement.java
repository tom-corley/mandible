package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class QueenBeeMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // TODO: Implement queen bee movement logic
        return moves;
    }
}
