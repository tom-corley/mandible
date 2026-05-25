package dev.tomcorley.mandible.game_logic.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class LadybugMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // 1. One beetle climb up move
        List<MovePiece> firstLadybugMoves = new ArrayList<>();
        firstLadybugMoves.addAll(BeetleMovement.getValidClimbUpMoves(coordinate, grid));

        // 2. One beetle climb across or climb up move — must stay on top of the hive
        List<MovePiece> secondLadybugMoves = new ArrayList<>();
        HiveGrid gridCopy = grid.copy();
        for (MovePiece firstMove : firstLadybugMoves) {
            gridCopy.movePiece(firstMove);
            secondLadybugMoves.addAll(BeetleMovement.getValidClimbAcrossAboveFloorMoves(firstMove.to(), gridCopy));
            secondLadybugMoves.addAll(BeetleMovement.getValidClimbUpMoves(firstMove.to(), gridCopy));
            secondLadybugMoves.addAll(BeetleMovement.getValidClimbDownAboveFloorMoves(firstMove.to(), gridCopy));
            gridCopy.movePiece(firstMove.invertMove());
        }

        // Create list of two step moves and deduplicate
        List<MovePiece> twoStepMoves = new ArrayList<>();
        for (MovePiece secondMove : secondLadybugMoves) {
            twoStepMoves.add(new MovePiece(coordinate, secondMove.to()));
        }
        twoStepMoves = twoStepMoves.stream().distinct().collect(Collectors.toList());

        // 3. Climb down off the hive to any empty adjacent space
        List<MovePiece> thirdLadybugMoves = new ArrayList<>();
        for (MovePiece firstTwoMoves : twoStepMoves) {
            gridCopy.movePiece(firstTwoMoves);
            thirdLadybugMoves.addAll(BeetleMovement.getValidClimbDownToFloorMoves(firstTwoMoves.to(), gridCopy));
            gridCopy.movePiece(firstTwoMoves.invertMove());
        }

        // Make from of the move original starting coordinate
        for (MovePiece thirdMove : thirdLadybugMoves) {
            moves.add(new MovePiece(coordinate, thirdMove.to()));
        }
        moves = moves.stream().distinct().collect(Collectors.toList());

        return moves;
    }
}
