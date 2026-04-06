# Hive Backend

- This repo is going to be a backend to service a application around the board game hive. See ___ for a concise summary of the rules.

The backend is going to be a Spring Boot application. We wish to implement a layered backend: 
- Security / Filter Chains / Middleware
- Controllers
- Services and Models / Entities
- Repository with JPA

Main challenges:
- How to represent a board state efficiently, and how to represent a board that evolves in shape dynamically.
- How to do analytics on moves
- Worth trying GraphQL protocol
- Containerisation and Deployment?

Think about a frontend later - It would be really nice to make a mobile app eventually, but we will see. Maybe just a web app, 

Some machine learning element would be really nice. Training a Hive bot somehow?

# Object Modelling

Game
- A game is probably an instance of some high level game object

Logging
- Spring boot standard logging

Board
- The board is purely a reflection of the pieces placed, there is no consistent structure
- Hexial Grid, arguably a graph, with 6-regular interior points (pieces are placed on these)
- Exterior points (a piece could feasibly interact with this grid point in the next turn)
- Edge cases here, e.g a ring of 6 bugs would surround an exterior point (which would have valency 6)
- Arguably if we fix the first white piece as our origin, everything becomes a traversal relative to this, thanks to one hive
- Technically the board is 3d, how do we represent this without complicating the graph too much, each node is a linked list?, we only really need to know about the top one? and whether there is more than one?

Game Mechanics
- End: Check the valency of the queens, if at least 1 has 6, there is either a win, draw or a loss
- Stalemate: same move 3 times? or how does that work
- Locking out: hasValidMove utility?
- Geometric obstacle - how do we define that abstractly

Bugs:
- Queen Bee - Queen
- Ladybug - Lady
- Grasshopper - Hopatron
- Spider - Spidos
- Pillbug - Pilly
- Ant - Anton
- Beetle - Beetaloid
- Mosquito - Mosquite

# Data Layer
- A game can be represented as a history of moves in theory, form which a board can be reconstructed?
- How we translate between game engine (connected graph), and tables for SQL representation, then is undoing and redoing moves just popping and adding moves from a stack?
- How do we store games
- We should also store users
- Do we have some concept of friends, messages, invites

# Frontend
- React web frontend would be good, shouldn't overcomplicate early on
- Pages, early on something like /game/{uuid} for a game
- Later we can have a home page
- Puzzles
- Analytics etc