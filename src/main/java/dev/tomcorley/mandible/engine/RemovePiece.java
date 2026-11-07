package dev.tomcorley.mandible.engine;

import java.util.Optional;

public record RemovePiece(HexCoordinate position) implements HiveMove {
    @Override
    public String toString() {
        return "Removing top piece from stack at " + position;
    }
  

  @Override 
  public Optional<HexCoordinate> coordinateToLock() {
    return Optional.empty();
    }

    public HiveMove invertMove() {
        throw new UnsupportedOperationException("Cannot invert a remove piece move");
    }
}
