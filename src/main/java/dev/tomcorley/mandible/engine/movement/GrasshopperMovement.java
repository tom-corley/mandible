package dev.tomcorley.mandible.engine.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HexDirection;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;

public class GrasshopperMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Iterate over neighbouring directions
        for (HexDirection direction : HexDirection.values()) {
            HexCoordinate neighbour = coordinate.add(direction);
            // If there is no neighbour in that direction, skip
            if (!grid.isCoordinateOccupied(neighbour)) {
                continue;
            }
            
            // Continue moving in that direction until we hit a free space
            HexCoordinate destination = neighbour.add(direction);
            while (grid.isCoordinateOccupied(destination)) {
                destination = destination.add(direction);
            }

            moves.add(new MovePiece(coordinate, destination));
        }
        
        return moves;
    }
}
