package eu.stefreschke.kata.bowling.java;

import lombok.Getter;

public class MultiplayerBowlingGame {
    @Getter
    private final int numberOfPlayers;

    public MultiplayerBowlingGame(int numberOfPlayers) {
        if (numberOfPlayers < 1) {
            throw new InvalidNumberOfPlayersException("Number Of Players must be greater than " +
                    "zero");
        }
        this.numberOfPlayers = numberOfPlayers;
    }

    public MultiplayerBowlingGame() {
        this(1);
    }

    public static class InvalidNumberOfPlayersException extends RuntimeException {

        public InvalidNumberOfPlayersException(String message) {
            super(message);
        }
    }
}
