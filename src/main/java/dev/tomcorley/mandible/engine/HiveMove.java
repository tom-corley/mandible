package dev.tomcorley.mandible.engine;

public sealed interface HiveMove permits PlacePiece, MovePiece, RemovePiece {
    HiveMove invertMove();
}