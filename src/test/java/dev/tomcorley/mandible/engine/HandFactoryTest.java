package dev.tomcorley.mandible.engine;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandFactoryTest {

    @Nested
    @DisplayName("standard hand")
    class StandardHandTests {

        @Test
        @DisplayName("contains 11 pieces")
        void standardHandSize() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            assertEquals(11, hand.size());
        }

        @Test
        @DisplayName("contains exactly 1 queen bee")
        void oneQueen() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            long count = hand.stream().filter(p -> p.getType() == HivePieceType.QUEEN_BEE).count();
            assertEquals(1, count);
        }

        @Test
        @DisplayName("contains exactly 3 ants")
        void threeAnts() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            long count = hand.stream().filter(p -> p.getType() == HivePieceType.ANT).count();
            assertEquals(3, count);
        }

        @Test
        @DisplayName("contains exactly 3 grasshoppers")
        void threeGrasshoppers() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            long count = hand.stream().filter(p -> p.getType() == HivePieceType.GRASSHOPPER).count();
            assertEquals(3, count);
        }

        @Test
        @DisplayName("contains exactly 2 spiders")
        void twoSpiders() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            long count = hand.stream().filter(p -> p.getType() == HivePieceType.SPIDER).count();
            assertEquals(2, count);
        }

        @Test
        @DisplayName("contains exactly 2 beetles")
        void twoBeetles() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            long count = hand.stream().filter(p -> p.getType() == HivePieceType.BEETLE).count();
            assertEquals(2, count);
        }

        @Test
        @DisplayName("does not contain expansion pieces")
        void noExpansionPieces() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            assertEquals(0, hand.stream().filter(p -> p.getType() == HivePieceType.LADYBUG).count());
            assertEquals(0, hand.stream().filter(p -> p.getType() == HivePieceType.MOSQUITO).count());
            assertEquals(0, hand.stream().filter(p -> p.getType() == HivePieceType.PILLBUG).count());
        }

        @Test
        @DisplayName("all pieces have the requested colour")
        void allSameColour() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.BLACK);
            assertTrue(hand.stream().allMatch(p -> p.getColour() == PlayerColour.BLACK));
        }

        @Test
        @DisplayName("white and black hands have same structure")
        void sameStructureBothColours() {
            List<HivePiece> white = HandFactory.createStandardHand(PlayerColour.WHITE);
            List<HivePiece> black = HandFactory.createStandardHand(PlayerColour.BLACK);

            assertEquals(white.size(), black.size());
            for (HivePieceType type : HivePieceType.values()) {
                long wCount = white.stream().filter(p -> p.getType() == type).count();
                long bCount = black.stream().filter(p -> p.getType() == type).count();
                assertEquals(wCount, bCount, "Mismatch for " + type);
            }
        }

        @Test
        @DisplayName("each piece is a distinct instance")
        void distinctInstances() {
            List<HivePiece> hand = HandFactory.createStandardHand(PlayerColour.WHITE);
            long distinctCount = hand.stream().distinct().count();
            assertEquals(hand.size(), distinctCount);
        }
    }

    @Nested
    @DisplayName("expanded hand")
    class ExpandedHandTests {

        @Test
        @DisplayName("contains 14 pieces")
        void expandedHandSize() {
            List<HivePiece> hand = HandFactory.createExpandedHand(PlayerColour.WHITE);
            assertEquals(14, hand.size());
        }

        @Test
        @DisplayName("contains all standard pieces plus expansions")
        void includesStandardPieces() {
            List<HivePiece> hand = HandFactory.createExpandedHand(PlayerColour.WHITE);
            assertEquals(1, hand.stream().filter(p -> p.getType() == HivePieceType.QUEEN_BEE).count());
            assertEquals(3, hand.stream().filter(p -> p.getType() == HivePieceType.ANT).count());
            assertEquals(3, hand.stream().filter(p -> p.getType() == HivePieceType.GRASSHOPPER).count());
            assertEquals(2, hand.stream().filter(p -> p.getType() == HivePieceType.SPIDER).count());
            assertEquals(2, hand.stream().filter(p -> p.getType() == HivePieceType.BEETLE).count());
        }

        @Test
        @DisplayName("contains exactly 1 ladybug")
        void oneLadybug() {
            List<HivePiece> hand = HandFactory.createExpandedHand(PlayerColour.WHITE);
            assertEquals(1, hand.stream().filter(p -> p.getType() == HivePieceType.LADYBUG).count());
        }

        @Test
        @DisplayName("contains exactly 1 mosquito")
        void oneMosquito() {
            List<HivePiece> hand = HandFactory.createExpandedHand(PlayerColour.WHITE);
            assertEquals(1, hand.stream().filter(p -> p.getType() == HivePieceType.MOSQUITO).count());
        }

        @Test
        @DisplayName("contains exactly 1 pillbug")
        void onePillbug() {
            List<HivePiece> hand = HandFactory.createExpandedHand(PlayerColour.WHITE);
            assertEquals(1, hand.stream().filter(p -> p.getType() == HivePieceType.PILLBUG).count());
        }

        @Test
        @DisplayName("all pieces have the requested colour")
        void allSameColour() {
            List<HivePiece> hand = HandFactory.createExpandedHand(PlayerColour.BLACK);
            assertTrue(hand.stream().allMatch(p -> p.getColour() == PlayerColour.BLACK));
        }
    }
}
