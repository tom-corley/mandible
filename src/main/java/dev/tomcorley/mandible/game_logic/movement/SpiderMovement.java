package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class SpiderMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        HiveGrid gridCopy = new HiveGrid(grid);
        gridCopy.removePiece(coordinate);

        // We need to construct valid paths of length 3, starting from coordinate, moving along one edge at a time
        // TODO: implement length 3 path construction logic


        return moves;
    }
}
