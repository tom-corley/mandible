package dev.tomcorley.mandible.game_logic;

public sealed interface HiveMove permits PlacePiece, MovePiece, RemovePiece {
}