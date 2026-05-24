package dev.tomcorley.mandible.game_logic;

import java.util.Objects;

public class EuclideanCoordinate {
    private final double x;
    private final double y;

    public EuclideanCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }   

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof EuclideanCoordinate e)) return false;
        return this.x == e.x && this.y == e.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "EuclideanCoordinate(x=" + x + ", y=" + y + ")";
    }
}
