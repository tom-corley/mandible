package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
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

        // Get list of neigbouring coordinates
        List<HexCoordinate> neighbours = coordinate.getNeighbours();

        // Get list of empty neighbouring coordinates
        List<HexCoordinate> emptyNeighbours = neighbours.stream()
            .filter(neighbour -> !grid.getGrid().containsKey(neighbour))
            .collect(Collectors.toList());

        // Get list of movable neighbouring coordinates
        // Meaning, occupied by one unstacked piece that is movable
        List<HexCoordinate> movableNeighbours = neighbours.stream()
            .filter(neighbour -> grid.getGrid().containsKey(neighbour))
            .filter(neighbour -> grid.getGrid().get(neighbour).size() == 1)
            .filter(neighbour -> grid.isPieceMovable(neighbour))
            .collect(Collectors.toList());

        // TODO: freedom of movement — gate check both the "up onto pillbug" and "down to empty space" steps

        // Construct pairs of pieces to move and empty neighbouring coordinates
        for (HexCoordinate movableNeighbour : movableNeighbours) {
            for (HexCoordinate emptyNeighbour : emptyNeighbours) {
                moves.add(new MovePiece(movableNeighbour, emptyNeighbour));
            }
        }

        return moves;
    }
}
