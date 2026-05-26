package dev.tomcorley.mandible.engine.movement;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;

public class AntMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        HiveGrid gridCopy = grid.copy();
        gridCopy.removePiece(coordinate);

        // We need to essentially bfs from the ants position sliding along one edge at a time, keeping track of visited coordinates
        Queue<HexCoordinate> queue = new ArrayDeque<>();
        queue.add(coordinate);
        Set<HexCoordinate> visited = new HashSet<>();
        visited.add(coordinate);

        while (!queue.isEmpty()) {
            HexCoordinate current = queue.poll();
            List<MovePiece> movesFromCurrent = HiveMovementUtils.slideAlongOneEdge(current, gridCopy);
            for (MovePiece move : movesFromCurrent) {
                if (!visited.contains(move.to())) {
                    queue.add(move.to());
                    visited.add(move.to());
                }
            }
        }
        
        visited.remove(coordinate);

        // Convert the visited coordinates to a list of moves
        List<MovePiece> movesToVisited =visited.stream().map(coord -> new MovePiece(coordinate, coord)).collect(Collectors.toList());
        moves.addAll(movesToVisited);

        return moves;
    }
}
