package dev.tomcorley.mandible.game_logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;

public class HiveGrid {
    private final Map<HexCoordinate, Deque<HivePiece>> grid;

    public HiveGrid() {
        this.grid = new HashMap<>();
    }

    public Map<HexCoordinate, Deque<HivePiece>> getGrid() {
        return grid;
    }

    public void placePiece(HexCoordinate coordinate, HivePiece piece) {
        // Validate that the coordinate is free
        if (grid.containsKey(coordinate)) {
            throw new IllegalArgumentException("Coordinate already occupied");
        }

        Deque<HivePiece> stack = new ArrayDeque<>();
        stack.push(piece);

        grid.put(coordinate, stack);
    }

    public boolean isPieceMovable(HexCoordinate coordinate) {
        if (grid.keySet().size() == 1) {
            return false;
        }

        // Form a set of all other occupied coordinates
        Set<HexCoordinate> occupiedCoordinates = new HashSet<>(grid.keySet());
        occupiedCoordinates.remove(coordinate);

        // Set up bfs search queue
        Queue<HexCoordinate> queue = new ArrayDeque<>();
        HexCoordinate first = occupiedCoordinates.iterator().next();
        queue.add(first);

        // bfs through the set, removing visited coordinates
        while (!queue.isEmpty()) {
            // Pick from front of queue and remove from set
            HexCoordinate current = queue.poll();
            occupiedCoordinates.remove(current);

            // Add all unvisited neighbours to the queue
            for (HexCoordinate neighbour : current.getNeighbours()) {
                if (
                    occupiedCoordinates.contains(neighbour) && 
                    !queue.contains(neighbour) &&
                    !current.equals(neighbour)
                ) {
                    queue.add(neighbour);
                }
            }
        }

        // The piece is movable iff the board without that piece is connected
        // iff the flood fill traveses all other pieces
        return occupiedCoordinates.isEmpty();
    }
}