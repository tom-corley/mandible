package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.ArrayList;

public class HandFactory {
    private HandFactory() {}

    public static List<HivePiece> createStandardHand(PlayerColour colour) {
        List<HivePiece> hand = new ArrayList<>();

        // 1 Queen Bee
        hand.add(new HivePiece(colour, HivePieceType.QUEEN_BEE));

        // 3 Ants
        hand.add(new HivePiece(colour, HivePieceType.ANT));
        hand.add(new HivePiece(colour, HivePieceType.ANT));
        hand.add(new HivePiece(colour, HivePieceType.ANT));
        
        // 3 Grasshoppers
        hand.add(new HivePiece(colour, HivePieceType.GRASSHOPPER));
        hand.add(new HivePiece(colour, HivePieceType.GRASSHOPPER));
        hand.add(new HivePiece(colour, HivePieceType.GRASSHOPPER));

        // 2 Spiders
        hand.add(new HivePiece(colour, HivePieceType.SPIDER));
        hand.add(new HivePiece(colour, HivePieceType.SPIDER));

        // 2 Beetles
        hand.add(new HivePiece(colour, HivePieceType.BEETLE));
        hand.add(new HivePiece(colour, HivePieceType.BEETLE));

        return hand;
    }

    public static List<HivePiece> createExpandedHand(PlayerColour colour) {
        List<HivePiece> hand = createStandardHand(colour);

        // 1 Ladybug
        hand.add(new HivePiece(colour, HivePieceType.LADYBUG));

        // 1 Mosquito
        hand.add(new HivePiece(colour, HivePieceType.MOSQUITO));

        // 1 Pillbug
        hand.add(new HivePiece(colour, HivePieceType.PILLBUG));

        return hand;
    }
}
