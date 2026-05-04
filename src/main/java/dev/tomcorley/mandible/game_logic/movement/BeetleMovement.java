package dev.tomcorley.mandible.game_logic.movement;

import java.util.List;
import java.util.ArrayList;

import dev.tomcorley.mandible.game_logic.HexCoordinate;
import dev.tomcorley.mandible.game_logic.HiveGrid;
import dev.tomcorley.mandible.game_logic.MovePiece;

public class BeetleMovement implements PieceMovementStrategy {
    @Override
    public List<MovePiece> getValidMoves(HexCoordinate coordinate, HiveGrid grid) {
        List<MovePiece> moves = new ArrayList<>();

        // If the beetle has not climbed, it can edge move or climb
        

        // If the beetle has climbed, it can neighbour move or climb down

        // TODO: Implement beetle movement logic
        return moves;
    }
}
