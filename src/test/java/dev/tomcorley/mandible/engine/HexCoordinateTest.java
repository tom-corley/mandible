package dev.tomcorley.mandible.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HexCoordinateTest {

    // --- Construction and cubic invariant ---

    @Test
    @DisplayName("q + r + s = 0 for origin")
    void cubicInvariantAtOrigin() {
        var coord = new HexCoordinate(0, 0);
        assertEquals(0, coord.getQ() + coord.getR() + coord.getS());
    }

    @Test
    @DisplayName("q + r + s = 0 for arbitrary coordinate")
    void cubicInvariantArbitrary() {
        var coord = new HexCoordinate(3, -7);
        assertEquals(0, coord.getQ() + coord.getR() + coord.getS());
    }

    @Test
    @DisplayName("q + r + s = 0 for negative coordinates")
    void cubicInvariantNegative() {
        var coord = new HexCoordinate(-5, 2);
        assertEquals(0, coord.getQ() + coord.getR() + coord.getS());
    }

    @Test
    @DisplayName("s is derived as -q - r")
    void sDerivedFromQAndR() {
        var coord = new HexCoordinate(4, -1);
        assertEquals(-3, coord.getS());
    }

    // --- Neighbours ---

    @Test
    @DisplayName("origin has exactly 6 neighbours")
    void originHasSixNeighbours() {
        var origin = new HexCoordinate(0, 0);
        assertEquals(6, origin.getNeighbours().size());
    }

    @Test
    @DisplayName("non-origin coordinate has exactly 6 neighbours")
    void nonOriginHasSixNeighbours() {
        var coord = new HexCoordinate(3, -2);
        assertEquals(6, coord.getNeighbours().size());
    }

    @Test
    @DisplayName("neighbour list does not contain the coordinate itself")
    void neighboursDoNotContainSelf() {
        var coord = new HexCoordinate(1, 2);
        assertFalse(coord.getNeighbours().contains(coord));
    }

    @Test
    @DisplayName("all neighbours differ from source by exactly one hex step")
    void neighboursAreAdjacent() {
        var coord = new HexCoordinate(0, 0);
        for (HexCoordinate neighbour : coord.getNeighbours()) {
            int dq = Math.abs(neighbour.getQ() - coord.getQ());
            int dr = Math.abs(neighbour.getR() - coord.getR());
            int ds = Math.abs(neighbour.getS() - coord.getS());
            // In cube coords, adjacent hexes differ by exactly (1,1,0) in some permutation
            List<Integer> diffs = List.of(dq, dr, ds);
            assertTrue(diffs.contains(0) && diffs.stream().filter(d -> d == 1).count() == 2,
                    "Expected adjacent step but got dq=" + dq + " dr=" + dr + " ds=" + ds);
        }
    }

    @Test
    @DisplayName("neighbours are all unique")
    void neighboursAreUnique() {
        var coord = new HexCoordinate(2, -1);
        List<HexCoordinate> neighbours = coord.getNeighbours();
        assertEquals(neighbours.size(), neighbours.stream().distinct().count());
    }

    @Test
    @DisplayName("neighbours of a neighbour include the original coordinate")
    void neighbourSymmetry() {
        var coord = new HexCoordinate(0, 0);
        for (HexCoordinate neighbour : coord.getNeighbours()) {
            assertTrue(neighbour.getNeighbours().contains(coord),
                    neighbour + " should have " + coord + " as a neighbour");
        }
    }

    // --- add(HexDirection) ---

    @Test
    @DisplayName("adding N direction from origin gives (0, 1)")
    void addNorth() {
        var origin = new HexCoordinate(0, 0);
        var result = origin.add(HexDirection.N);
        assertEquals(new HexCoordinate(0, 1), result);
    }

    @Test
    @DisplayName("adding NE direction from origin gives (1, 0)")
    void addNorthEast() {
        var origin = new HexCoordinate(0, 0);
        var result = origin.add(HexDirection.NE);
        assertEquals(new HexCoordinate(1, 0), result);
    }

    @Test
    @DisplayName("adding S from (0,1) returns to origin")
    void addSouthFromNorth() {
        var coord = new HexCoordinate(0, 1);
        assertEquals(new HexCoordinate(0, 0), coord.add(HexDirection.S));
    }

    @Test
    @DisplayName("N then S returns to original position")
    void addNorthThenSouthReturnsToOrigin() {
        var origin = new HexCoordinate(0, 0);
        var result = origin.add(HexDirection.N).add(HexDirection.S);
        assertEquals(origin, result);
    }

    @Test
    @DisplayName("NE then SW returns to original position")
    void addNEThenSWReturnsToOrigin() {
        var origin = new HexCoordinate(0, 0);
        var result = origin.add(HexDirection.NE).add(HexDirection.SW);
        assertEquals(origin, result);
    }

    @Test
    @DisplayName("SE then NW returns to original position")
    void addSEThenNWReturnsToOrigin() {
        var origin = new HexCoordinate(0, 0);
        var result = origin.add(HexDirection.SE).add(HexDirection.NW);
        assertEquals(origin, result);
    }

    @Test
    @DisplayName("walking all 6 directions from origin produces the 6 neighbours")
    void allDirectionsProduceAllNeighbours() {
        var origin = new HexCoordinate(0, 0);
        List<HexCoordinate> neighbours = origin.getNeighbours();
        for (HexDirection dir : HexDirection.values()) {
            assertTrue(neighbours.contains(origin.add(dir)));
        }
    }

    // --- add(HexCoordinate) ---

    @Test
    @DisplayName("adding two coordinates sums q and r")
    void addCoordinates() {
        var a = new HexCoordinate(1, 2);
        var b = new HexCoordinate(3, -1);
        assertEquals(new HexCoordinate(4, 1), a.add(b));
    }

    @Test
    @DisplayName("adding origin is identity")
    void addOriginIsIdentity() {
        var coord = new HexCoordinate(5, -3);
        assertEquals(coord, coord.add(new HexCoordinate(0, 0)));
    }

    @Test
    @DisplayName("coordinate addition is commutative")
    void additionIsCommutative() {
        var a = new HexCoordinate(2, -1);
        var b = new HexCoordinate(-3, 4);
        assertEquals(a.add(b), b.add(a));
    }

    // --- equals / hashCode ---

    @Test
    @DisplayName("coordinates with same q,r are equal")
    void equalCoordinates() {
        assertEquals(new HexCoordinate(1, 2), new HexCoordinate(1, 2));
    }

    @Test
    @DisplayName("coordinates with different q are not equal")
    void differentQ() {
        assertNotEquals(new HexCoordinate(1, 2), new HexCoordinate(0, 2));
    }

    @Test
    @DisplayName("coordinates with different r are not equal")
    void differentR() {
        assertNotEquals(new HexCoordinate(1, 2), new HexCoordinate(1, 3));
    }

    @Test
    @DisplayName("coordinate is not equal to null")
    void notEqualToNull() {
        assertNotEquals(null, new HexCoordinate(0, 0));
    }

    @Test
    @DisplayName("equal coordinates produce equal hashCodes")
    void equalHashCodes() {
        var a = new HexCoordinate(3, -1);
        var b = new HexCoordinate(3, -1);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("can be used as HashMap key — put and get retrieve same value")
    void worksAsMapKey() {
        Map<HexCoordinate, String> map = new HashMap<>();
        map.put(new HexCoordinate(1, 2), "test");
        assertEquals("test", map.get(new HexCoordinate(1, 2)));
    }

    @Test
    @DisplayName("HashMap distinguishes different coordinates")
    void mapDistinguishesDifferentKeys() {
        Map<HexCoordinate, String> map = new HashMap<>();
        map.put(new HexCoordinate(1, 2), "a");
        map.put(new HexCoordinate(2, 1), "b");
        assertEquals(2, map.size());
        assertEquals("a", map.get(new HexCoordinate(1, 2)));
        assertEquals("b", map.get(new HexCoordinate(2, 1)));
    }

    // --- toString ---

    @Test
    @DisplayName("toString includes q and r values")
    void toStringFormat() {
        var coord = new HexCoordinate(3, -1);
        String str = coord.toString();
        assertTrue(str.contains("3"));
        assertTrue(str.contains("-1"));
    }
}
