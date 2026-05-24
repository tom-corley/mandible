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

### 6. Beetle climb-up/down doesn't enforce Freedom-to-Move (gate check)

**File:** `BeetleMovement.java:31-42, 57-68`

`getValidClimbUpMoves` allows climbing onto any occupied neighbour with no gate check. `getValidClimbDownMoves` allows climbing to any empty neighbour with no adjacency-to-hive check. A beetle could land somewhere disconnected.

### 7. `BotController` creates a new `Random()` each call

**File:** `BotController.java:23`

Not seedable, not reproducible. For testing, debugging, and replay, the `Random` should be injected or stored as a field.

### 8. No exception hierarchy

All errors throw `IllegalArgumentException` with string messages. A REST API needs structured responses. Define `InvalidMoveException`, `GameOverException`, `NotYourTurnException` etc.

### 9. Pillbug missing "last-moved" tracking

**File:** `PillbugMovement.java`

The pillbug can't move a piece that was just moved on the previous turn (official Hive rule). No tracking for this exists.

---

## Low

### 10. `System.out.println` calls (12 instances)

Replace with SLF4J before Spring.

### 11. `HumanController` is a stub

Returns `null` (permanent turn-skip). Remove or implement before Phase 2.

### 12. `slideAlongOneEdge` inconsistency

Ant and spider remove the piece from the grid copy before checking; queen does not. Works by geometric coincidence, but inconsistent.

### 13. No game ID

Needed for multi-game REST support.

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
| —   | `HivePiece` has no `equals`/`hashCode`          | Added per-type index and value-based equality |
| —   | `isPieceMovable` doesn't handle stacked pieces  | Fixed — stack size check added                |
| —   | Queen-by-turn-4 uses `== 4`, not `>= 4`         | Fixed to `>= 4`                               |
| —   | Mosquito unconditionally gets queen-bee sliding | Not a bug — intended behaviour                |
| —   | `pieceLocations` inconsistency with stacking    | Not a bug — non-injective mapping is by design |
| —   | `EuclideanCoordinate` has no `equals`/`hashCode`/`toString` | Fixed — added                 |


---

## Recommended fix order before Phase 2


| #   | Fix                                              | Why                           |
| --- | ------------------------------------------------ | ----------------------------- |
| ~~1~~   | ~~Add move validation to `makeMove`~~        | Resolved — validated by construction |
| ~~2~~   | ~~Return unmodifiable collections from getters~~ | Resolved — wrapped with `Collections.unmodifiable` |
| ~~3~~   | ~~Fix `getValidPlacementPositions` duplicates~~ | Resolved — `.distinct()` deduplication |
| ~~4~~   | ~~`checkWinCondition` — don't reset finished games~~ | Resolved — early return guard |
| 5   | Add domain exception hierarchy                   | Proper HTTP error mapping     |
| 6   | Replace `System.out.println` with SLF4J          | Production logging            |


