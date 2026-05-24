# Game Engine Review

Pre-Phase 2 (REST API) audit of the Hive game engine. Findings ranked by severity.

---

## Critical

### 1. `HivePiece` has no `equals`/`hashCode` — identity-based equality

**File:** `HivePiece.java`

The entire system relies on object reference identity. The moment you serialise/deserialise (JSON for REST, or JPA persistence), reconstructed `HivePiece` objects are different instances. Every `pieceLocations.containsKey(piece)` call returns `false`. Every `isPiecePlaced` check breaks.

**Fix:** Add a unique ID field with value-based equality, or make it a record.

### 2. `isPieceMovable` doesn't handle stacked pieces

**File:** `HiveGrid.java:115`

`occupiedCoordinates.remove(coordinate)` removes the whole coordinate. But if a beetle is on top of another piece, removing the beetle shouldn't remove the coordinate — the piece underneath still occupies it. A beetle sitting on a bridge piece gets incorrectly flagged as immovable.

### 3. Queen-by-turn-4 uses `== 4`, not `>= 4`

**File:** `HiveGame.java:103`

If the queen isn't placed on turn 4 (e.g. a future pass mechanic), turns 5+ allow non-queen placements. Should be `>= 4`.

### 4. Mosquito unconditionally gets queen-bee sliding

**File:** `MosquitoMovement.java:25`

`slideAlongOneEdge` is called regardless of whether a queen is adjacent. The mosquito should only move like adjacent piece types. The loop at lines 27-36 already handles copying neighbour strategies, so line 25 is a duplicate that adds queen movement even when no queen is nearby.

---

## High

### 5. No move validation in `makeMove`

**File:** `HiveGame.java:69-71`

Accepts any `HiveMove` with zero validation. No check for correct player's turn, no check the move is in the valid set, no queen-by-turn-4 enforcement. Fine internally, but a REST API receives arbitrary moves from untrusted clients.

### 6. Mutable internals exposed through getters

**Files:** `HiveBoard.getPieceLocations()`, `HiveGrid.getGrid()`, `Player.getHand()`

All return raw mutable collections. Any caller can mutate game state without going through the APIs. In a REST context with concurrent requests, this is a data corruption vector.

**Fix:** Return `Collections.unmodifiableMap()` / `Collections.unmodifiableList()`.

### 7. `PlayerController` couples I/O to game logic

**File:** `PlayerController.java`

`chooseMove(HiveGame game)` passes the entire mutable game to the controller, blocks the thread, and drives via a `while` loop in `GameRunner`. In a REST API moves arrive asynchronously via HTTP. You need a request-response model.

### 8. `checkWinCondition` can reset a finished game to `IN_PROGRESS`

**File:** `HiveGame.java:36`

The `else` branch sets `state = IN_PROGRESS`. If called again after a game ends, it can un-end the game. Once a game finishes, the state should be immutable.

### 9. `getValidPlacementPositions` can return duplicates

**File:** `HiveGrid.java:159-203`

Two friendly pieces sharing an empty neighbour both add it. No deduplication. This inflates move lists and creates duplicate `PlacePiece` moves.

---

## Medium

### 10. Beetle climb-up/down doesn't enforce Freedom-to-Move (gate check)

**File:** `BeetleMovement.java:31-42, 57-68`

`getValidClimbUpMoves` allows climbing onto any occupied neighbour with no gate check. `getValidClimbDownMoves` allows climbing to any empty neighbour with no adjacency-to-hive check. A beetle could land somewhere disconnected.

### 11. `pieceLocations` inconsistency with stacking

**File:** `HiveBoard.java:34-43`

When a beetle moves onto another piece, both pieces have entries in `pieceLocations` pointing to the same coordinate. The bookkeeping can't reliably handle nested stacking.

### 12. `BotController` creates a new `Random()` each call

**File:** `BotController.java:23`

Not seedable, not reproducible. For testing, debugging, and replay, the `Random` should be injected or stored as a field.

### 13. No exception hierarchy

All errors throw `IllegalArgumentException` with string messages. A REST API needs structured responses. Define `InvalidMoveException`, `GameOverException`, `NotYourTurnException` etc.

### 14. Pillbug missing "last-moved" tracking

**File:** `PillbugMovement.java`

The pillbug can't move a piece that was just moved on the previous turn (official Hive rule). No tracking for this exists.

---

## Low

### 15. `System.out.println` calls (12 instances)

Replace with SLF4J before Spring.

### 16. `HumanController` is a stub

Returns `null` (permanent turn-skip). Remove or implement before Phase 2.

### 17. `EuclideanCoordinate` has no `equals`/`hashCode`/`toString`

Could be a record.

### 18. `slideAlongOneEdge` inconsistency

Ant and spider remove the piece from the grid copy before checking; queen does not. Works by geometric coincidence, but inconsistent.

### 19. No game ID

Needed for multi-game REST support.

### 20. No move history, undo, or draw-by-repetition

Can trap bots in infinite loops.

---

## Recommended fix order before Phase 2

| # | Fix | Why |
|---|---|---|
| 1 | Add ID + `equals`/`hashCode` to `HivePiece` | Serialisation breaks without this |
| 2 | Fix `isPieceMovable` for stacked pieces | Beetle movement is fundamentally broken |
| 3 | Fix mosquito's unconditional queen-slide | Incorrect Hive rules |
| 4 | Add move validation to `makeMove` | REST receives untrusted input |
| 5 | Fix `getValidPlacementPositions` duplicates | Inflates move lists |
| 6 | Return unmodifiable collections from getters | Prevent mutation |
| 7 | `checkWinCondition` — don't reset finished games | State integrity |
| 8 | Queen-by-turn-4: `== 4` to `>= 4` | Edge case correctness |
| 9 | Add domain exception hierarchy | Proper HTTP error mapping |
| 10 | Replace `System.out.println` with SLF4J | Production logging |
