package dev.tomcorley.mandible.game_logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Deque;

public class HiveGrid {
    private final Map<HexCoordinate, Deque<HivePiece>> grid;

    public HiveGrid() {
        this.grid = new HashMap<>();
    }

    public Map<HexCoordinate, Deque<HivePiece>> getGrid() {
        return grid;
    }
}