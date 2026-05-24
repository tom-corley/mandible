package dev.tomcorley.mandible.game_logic;

import java.util.List;
import java.util.ArrayList;

public class HandFactory {
    private HandFactory() {}

    public static List<HivePiece> createStandardHand(PlayerColour colour) {
        List<HivePiece> hand = new ArrayList<>();

        // 1 Queen Bee
        hand.add(new HivePiece(colour, HivePieceType.QUEEN_BEE, 1));

        // 3 Ants
        for (int i = 1; i <= 3; i++) {
            hand.add(new HivePiece(colour, HivePieceType.ANT, i));
        }

        // 3 Grasshoppers
        for (int i = 1; i <= 3; i++) {
            hand.add(new HivePiece(colour, HivePieceType.GRASSHOPPER, i));
        }

        // 2 Spiders
        for (int i = 1; i <= 2; i++) {
            hand.add(new HivePiece(colour, HivePieceType.SPIDER, i));
        }

        // 2 Beetles
        for (int i = 1; i <= 2; i++) {
            hand.add(new HivePiece(colour, HivePieceType.BEETLE, i));
        }

        return hand;
    }

    public static List<HivePiece> createExpandedHand(PlayerColour colour) {
        List<HivePiece> hand = createStandardHand(colour);

        // 1 Ladybug
        hand.add(new HivePiece(colour, HivePieceType.LADYBUG, 1));

        // 1 Mosquito
        hand.add(new HivePiece(colour, HivePieceType.MOSQUITO, 1));

        // 1 Pillbug
        hand.add(new HivePiece(colour, HivePieceType.PILLBUG, 1));

        return hand;
    }
}
