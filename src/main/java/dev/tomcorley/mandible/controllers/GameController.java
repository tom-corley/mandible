package dev.tomcorley.mandible.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import dev.tomcorley.mandible.dto.GameStateDTO;
import dev.tomcorley.mandible.dto.GameCreationDTO;
import dev.tomcorley.mandible.dto.MoveDTO;

@RestController
@RequestMapping("/api/games")
public class GameController {
    // private final GameService gameService;

    @GetMapping("/{id}")
    public ResponseEntity<GameStateDTO> getGame(@PathVariable String id) {
        return ResponseEntity.ok(new GameStateDTO(id));
    }

    @PostMapping()
    public ResponseEntity<GameStateDTO> createGame(@RequestBody GameCreationDTO gameCreationDTO) {
        return ResponseEntity.ok(new GameStateDTO(gameCreationDTO.getId()));
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameStateDTO> move(@PathVariable String id, @RequestBody MoveDTO moveDTO) {
        return ResponseEntity.ok(new GameStateDTO(id));
    }
}
