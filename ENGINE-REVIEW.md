# Game Engine Review

Pre-Phase 2 (REST API) audit of the Hive game engine. Findings ranked by severity.

---

## High

### ~~1. No move validation in `makeMove`~~ — Resolved

See Resolved table.

### ~~2. Mutable internals exposed through getters~~ — Resolved

See Resolved table.

### ~~3. `PlayerController` couples I/O to game logic~~ — Phase 2

Phase 2 concern. `GameRunner` and `PlayerController` are console simulation artifacts that won't exist in the REST API. See `design/rest-api-architecture.md`.

### ~~4. `checkWinCondition` can reset a finished game to `IN_PROGRESS`~~ — Resolved

See Resolved table.

### ~~5. `getValidPlacementPositions` can return duplicates~~ — Resolved

See Resolved table.

---

## Medium

### ~~6. Beetle climb-up/down doesn't enforce Freedom-to-Move (gate check)~~ — Resolved

See Resolved table.

### ~~7. `BotController` creates a new `Random()` each call~~ — Resolved

See Resolved table.

### ~~8. No exception hierarchy~~ — Resolved

See Resolved table.

### ~~9. Pillbug missing "last-moved" tracking~~ — Partially resolved

See Resolved table for what's done. Remaining: freedom of movement gate check on throw (TODO in `PillbugMovement.java`).

---

## Low

### ~~10. `System.out.println` calls (12 instances)~~ — Resolved

See Resolved table.

### ~~11. `HumanController` is a stub~~ — Phase 2

See Resolved table.

### 12. `slideAlongOneEdge` inconsistency

Ant and spider remove the piece from the grid copy before checking; queen does not. Works by geometric coincidence, but inconsistent.

### ~~13. No game ID~~ — Phase 2

See Resolved table.

### 14. No move history, undo, or draw-by-repetition

Can trap bots in infinite loops.

---

## Resolved


| #   | Finding                                         | Resolution                                    |
| --- | ----------------------------------------------- | --------------------------------------------- |
| 1   | No move validation in `makeMove`                | By design — client picks from server-provided valid move set, so moves are validated by construction. Lightweight tamper check belongs in Phase 2 service layer, not the engine |
| 2   | Mutable internals exposed through getters       | Fixed — `getHand()`, `getPieceLocations()`, `getGrid()` now return `Collections.unmodifiable` wrappers |
| 3   | `PlayerController` couples I/O to game logic    | Phase 2 concern — `GameRunner`/`PlayerController` are console artifacts, replaced by REST request/response. See `design/rest-api-architecture.md` |
| 4   | `checkWinCondition` can reset finished game      | Fixed — early return guard when state is not `IN_PROGRESS` |
| 5   | `getValidPlacementPositions` returns duplicates  | Fixed — `.stream().distinct()` deduplication added |
| 6   | Beetle climb gate check and height logic         | Fixed — height-aware gate check on climb-across, proper height comparisons on all climb types, `isClimbAcross` formula corrected, ladybug movement refactored |
| 7   | `BotController` creates new `Random()` each call | Fixed — `Random` stored as field on the controller |
| 8   | No exception hierarchy                           | Fixed — `HiveException` base with `InvalidMoveException` |
| 11  | `HumanController` is a stub                      | Phase 2 — replaced by REST request/response model |
| 10  | `System.out.println` calls                       | Fixed — replaced with SLF4J logging |
| 13  | No game ID                                       | Phase 2 — needed for multi-game REST support |
| —   | `HivePiece` has no `equals`/`hashCode`          | Added per-type index and value-based equality |
| —   | `isPieceMovable` doesn't handle stacked pieces  | Fixed — stack size check added                |
| —   | Queen-by-turn-4 uses `== 4`, not `>= 4`         | Fixed to `>= 4`                               |
| —   | Mosquito unconditionally gets queen-bee sliding | Not a bug — intended behaviour                |
| —   | `pieceLocations` inconsistency with stacking    | Not a bug — non-injective mapping is by design |
| 9   | Pillbug last-moved locking                      | Fixed — `lockedCoordinate` on `HiveGrid`, set on any `movePiece`, cleared each turn in `advanceTurn`. Covers both "can't move itself" and "can't be thrown". Gate check on throw still pending (TODO in `PillbugMovement`) |
| —   | `EuclideanCoordinate` has no `equals`/`hashCode`/`toString` | Fixed — added                 |


---

## Remaining fix order before Phase 2


| #   | Fix                                              | Why                           |
| --- | ------------------------------------------------ | ----------------------------- |
| 1   | Pillbug special ability + stun tracking (#9)     | Correctness — missing game rule |
| 2   | Replace `System.out.println` with SLF4J (#10)    | Production logging            |
| 3   | `slideAlongOneEdge` inconsistency (#12)          | Correctness — piece removal before slide check is inconsistent across piece types |
| 4   | Move history / draw-by-repetition (#14)          | Prevents infinite bot loops, needed for event sourcing in Phase 2 |


