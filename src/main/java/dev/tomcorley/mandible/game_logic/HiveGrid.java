package dev.tomcorley.mandible.game_logic;

import dev.tomcorley.mandible.game_logic.movement.PieceMovementStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Collections;

public class HiveGrid {
    private final Map<HexCoordinate, Deque<HivePiece>> grid;

    public HiveGrid() {
        this.grid = new HashMap<>();
    }

    public HiveGrid(HiveGrid other) {
        this.grid = new HashMap<>();
        for (Map.Entry<HexCoordinate, Deque<HivePiece>> entry : other.getGrid().entrySet()) {
            // Use the same coordinate, mapped to a copy of the stack, referencing the same pieces
            HexCoordinate coordinate = entry.getKey();
            Deque<HivePiece> stack = new ArrayDeque<>(entry.getValue());
            this.grid.put(coordinate, stack);
        }
    }

    public int getStackHeight(HexCoordinate coordinate) {
        if (!grid.containsKey(coordinate)) {
            return 0;
        }

        return grid.get(coordinate).size();
    }

    public boolean isCoordinateOccupied(HexCoordinate coordinate) {
        return grid.containsKey(coordinate);
    }

    public boolean gateCheck(HexCoordinate coordinate, HexDirection direction) {
        int gateHeight = grid.get(coordinate).size();
        HexCoordinate anticlockwiseNeighbour = coordinate.add(direction.antiClockwise());
        HexCoordinate clockwiseNeighbour = coordinate.add(direction.clockwise());
        boolean antiClockwiseGated = isCoordinateOccupied(anticlockwiseNeighbour) && getStackHeight(anticlockwiseNeighbour) >= gateHeight && getStackHeight(clockwiseNeighbour) >= gateHeight;
        boolean clockwiseGated = isCoordinateOccupied(clockwiseNeighbour) && getStackHeight(clockwiseNeighbour) >= gateHeight && getStackHeight(anticlockwiseNeighbour) >= gateHeight;
            
        return antiClockwiseGated && clockwiseGated;
    }

    public Map<HexCoordinate, Deque<HivePiece>> getGrid() {
        return Collections.unmodifiableMap(grid);
    }

    public void placePiece(PlacePiece move) {
        HexCoordinate coordinate = move.position();
        HivePiece piece = move.piece();

        // Validate that the coordinate is free
        if (grid.containsKey(coordinate)) {
            throw new IllegalArgumentException("Coordinate already occupied");
        }

        Deque<HivePiece> stack = new ArrayDeque<>();
        stack.push(piece);

        grid.put(coordinate, stack);
    }

    public void removePiece(RemovePiece move) {
        HexCoordinate coordinate = move.position();

        if (!grid.containsKey(coordinate)) {
            throw new IllegalArgumentException("Coordinate not occupied");
        }

        grid.get(coordinate).pop();
        if (grid.get(coordinate).isEmpty()) {
            grid.remove(coordinate);
        }
    }

    public void removePiece(HexCoordinate coordinate) {
        // TODO: deprecate this and use above
        if (!grid.containsKey(coordinate)) {
            throw new IllegalArgumentException("Coordinate not occupied");
        }

        grid.get(coordinate).pop();
        if (grid.get(coordinate).isEmpty()) {
            grid.remove(coordinate);
        }
    }

    public HivePiece getPiece(HexCoordinate coordinate) {
        return grid.get(coordinate).peek();
    }

    public boolean isValidMove(MovePiece move) {
        HexCoordinate from = move.from();

        // Validate piece to move exists
        if (!grid.containsKey(from)) {
            System.out.println("From coordinate not occupied: " + from);
            return false;
        }

        return true;
    }

    public void movePiece(MovePiece move) {
        HexCoordinate from = move.from();
        HexCoordinate to = move.to();

        // Validate move
        if (!isValidMove(move)) {
            System.out.println("Validation failed at some point for move: " + move);
            throw new IllegalArgumentException("Invalid move");
        }

        // Pop piece to move from stack
        HivePiece piece = grid.get(from).pop();
        if (grid.get(from).isEmpty()) {
            grid.remove(from);
        }

        // Push piece to new or existing stack
        if (grid.containsKey(to)) {
            grid.get(to).push(piece);
        } else {
            Deque<HivePiece> stack = new ArrayDeque<>();
            stack.push(piece);
            grid.put(to, stack);
        }
    }

    public boolean isClimbUp(HexCoordinate from, HexCoordinate to) {
        // Both have height zero
        if (!grid.containsKey(from) && !grid.containsKey(to)) {
            return true;
        }

        // Current has height zero, so must be climb up
        if (!grid.containsKey(from)) {
            return true;
        }

        // Other having height zero so is climb down or across
        if (!grid.containsKey(to)) {
            return false;
        }

        // Equal size means destination would get taller, so is climb up
        return grid.get(from).size() <= grid.get(to).size();
    }

    public boolean isClimbDown(HexCoordinate from, HexCoordinate to) {
        // Both have height zero
        if (!grid.containsKey(from) && !grid.containsKey(to)) {
            return false;
        }

        // Current has height zero, so is climb up
        if (!grid.containsKey(from)) {
            return false;
        }
        
        // If the destination has height zero, it is a climb down if the current has height two or more
        if (!grid.containsKey(to) && grid.get(from).size() >= 2) {
            return true;
        }

        // Otherwise it is a climb across
        if (!grid.containsKey(to)) {
            return false;
        }

        // In general, it is a climb down if the current has height greater than the destination plus one
        return grid.get(from).size() > grid.get(to).size() + 1;
    }

    public boolean isClimbAcross(HexCoordinate from, HexCoordinate to) {
        // If both have height zero, it is a climb up
        if (!grid.containsKey(from) && !grid.containsKey(to)) {
            return false;
        }

        // If current has height zero but not destination, it is a climb up
        if (!grid.containsKey(from)) {
            return false;
        }

        // If destination has height zero but not current, it is a climb across if current has height 1
        if (!grid.containsKey(to) && grid.get(from).size() == 1) {
            return true;
        }

        // Otherwise it is a climb down
        if (!grid.containsKey(to)) {
            return false;
        }

        // Otherwise it is a climb across if the current has height equal to the destination plus one
        return grid.get(from).size() == grid.get(to).size() + 1;
    }

    public boolean isPieceMovable(HexCoordinate coordinate) {
        if (grid.keySet().size() == 1) {
            return false;
        }

        // If the coordinate has a stack with more than one piece, it is movable
        if (grid.get(coordinate).size() > 1) {
            return true;
        }

        // Form a set of all other occupied coordinates
        Set<HexCoordinate> occupiedCoordinates = new HashSet<>(grid.keySet());
        occupiedCoordinates.remove(coordinate);

        // Set up bfs search queue
        Queue<HexCoordinate> queue = new ArrayDeque<>();
        HexCoordinate first = occupiedCoordinates.iterator().next();
        queue.add(first);
        occupiedCoordinates.remove(first);

        // bfs through the set, removing visited coordinates
        while (!queue.isEmpty()) {
            // Pick from front of queue and remove from set
            HexCoordinate current = queue.poll();

            // Add all unvisited neighbours to the queue
            for (HexCoordinate neighbour : current.getNeighbours()) {
                if (
                    occupiedCoordinates.contains(neighbour)
                ) {
                    occupiedCoordinates.remove(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        // The piece is movable iff the board without that piece is connected
        // iff the flood fill traverses all other pieces
        return occupiedCoordinates.isEmpty();
    }

    public List<MovePiece> getValidMovesForPiece(HexCoordinate coordinate) {
        List<MovePiece> moves = new ArrayList<>();

        // If piece is not movable, return empty list
        if (!isPieceMovable(coordinate)) {
            return moves;
        }

        HivePiece piece = grid.get(coordinate).peek();
        PieceMovementStrategy movementStrategy = piece.getType().getMovementStrategy();

        return movementStrategy.getValidMoves(coordinate, this);
    }

    public List<HexCoordinate> getValidPlacementPositions(PlayerColour colour) {
        List<HexCoordinate> positions = new ArrayList<>();

        // If the grid is empty, we must place the first piece at the origin
        if (grid.keySet().isEmpty()) {
            positions.add(new HexCoordinate(0, 0));
            return positions;
        }

        // If the grid has one piece, we can place the second piece in any of the neighbouring positions
        if (grid.keySet().size() == 1) {
            HexCoordinate first = grid.keySet().iterator().next();
            positions.addAll(first.getNeighbours());
            return positions;
        }

        // Otherwise, we must iterate over all grid points
        for (HexCoordinate coordinate : grid.keySet()) {
            if (grid.get(coordinate).isEmpty() || grid.get(coordinate).peek().getColour() != colour) {
                continue;
            }

            // Current coordinate is of player colour, check if it has any valid empty neighbour positions
            for (HexCoordinate neighbour : coordinate.getNeighbours()) {
                if (grid.containsKey(neighbour)) {
                    continue;
                }

                // Check if the neigbour has any neigbours of the opposite colour
                boolean validPlacement = true;
                for (HexCoordinate neighbourOfNeighbour : neighbour.getNeighbours()) {
                    if (grid.containsKey(neighbourOfNeighbour) && grid.get(neighbourOfNeighbour).peek().getColour() != colour) {
                        validPlacement = false;
                        break;
                    }
                }

                if (validPlacement) {
                    positions.add(neighbour);
                }
            }
        }

        // Remove duplicates
        positions = positions.stream().distinct().collect(Collectors.toList());

        return positions;
    }
}