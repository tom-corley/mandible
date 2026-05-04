package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class QueenBeeMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<HexCoordinate> destinations = HiveMovementUtils.slideAlongOneEdge(coordinate, grid);

        List<MovePiece> moves = destinations.stream()
            .map(move -> new MovePiece(coordinate, move))
            .collect(Collectors.toList());

        return moves;
    }
}
