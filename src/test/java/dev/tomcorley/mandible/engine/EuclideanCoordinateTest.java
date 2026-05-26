package dev.tomcorley.mandible.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class EuclideanCoordinateTest {

    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("stores x and y values")
    void storesValues() {
        EuclideanCoordinate coord = new EuclideanCoordinate(3.5, -1.2);
        assertEquals(3.5, coord.getX(), DELTA);
        assertEquals(-1.2, coord.getY(), DELTA);
    }

    @Test
    @DisplayName("hex origin converts to euclidean origin")
    void hexOriginToEuclideanOrigin() {
        HexCoordinate hex = new HexCoordinate(0, 0);
        EuclideanCoordinate euc = hex.toEuclidean();

        assertEquals(0.0, euc.getX(), DELTA);
        assertEquals(0.0, euc.getY(), DELTA);
    }

    @Test
    @DisplayName("hex (1, 0) converts correctly")
    void hexOneZeroConverts() {
        HexCoordinate hex = new HexCoordinate(1, 0);
        EuclideanCoordinate euc = hex.toEuclidean();

        // x = (2/3)*1 + (-1/3)*0 = 2/3
        // y = (sqrt(3)/3)*0 = 0
        assertEquals(2.0 / 3.0, euc.getX(), DELTA);
        assertEquals(0.0, euc.getY(), DELTA);
    }

    @Test
    @DisplayName("hex (0, 1) converts correctly")
    void hexZeroOneConverts() {
        HexCoordinate hex = new HexCoordinate(0, 1);
        EuclideanCoordinate euc = hex.toEuclidean();

        // x = (2/3)*0 + (-1/3)*1 = -1/3
        // y = (sqrt(3)/3)*1 = sqrt(3)/3
        assertEquals(-1.0 / 3.0, euc.getX(), DELTA);
        assertEquals(Math.sqrt(3) / 3.0, euc.getY(), DELTA);
    }

    @Test
    @DisplayName("hex (1, 1) converts correctly")
    void hexOneOneConverts() {
        HexCoordinate hex = new HexCoordinate(1, 1);
        EuclideanCoordinate euc = hex.toEuclidean();

        // x = (2/3)*1 + (-1/3)*1 = 1/3
        // y = (sqrt(3)/3)*1
        assertEquals(1.0 / 3.0, euc.getX(), DELTA);
        assertEquals(Math.sqrt(3) / 3.0, euc.getY(), DELTA);
    }

    @Test
    @DisplayName("negative hex coordinates convert correctly")
    void negativeHexConverts() {
        HexCoordinate hex = new HexCoordinate(-2, 3);
        EuclideanCoordinate euc = hex.toEuclidean();

        double expectedX = (2.0 / 3.0) * (-2) + (-1.0 / 3.0) * 3;
        double expectedY = (Math.sqrt(3) / 3.0) * 3;
        assertEquals(expectedX, euc.getX(), DELTA);
        assertEquals(expectedY, euc.getY(), DELTA);
    }

    @Test
    @DisplayName("opposite hex coordinates produce opposite euclidean x")
    void oppositeCoordinates() {
        HexCoordinate a = new HexCoordinate(2, -1);
        HexCoordinate b = new HexCoordinate(-2, 1);

        EuclideanCoordinate eucA = a.toEuclidean();
        EuclideanCoordinate eucB = b.toEuclidean();

        assertEquals(-eucA.getX(), eucB.getX(), DELTA);
        assertEquals(-eucA.getY(), eucB.getY(), DELTA);
    }
}
