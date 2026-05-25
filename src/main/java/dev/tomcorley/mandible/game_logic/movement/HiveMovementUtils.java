package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;

import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.HexDirection;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class HiveMovementUtils {
    public static List<MovePiece> slideAlongOneEdge(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        HexDirection[] orderedDirections = HexDirection.values();
        for (int i = 0; i < orderedDirections.length; i++) {
            HexDirection direction = orderedDirections[i];
            HexCoordinate neighbour = coordinate.add(direction);

            // If the neigbouring space is occupied, cannot slide there
            if (grid.isCoordinateOccupied(neighbour)) {
                continue;
            }
    
            // If it is free, check if we can slide along one edge
            if (!canSlideAlongOneEdge(coordinate, i, grid)) {
                continue;
            }

            // If we can slide along one edge, add the destination
            moves.add(new MovePiece(coordinate, neighbour));
        }

        return moves;
    }

    public static boolean canSlideAlongOneEdge(HexCoordinate coordinate, int directionIndex, HiveGrid grid) {
        HexDirection[] orderedDirections = HexDirection.values();
        int occupancyCount = 0;

        // Count occupancy of anti-clockwise and clockwise neighbours of target space
        HexDirection previousDirection = orderedDirections[Math.floorMod(directionIndex - 1, orderedDirections.length)];
        HexCoordinate previousNeighbour = coordinate.add(previousDirection);
        if (grid.isCoordinateOccupied(previousNeighbour)) {
            occupancyCount++;
        }

        HexDirection nextDirection = orderedDirections[Math.floorMod(directionIndex + 1, orderedDirections.length)];
        HexCoordinate nextNeighbour = coordinate.add(nextDirection);
        if (grid.isCoordinateOccupied(nextNeighbour)) {
            occupancyCount++;
        }

        // If no piece to slide past, return false
        if (occupancyCount == 0) {
            return false;
        }

        // If two pieces form a gate, return false
        if (occupancyCount == 2) {
            return false;
        }

        // One piece to slide past, return true
        return true;
    }
}
