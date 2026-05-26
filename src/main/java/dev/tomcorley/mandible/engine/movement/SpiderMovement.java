package dev.tomcorley.mandible.engine.movement;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.stream.Collectors;


import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;

public class SpiderMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        HiveGrid gridCopy = grid.copy();
        gridCopy.removePiece(coordinate);

        // Set up path array and dfs stack
        Deque<List<HexCoordinate>> stack = new ArrayDeque<>();
        stack.push(new ArrayList<>(List.of(coordinate)));

        // Max depth 3 DFS
        while (!stack.isEmpty()) {
            // Pick from top of stack and append to path
            List<HexCoordinate> current = stack.pop();
            HexCoordinate currentCoordinate = current.get(current.size() - 1);

            if (current.size() == 4) {
                // Convert path to move
                MovePiece move = new MovePiece(current.get(0), current.get(3));
                moves.add(move);
                continue;
            }

            // Get valid next steps from current coordinate
            List<MovePiece> nextSteps = HiveMovementUtils.slideAlongOneEdge(currentCoordinate, gridCopy);
            
            // Add all valid neighbours to the stack
            for (MovePiece nextStep : nextSteps) {
                HexCoordinate nextCoordinate = nextStep.to();

                // If next coordinate is free and not already in path, add to stack and path
                if (!current.contains(nextCoordinate)) {
                    List<HexCoordinate> newPath = new ArrayList<>(current);
                    newPath.add(nextCoordinate);
                    stack.push(newPath);
                }
            }
        }

        // We may have several paths to the same destination, so we need to deduplicate
        moves = moves.stream().distinct().collect(Collectors.toList());


        return moves;
    }
}
