package dev.tomcorley.mandible.engine.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.engine.HexCoordinate;
import dev.tomcorley.mandible.engine.HiveGrid;
import dev.tomcorley.mandible.engine.MovePiece;
import dev.tomcorley.mandible.engine.HexDirection;

public class BeetleMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();
        boolean hasClimbed = grid.getStackHeight(coordinate) > 1;

        // If the beetle has not climbed, it can edge move or climb up
        if (!hasClimbed) {
            moves.addAll(HiveMovementUtils.slideAlongOneEdge(coordinate, grid));
            moves.addAll(getValidClimbUpMoves(coordinate, grid));
        }

        // If the beetle has climbed, it can climb up, across or down
        else {
            moves.addAll(getValidClimbUpMoves(coordinate, grid));
            moves.addAll(getValidClimbAcrossMoves(coordinate, grid));
            moves.addAll(getValidClimbDownMoves(coordinate, grid));
        }

        return moves;
    }

    public static List<MovePiece> getValidClimbUpMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Can climb on to any occupied neighbouring space
        for (HexDirection direction : HexDirection.values()) {
            HexCoordinate neighbour = coordinate.add(direction);
            int potentialGateHeight = grid.getStackHeight(neighbour) + 1;
            if (grid.isClimbUp(coordinate, neighbour) && !grid.gateCheckAtHeight(coordinate, direction, potentialGateHeight)) {
                moves.add(new MovePiece(coordinate, neighbour));
            }
        }

        return moves;
    }

    public static List<MovePiece> getValidClimbAcrossMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Can climb across to any occupied neighbouring space
        for (HexDirection direction : HexDirection.values()) {
            HexCoordinate neighbour = coordinate.add(direction);

            if (!grid.isClimbAcross(coordinate, neighbour) || grid.gateCheck(coordinate, direction)) {
                continue;
            }

            moves.add(new MovePiece(coordinate, neighbour));
        }

        return moves;
    }

    public static List<MovePiece> getValidClimbAcrossAboveFloorMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = getValidClimbAcrossMoves(coordinate, grid);
        moves.removeIf(move -> grid.getStackHeight(move.from()) == 0);
        moves.removeIf(move -> grid.getStackHeight(move.to()) == 0);
        return moves;
    }

    public static List<MovePiece> getValidClimbDownMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Can climb down to any empty neighbouring space
        for (HexDirection direction : HexDirection.values()) {
            HexCoordinate neighbour = coordinate.add(direction);
            if (!grid.isClimbDown(coordinate, neighbour) || grid.gateCheck(coordinate, direction)) {
                continue;
            }

            moves.add(new MovePiece(coordinate, neighbour));
        }

        return moves;
    }

    public static List<MovePiece> getValidClimbDownAboveFloorMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = getValidClimbDownMoves(coordinate, grid);
        moves.removeIf(move -> grid.getStackHeight(move.to()) == 0);
        return moves;
    }

    public static List<MovePiece> getValidClimbDownToFloorMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = getValidClimbDownMoves(coordinate, grid);
        moves.removeIf(move -> grid.getStackHeight(move.to()) > 0);
        return moves;
    }
}
