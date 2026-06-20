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
- Proper spring boot layering with filter chains, controllers, services and repositories
- Sensible use of design patterns

Think about a frontend later - It would be really nice to make a mobile app eventually, but we will see. Maybe just a web app, 

Some machine learning element would be really nice. Training a Hive bot somehow?
