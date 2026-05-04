package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class BeetleMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();
        boolean hasClimbed = grid.getGrid().get(coordinate).size() > 1;

        // If the beetle has not climbed, it can edge move or climb up
        if (!hasClimbed) {
            moves.addAll(HiveMovementUtils.slideAlongOneEdge(coordinate, grid));
            moves.addAll(getValidClimbUpMoves(coordinate, grid));
        }

        // If the beetle has climbed, it can climb across, climb up or climb down
        else {
            // TODO: Implement beetle climb across, up or down movement logic
        }

        return moves;
    }

    public List<MovePiece> getValidClimbUpMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();
        
        // Can climb on to any occupied neighbouring space
        for (HexCoordinate neighbour : coordinate.getNeighbours()) {
            if (grid.getGrid().containsKey(neighbour)) {
                moves.add(new MovePiece(coordinate, neighbour));
            }
        }

        return moves;
    }
}
