# API Contracts

Core Gameplay:
- POST api/games/[id]/move
    1. Client sends move via payload, optimistic render of game state afterwards
    2. Server authenticates requests and passes to controller
    3. Controller deserialises payload and routes to game service
    4. Game service calls repository to fetch Game data model from postgres
    5. Game data model is used to instantiate a HiveGame
    6. Game engine logic runs to validate the move, update game state, iterate the turn
    7. Game state is persisted to the data layer
    8. Game state and moves for next turn are serialised and sent to the client - GameStateDTO or something, need a mapper class
    9. Client renders updated game state

- GET api/games/[id]
    1. mostly same as above, but we just need to fetch and serialise game state and sent to client

- POST api/games
    1. Client form decides on what kind of game with what players 
    2. Client serialises this and sends to server
    3. Similar layered approach with Controller, Service, Engine, Repository

Auth:
- POST api/auth/register
    1. Standard register

- POST api/auth/signin
    1. standard login 

Profile:
- GET api/users/[userId]
    2. standard profile page




