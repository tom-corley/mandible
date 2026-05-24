# REST API Architecture — Engine to Spring

## The Core Question

How does a synchronous game engine (with a `while` loop driving turns) map to an async, request-driven REST API?

## Short Answer

**It doesn't — and it doesn't need to.** `GameRunner` is a console simulation harness. In a REST API, the client drives the game loop by making requests. Each request is one turn. The engine classes (`HiveGame`, `HiveBoard`, pieces, moves) survive unchanged — only the orchestration layer changes.

## What Survives Into Phase 2

| Component | Role in REST API |
|-----------|-----------------|
| `HiveGame` | Core state container — loaded per request, mutated, persisted |
| `HiveBoard` / `HiveGrid` | Board logic — unchanged |
| All piece types | Move validation — unchanged |
| `HiveMove` / `PlacePiece` / `MovePiece` | Command objects — deserialized from request body |
| `HandFactory` | Game creation — unchanged |
| Move validation logic | Called by service layer before applying moves |

## What Doesn't Survive

| Component | Why |
|-----------|-----|
| `GameRunner` | The `while (IN_PROGRESS)` loop has no REST equivalent. The client drives the loop. |
| `PlayerController` / `BotController` | The HTTP request *is* the player controller. Bot play could return as an AI opponent service, but not as a loop participant. |
| `HumanController` | Was already a stub. REST endpoints replace it entirely. |

## The Request Lifecycle

A single move in the REST API:

```
POST /api/games/{gameId}/moves
{ "type": "PLACE", "piece": "ANT_1", "coordinate": { "q": 0, "r": 1 } }
```

1. **Load** game state from persistence
2. **Validate** it's the requesting player's turn
3. **Validate** the move is legal (`getValidMovesForPlayer`)
4. **Apply** the move (`game.makeMove(move)`)
5. **Check** win condition (`game.checkWinCondition()`)
6. **Persist** updated state
7. **Return** response (new board state, game status, next player)

No loop. No polling. One request, one move, one response.

## Persistence Strategy Tradeoffs

### Option A: Event Sourcing (store moves, replay to reconstruct)

Store each move as a row. On every request, create a fresh `HiveGame` and replay all moves to get current state.

**Pros:**
- Complete move history is the primary data — great for replays, analysis, undo
- `HiveMove` is already a clean command object, fits naturally
- No serialization complexity for board state
- Auditable — you can verify any game state by replaying from scratch
- Strong interview talking point (event sourcing is a well-regarded pattern)

**Cons:**
- Gets slower as games get longer (replay cost scales linearly with move count)
- Needs snapshotting to stay performant at scale (checkpoint every N moves)
- Replay must be deterministic — any engine change that alters move semantics breaks old games
- More complex to query "current state" (it's derived, not stored)

**Mitigations:**
- Hive games are short (~30-60 moves). Replay cost is negligible for this scale.
- Snapshotting is a good stretch goal, not a launch requirement.

### Option B: Snapshot (serialize full game state per row)

Serialize `HiveGame` (board, hands, turn count, current player) into a JSON/binary column. Load, deserialize, mutate, re-serialize.

**Pros:**
- Fast reads — current state is always directly available
- Simple mental model — load, change, save
- Easy to query ("show me all games where white has won")
- Familiar to most reviewers (standard CRUD)

**Cons:**
- Lose move history unless stored separately (can add a moves table alongside)
- Serialization/deserialization of `HiveGame` requires careful design (custom serializers, handling object graphs)
- Board state serialization is non-trivial: `Deque<HivePiece>` stacks, coordinate maps, piece references shared between board and hands
- Schema migrations are harder if the engine model changes

**Mitigations:**
- Adding a separate `moves` table gives you history without the replay cost
- Jackson custom serializers handle the object graph, just need upfront work

### Option C: Hybrid (snapshot + move log)

Store both: a snapshot of current state for fast reads, plus a move log for history and auditability.

**Pros:**
- Fast reads (snapshot) + full history (move log)
- Best of both worlds for a portfolio piece — can talk about both patterns
- Move log is append-only (simple, performant writes)

**Cons:**
- Two sources of truth to keep in sync
- More storage (though negligible at this scale)
- More tables and persistence logic to maintain

## Recommendation

**Option A (event sourcing) for this project.** Reasons:

1. Hive games are short enough that replay cost doesn't matter
2. `HiveMove` is already a well-defined command object — the hard part is done
3. It's the more interesting interview conversation
4. It avoids the serialization complexity of snapshotting `HiveBoard`'s internal data structures
5. Move history comes for free — useful for game review, analytics stretch goals

If performance becomes a concern (it won't at portfolio scale), snapshotting is a clean optimisation to add later.

## Open Questions

- **Game creation and lobby:** How do two players find each other? Simple game codes? Matchmaking is out of scope, but the create/join flow needs designing.
- **Authentication:** Token-based (JWT) or session-based? Needed to verify "is it actually this player's turn?"
- **Real-time updates:** REST is request/response. Does the opponent poll for state, or do we add WebSocket/SSE for push notifications when a move is made? Polling is simpler for MVP.
- **Bot play in REST context:** If a player plays against a bot, does the server immediately compute and apply the bot's response move within the same request? Or is it a separate async step?
