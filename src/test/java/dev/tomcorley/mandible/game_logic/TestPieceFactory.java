package dev.tomcorley.mandible.game_logic;

import java.util.EnumMap;
import java.util.Map;

public class TestPieceFactory {

    private final Map<HivePieceType, Integer> indices = new EnumMap<>(HivePieceType.class);

    public void reset() {
        indices.clear();
    }

    public HivePiece white(HivePieceType type) {
        return new HivePiece(PlayerColour.WHITE, type, nextIndex(type));
    }

    public HivePiece black(HivePieceType type) {
        return new HivePiece(PlayerColour.BLACK, type, nextIndex(type));
    }

    private int nextIndex(HivePieceType type) {
        return indices.merge(type, 1, Integer::sum);
    }
}
