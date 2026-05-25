package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HexDirection;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

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
