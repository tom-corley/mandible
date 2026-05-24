package dev.tomcorley.mandible.game_logic;

import java.util.List;

public enum HexDirection {
    N(0, 1),
    NE(1, 0),
    SE(1, -1),
    S(0, -1),
    SW(-1, 0),
    NW(-1, 1),
    ;

    private final int q;
    private final int r;

    HexDirection(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public HexDirection clockwise() {
        return values()[Math.floorMod(this.ordinal() + 1, values().length)];
    }

    public HexDirection antiClockwise() {
        return values()[Math.floorMod(this.ordinal() - 1, values().length)];
    }

    public List<HexDirection> getNeighbouringDirections() {
        return List.of(this.antiClockwise(), this.clockwise());
    }
}
