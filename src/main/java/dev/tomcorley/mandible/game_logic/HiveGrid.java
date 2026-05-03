package dev.tomcorley.mandible.game_logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class HiveGrid {
    private final Map<HexCoordinate, Deque<HivePiece>> grid;

    public HiveGrid() {
        this.grid = new HashMap<>();
    }

    public Map<HexCoordinate, Deque<HivePiece>> getGrid() {
        return grid;
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

    public boolean isValidMove(MovePiece move) {
        HexCoordinate from = move.from();
        HexCoordinate to = move.to();

        // Validate piece to move exists
        if (!grid.containsKey(from)) {
            return false;
        }

        // validate destination is free
        return !grid.containsKey(to);
    }

    public void movePiece(MovePiece move) {
        HexCoordinate from = move.from();
        HexCoordinate to = move.to();

        // Validate move
        if (!isValidMove(move)) {
            throw new IllegalArgumentException("Invalid move");
        }

        // Pop piece to move from stack
        HivePiece piece = grid.get(from).pop();

        // Push piece to new or existing stack
        if (grid.containsKey(to)) {
            grid.get(to).push(piece);
        } else {
            Deque<HivePiece> stack = new ArrayDeque<>();
            stack.push(piece);
            grid.put(to, stack);
        }
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

    public List<MovePiece> getValidMovesForPiece(HexCoordinate coordinate) {
        List<MovePiece> moves = new ArrayList<>();

        // If piece is not movable, return empty list
        if (!isPieceMovable(coordinate)) {
            return moves;
        }

        HivePiece piece = grid.get(coordinate).peek();
        HivePieceType type = piece.getType();

        switch (type) {
            case QUEEN_BEE:
                // TODO: Implement queen bee moves
                break;
            case LADYBUG:
                // TODO: Implement ladybug moves
                break;
            case GRASSHOPPER:
                // TODO: Implement grasshopper moves
                break;
            case SPIDER:
                // TODO: Implement spider moves
                break;
            case ANT:
                // TODO: Implement ant moves
                break;
            case BEETLE:
                // TODO: Implement beetle moves
                break;
            case PILLBUG:
                // TODO: implement pillbug moves
                break;
            case MOSQUITO:
                // TODO: implement mosquito moves
                break;
            default:
                throw new IllegalArgumentException("Invalid piece type");
        }

        return moves;
    }

    public List<HexCoordinate> getValidPlacementPositions(PlayerColour colour) {
        List<HexCoordinate> positions = new ArrayList<>();

        // Iterate over all grid points
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

        return positions;
    }
}