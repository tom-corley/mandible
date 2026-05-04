# Coordinates on a Hexagonal Grid

### Cubic Coordinates

You can move in three directions, assuming flat top:
- N/S $q$-axis
- NE/SW $r$-axis
- SE/NW $s$-axis

Moving in any direction fixes one of these:
- for example, placing a hex directly above shifts by $(0, -1, +1)$
- below is $(0, +1, -1)$

The constraint here is that moving to an adjacent tile represents a shift of $+1$ in one axis, and $-1$ in another.
Given the central tile is $(0,0,0)$, this preserves the constraint $q+r+s=0$.

Therefore we actually only have two degrees of freedom, and so can eliminate $s$ entirely.

### Axial Coordinates

These are the $(q,r)$ from the cubic $(q,r,s)$. This is the space defined uniquely by integer linear combinations of the vectors
- $(0,1)$
- $(1,0)$
- $(1, -1)$

This is a metric space over $\mathbb{Z}^2$, with the metric of distance being $\frac{|\Delta q| + |\Delta r| + |\Delta q + \Delta r|}{2}$

This is quite different from the manhattan distance $(|dx|+|dy|)$ over an integer lattice.

### Geometric Intuition

We can think of the axial grid as the integer lattice over $\mathbb{Z}^2$ with basis vectors offset by $120°$, instead of orthogonally.

The unit ball over this metric space contains 6 points instead of the 4 in the standard integer lattice.

### Conversions between Euclidean and Axial

We can compute a change of basis matrix between the two spaces by expressing the basis vectors of each space in terms of the other. Assuming a flat top orientation:

If we set the centre to vertex distance of a hexagon to $1$:
- The distance from center to middle of edge is $\frac{\sqrt{3}}{2}$
- The height of the hexagon is $\sqrt{3}$

We can let $q$ be fixed along the SW/NE axis, therefore it changes moving SE/NW, we can set NW as the positive direction.

Let $r$ be fixed along vertical columns, going up by $1$ if we shift to the right column of hexagons, and down by $1$ if we go left.

We then have:
- $q$ means moving up by $\frac{\sqrt{3}}{2}$ and across by $\frac{3}{2}$
- $r$ means moving up by $\sqrt{3}$

So the change of basis matrix from axial to euclidean becomes:

$$M = \begin{bmatrix} \frac{3}{2} & 0 \\ \frac{\sqrt{3}}{2} & \sqrt{3} \end{bmatrix}$$

We can invert this matrix, or just set up a system of simultaneous equations to get the reverse change of basis:

$$M^{-1} = \begin{bmatrix} \frac{2}{3} & 0 \\ -\frac{1}{3} & \frac{\sqrt{3}}{3} \end{bmatrix}$$
