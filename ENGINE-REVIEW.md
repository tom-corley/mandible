# Game Engine Review

Pre-Phase 2 (REST API) audit of the Hive game engine. Findings ranked by severity.

---

## High

### 1. No move validation in `makeMove`

**File:** `HiveGame.java:69-71`

Accepts any `HiveMove` with zero validation. No check for correct player's turn, no check the move is in the valid set, no queen-by-turn-4 enforcement. Fine internally, but a REST API receives arbitrary moves from untrusted clients.

### 2. Mutable internals exposed through getters

**Files:** `HiveBoard.getPieceLocations()`, `HiveGrid.getGrid()`, `Player.getHand()`

All return raw mutable collections. Any caller can mutate game state without going through the APIs. In a REST context with concurrent requests, this is a data corruption vector.

**Fix:** Return `Collections.unmodifiableMap()` / `Collections.unmodifiableList()`.

### 3. `PlayerController` couples I/O to game logic

**File:** `PlayerController.java`

`chooseMove(HiveGame game)` passes the entire mutable game to the controller, blocks the thread, and drives via a `while` loop in `GameRunner`. In a REST API moves arrive asynchronously via HTTP. You need a request-response model.

### 4. `checkWinCondition` can reset a finished game to `IN_PROGRESS`

**File:** `HiveGame.java:36`

The `else` branch sets `state = IN_PROGRESS`. If called again after a game ends, it can un-end the game. Once a game finishes, the state should be immutable.

### 5. `getValidPlacementPositions` can return duplicates

**File:** `HiveGrid.java:159-203`

Two friendly pieces sharing an empty neighbour both add it. No deduplication. This inflates move lists and creates duplicate `PlacePiece` moves.

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
| 1   | Add move validation to `makeMove`                | REST receives untrusted input |
| 2   | Fix `getValidPlacementPositions` duplicates      | Inflates move lists           |
| 3   | Return unmodifiable collections from getters     | Prevent mutation              |
| 4   | `checkWinCondition` — don't reset finished games | State integrity               |
| 5   | Add domain exception hierarchy                   | Proper HTTP error mapping     |
| 6   | Replace `System.out.println` with SLF4J          | Production logging            |


