package dev.tomcorley.mandible.game_logic;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public class HexCoordinate implements Comparable<HexCoordinate> {
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

        // Iterate over all directions and add the neighbour in that direction
        for (HexDirection direction : HexDirection.values()) {
            HexCoordinate neighbour = this.add(direction);
            neighbours.add(neighbour);
        }

        return neighbours;
    }

    public HexCoordinate add(HexDirection direction) {
        return new HexCoordinate(q + direction.getQ(), r + direction.getR());
    }

    public HexCoordinate add(HexCoordinate other) {
        return new HexCoordinate(q + other.getQ(), r + other.getR());
    }

    public EuclideanCoordinate toEuclidean() {
        double x = ((2.0d / 3.0d) * q) + ((-1.0d / 3.0d) * r);
        double y = (0 * q) + ((Math.sqrt(3) / 3) * r);
        return new EuclideanCoordinate(x, y);
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

    @Override
    public String toString() {
        return "HexCoordinate(q=" + q + ", r=" + r + ")";
    }

    @Override 
    public int compareTo(HexCoordinate other) {
        if (this.q == other.q) {
            return Integer.compare(this.r, other.r);
        }
        return Integer.compare(this.q, other.q);
    }
}