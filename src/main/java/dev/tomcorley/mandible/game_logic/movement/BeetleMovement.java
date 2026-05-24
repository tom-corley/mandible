package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;
import dev.tomcorley.mandible.game_logic.HexDirection;

public class BeetleMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();
        boolean hasClimbed = grid.getGrid().get(coordinate).size() > 1;

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
        for (HexCoordinate neighbour : coordinate.getNeighbours()) {
            if (grid.isClimbUp(coordinate, neighbour)) {
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
            boolean isClimbAcross = grid.isClimbAcross(coordinate, neighbour);

            if (!isClimbAcross || grid.gateCheck(coordinate, direction)) {
                continue;
            }

            moves.add(new MovePiece(coordinate, neighbour));
        }

        return moves;
    }

    public static List<MovePiece> getValidClimbAcrossAboveFloorMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = getValidClimbAcrossMoves(coordinate, grid);
        moves.removeIf(move -> grid.getStackHeight(move.to()) == 0);
        return moves;
    }

    public static List<MovePiece> getValidClimbDownMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // Can climb down to any empty neighbouring space
        for (HexCoordinate neighbour : coordinate.getNeighbours()) {
            if (grid.isClimbDown(coordinate, neighbour)) {
                moves.add(new MovePiece(coordinate, neighbour));
            }
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
