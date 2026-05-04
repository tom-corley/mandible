package dev.tomcorley.mandible.game_logic;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

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

    public List<HexCoordinate> getNeighbours() {
        List<HexCoordinate> neighbours = new ArrayList<>();
        neighbours.add(new HexCoordinate(q + 1, r));
        neighbours.add(new HexCoordinate(q + 1, r - 1));
        neighbours.add(new HexCoordinate(q, r + 1));
        neighbours.add(new HexCoordinate(q, r - 1));
        neighbours.add(new HexCoordinate(q - 1, r));
        neighbours.add(new HexCoordinate(q - 1, r + 1));
        return neighbours;
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