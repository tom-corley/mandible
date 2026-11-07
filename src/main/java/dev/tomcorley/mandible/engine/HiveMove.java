package dev.tomcorley.mandible.engine;

import java.util.Optional;

public sealed interface HiveMove permits PlacePiece, MovePiece, RemovePiece, PassMove {
    HiveMove invertMove();
    Optional<HexCoordinate> coordinateToLock();
}
