package dev.tomcorley.mandible.engine;

import java.util.Optional;

public record PassMove() implements HiveMove {
   @Override
   public HiveMove invertMove() {
    return this;
   }

  @Override 
  public Optional<HexCoordinate> coordinateToLock() {
    return Optional.empty();
    }

  @Override 
  public String toString() {
    return "Pass";
  }
}
