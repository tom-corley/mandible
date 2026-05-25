NW: BG
O: WB
NE: BG
N: Objective for WB

let x be height of O stack (includes the beetle itself)
,y be height of NE/NW stack (assumed equal; these are the gate pieces)
and z be height of N stack (before the beetle arrives)

When should the beetle climb across, up or down moves be legal, and when illegal:

---

## Movement Rule

When the beetle moves from O to N, it physically passes between NW and NE.
These two pieces form a **gate**. The beetle's path height is:

    travel_height = max(x, z+1)

where z+1 is the height of N after the beetle lands.

**Gate rule**: blocked iff both gate pieces are >= travel_height.
Since NW and NE both have height y:

    ALLOWED iff max(x, z+1) > y
    BLOCKED iff max(x, z+1) <= y  (i.e. x <= y AND z+1 <= y, i.e. x <= y AND z < y)

### One Hive Rule interaction

In this specific topology, the One Hive Rule does not add any extra constraint
beyond the gate condition. When x=1 and z=0 (the only case where the beetle could
be a bridge), the gate (y >= 1) blocks the move identically. If y=0 (no gate pieces,
no bridge concern), both conditions agree the move is allowed.

---

## Move Types

| Type         | Condition    | Travel height | Allowed iff |
|--------------|--------------|---------------|-------------|
| Climb down   | z < x-1      | x             | x > y       |
| Flat (same level) | z = x-1 | x            | x > y       |
| Climb up     | z >= x       | z+1           | z >= y      |

---

## Edge Cases by Gate Height (y)

### y=0: No gate
- max(x, z+1) >= 1 > 0 for all valid x
- **All moves are allowed.**

### y=1: One piece on each side
- Blocked only when max(x, z+1) <= 1, i.e. x=1 AND z=0
- **One blocked case**: beetle alone at O moving to empty N (flat ground slide).
  This is also the case where the beetle would be pinned by the One Hive Rule.
- All other (x,z) pairs: allowed.

### y=2
- Blocked when x <= 2 AND z <= 1 (travel height <= 2)
- Blocked region: x ∈ {1,2} × z ∈ {0,1}  →  4 blocked pairs
- Allowed as soon as x >= 3 OR z >= 2

Notable boundaries:
- x=2, z=1: max(2,2)=2, NOT > 2 → blocked
- x=2, z=2: max(2,3)=3 > 2 → allowed  (climbing up breaks free of gate)
- x=3, z=0: max(3,1)=3 > 2 → allowed  (descending from sufficient height)
- x=3, z=1: max(3,2)=3 > 2 → allowed

### y=3
- Blocked when x <= 3 AND z <= 2 (travel height <= 3)
- Blocked region: x ∈ {1,2,3} × z ∈ {0,1,2}  →  9 blocked pairs
- Allowed as soon as x=4 OR z >= 3

Notable boundaries:
- x=3, z=2: max(3,3)=3, NOT > 3 → blocked
- x=3, z=3: max(3,4)=4 > 3 → allowed  (climbing up to equal gate height)
- x=4, z=0: max(4,1)=4 > 3 → allowed  (full-height descent)
- x=4, z=2: max(4,3)=4 > 3 → allowed

### y=4 (max gate height)
- Blocked when x <= 4 AND z <= 3 (travel height <= 4)
- Since x <= 4, only z=4 (destination already at max height) allows the move.
  Travel height becomes max(x, 5)=5 > 4.
- **Allowed only when z=4, for any x.**
- All (x, z<=3) pairs blocked regardless of x.

Notable boundaries:
- x=4, z=3: max(4,4)=4, NOT > 4 → blocked  (even from max height, can't get over)
- x=1, z=4: max(1,5)=5 > 4 → allowed  (beetle alone can climb onto a height-4 stack)
- x=4, z=4: max(4,5)=5 > 4 → allowed

---

## Summary Table: ALLOWED (A) / BLOCKED (B)

Travel height = max(x, z+1). Allowed iff travel height > y.

### y=0 (all allowed — omitted)

### y=1
```
z\x  1  2  3  4
  0  B  A  A  A
  1  A  A  A  A
  2  A  A  A  A
  3  A  A  A  A
  4  A  A  A  A
```

### y=2
```
z\x  1  2  3  4
  0  B  B  A  A
  1  B  B  A  A
  2  A  A  A  A
  3  A  A  A  A
  4  A  A  A  A
```

### y=3
```
z\x  1  2  3  4
  0  B  B  B  A
  1  B  B  B  A
  2  B  B  B  A
  3  A  A  A  A
  4  A  A  A  A
```

### y=4
```
z\x  1  2  3  4
  0  B  B  B  B
  1  B  B  B  B
  2  B  B  B  B
  3  B  B  B  B
  4  A  A  A  A
```

---

## Key Observations

1. **Flat ground move** (x=1, z=0): blocked by any gate (y >= 1). This is the most
   restricted case — the beetle has no height advantage to climb over.

2. **Climbing up** always helps: increasing z raises travel height and can unlock moves
   that descending or flat moves cannot make.

3. **Descent from max height** (x=4): can pass gates up to y=3 when dropping to any z.
   Blocked only by y=4 unless destination is also z=4.

4. **Max gate** (y=4): the beetle is almost completely trapped — only escape is climbing
   onto a stack of 4 (z=4), reaching travel height 5.

5. **Symmetry of the blocked region**: blocked iff x <= y AND z < y. The two independent
   conditions mean the "blocked rectangle" in (x,z) space shrinks from bottom-left as y
   increases, with the x-dimension bounded by x <= y and z-dimension bounded by z <= y-1.

6. **When both NW and NE are empty (y=0)**: all moves free. This also means the
   beetle is trivially not an articulation point.

7. **When the beetle is the only piece at O (x=1) and N is occupied (z >= 1)**:
   the One Hive Rule is satisfied (NW—N—NE path exists), and the gate condition
   reduces to z >= y. The beetle can escape upward by climbing onto a tall-enough stack.
