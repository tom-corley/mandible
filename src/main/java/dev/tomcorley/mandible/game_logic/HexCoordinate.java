package dev.tomcorley.mandible.game_logic;

import java.util.Objects;

public class HexCoordinate {
    private final int q;
    private final int r;

    public HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public int getS() {
        return -q - r;
    }

    public HexCoordinate[] getNeighbours() {
        return new HexCoordinate[] {
            new HexCoordinate(q + 1, r),
            new HexCoordinate(q + 1, r - 1),
            new HexCoordinate(q, r + 1),
            new HexCoordinate(q, r - 1),
            new HexCoordinate(q - 1, r),
            new HexCoordinate(q - 1, r + 1),
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HexCoordinate h)) return false;
        return this.q == h.q && this.r == h.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }
}