# Coordinates on a Hexagonal Grid

### Cubic Coordinates

You can move in three directions, assuming flat top:
- N/S q-axis
- NE/SW r-axis
- SE/NW s-asix

Moving in any direction fixes one of these:
- for example, placing a hex directly above shifts by (0, -1, +1)
- below is (0, +1, -1)

The constraint here is that moving to an adjacent tile represents a shift of +1 in one axis, and -1 in another.
Given the central tile is (0,0,0), this preserves the constraint q+r+s=0.

Therefore we actually only have two degrees of freedom, and so can eliminate s entirely.

### Axial Coordinates

These are the (q,r) from the cubic (q,r,s). This is the space defined uniquely by integer linear combinations of the vectors
- (0,1)
- (1,0)
- (1, -1) 

This is a metric space over Z^2, with the metric of distance being (|dq| + |dr| + |dq+dr|)
This is quite different from the manhattan distance (|dx|+|dy|) over an integer lattice.

### Geometric Intuition

We can think of the axial grid as the integer latice over Z^2 with basis vectors offset by 30 or 60 degrees, instead of orthogonally.

The unit ball over this metric space contains 6 points instead of 4 in the standard integer lattice. 