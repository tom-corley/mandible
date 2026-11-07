package dev.tomcorley.mandible.engine;

import java.util.Optional;

public record MovePiece(HexCoordinate from, HexCoordinate to) implements HiveMove {
    @Override
    public String toString() {
        return "Moving" + " from " + from + " to " + to;
    }

  @Override 
  public Optional<HexCoordinate> coordinateToLock() {
    return Optional.of(to);
    }

  public MovePiece invertMove() {
        return new MovePiece(to, from);
    }

}
