package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HexDirection;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class PillbugMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Can move like a queen bee
        moves.addAll(HiveMovementUtils.slideAlongOneEdge(coordinate, grid));

        // Or can move neigbouring pieces to empty neighbouring spaces
        moves.addAll(getValidPillbugNeighbourMoves(coordinate, grid));

        return moves;
    }

    public List<MovePiece> getValidPillbugNeighbourMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Get list of directions
        List<HexDirection> directions = List.of(HexDirection.values());
        
        // Get list of throwable neighbours
        List<HexCoordinate> throwableNeighbours = directions.stream()
            .filter(direction -> !grid.gateCheckAtHeight(coordinate, direction, 2))
            .map(direction -> coordinate.add(direction))
            .filter(neighbour -> grid.getStackHeight(neighbour) == 1)
            .filter(neighbour -> grid.isPieceMovable(neighbour))
            .collect(Collectors.toList());

        // Get list of valid throw destinations
        List<HexCoordinate> throwDestinations = directions.stream()
            .filter(direction -> !grid.gateCheckAtHeight(coordinate, direction, 2))
            .map(direction -> coordinate.add(direction))
            .filter(destination -> grid.getStackHeight(destination) == 0)
            .collect(Collectors.toList());

        // Construct pairs of pieces to move and empty neighbouring coordinates
        for (HexCoordinate throwableNeighbour : throwableNeighbours) {
            for (HexCoordinate throwDestination : throwDestinations) {
                moves.add(new MovePiece(throwableNeighbour, throwDestination));
            }
        }

        return moves;
    }
}
