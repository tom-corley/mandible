package dev.tomcorley.mandible.engine.movement;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;
import dev.tomcorley.mandible.engine.HivePieceType;

public class MosquitoMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // If mosquito is stacked, it must be a beetle
        if (grid.getStackHeight(coordinate) > 1) {
            PieceMovementStrategy beetleMovement = HivePieceType.BEETLE.getMovementStrategy();
            moves.addAll(beetleMovement.getValidMoves(coordinate, grid));
            return moves;
        }

        // Otherwise, can move like a queen bee or any of its neigbouring pieces
        moves.addAll(HiveMovementUtils.slideAlongOneEdge(coordinate, grid));
        for (HexCoordinate neighbour : coordinate.getNeighbours()) {
            if (grid.isCoordinateOccupied(neighbour)) {
                // Edge case for neigbouring mosquito
                if (grid.getStack(neighbour).peek().getType() == HivePieceType.MOSQUITO) {
                    continue;
                }

                // Get moves using the strategy of the neigbouring piece
                PieceMovementStrategy neighbourStrategy = grid.getStack(neighbour).peek().getType().getMovementStrategy();
                List<MovePiece> movesAsNeigbourType = neighbourStrategy.getValidMoves(coordinate, grid);
                moves.addAll(movesAsNeigbourType);
            }
        }

        // It is possible the same strategy is used multiple times
        // Or multiple strategies yield the same move, so we need to deduplicate
        moves = moves.stream().distinct().collect(Collectors.toList());

        return moves;
    }
}