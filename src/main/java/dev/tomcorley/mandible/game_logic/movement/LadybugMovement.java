package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class LadybugMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // One beetle climb up move
        List<MovePiece> firstLadybugMoves = new ArrayList<>();
        firstLadybugMoves.addAll(BeetleMovement.getValidClimbUpMoves(coordinate, grid));

        // One beetle climb across move
        List<MovePiece> secondLadybugMoves = new ArrayList<>();
        HiveGrid gridCopy = new HiveGrid(grid);
        gridCopy.removePiece(coordinate);
        for (MovePiece firstMove : firstLadybugMoves) {
            secondLadybugMoves.addAll(BeetleMovement.getValidClimbAcrossMoves(firstMove.to(), gridCopy));
        }
        secondLadybugMoves = secondLadybugMoves.stream().distinct().collect(Collectors.toList());

        // One beetle climb down move
        List<MovePiece> thirdLadybugMoves = new ArrayList<>();
        for (MovePiece secondMove : secondLadybugMoves) {
            thirdLadybugMoves.addAll(BeetleMovement.getValidClimbDownMoves(secondMove.to(), gridCopy));
        }
        // Make from of the move original starting coordinate
        for (MovePiece thirdMove : thirdLadybugMoves) {
            moves.add(new MovePiece(coordinate, thirdMove.to()));
        }
        moves = moves.stream().distinct().collect(Collectors.toList());

        return moves;
    }
}
