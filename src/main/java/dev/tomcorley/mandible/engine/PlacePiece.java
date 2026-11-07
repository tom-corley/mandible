package dev.tomcorley.mandible.engine;

import java.util.Optional;

public record PlacePiece(HexCoordinate position, HivePiece piece) implements HiveMove {
    @Override
    public String toString() {
        return "Placing " + piece + " at " + position;
    }
  
  @Override 
  public Optional<HexCoordinate> coordinateToLock() {
    return Optional.of(position);
    }

    public RemovePiece invertMove() {
        return new RemovePiece(position);
    }
}
