package eu.stefreschke.kata.bowling.java;

import lombok.Getter;

@Getter
public class Throw {
    private final int numberOfPins;

    public Throw(int numberOfPins) {
        assertPinNumberIsInRange(numberOfPins);
        this.numberOfPins = numberOfPins;
    }

    private void assertPinNumberIsInRange(int pinNumber) {
        if (pinNumber > 10 || pinNumber < 0) {
            throw new Throw.InvalidNumberOfPinsThrownException("Number of pins thrown cannot " +
                    "exceed 10");
        }
    }

    public static class InvalidNumberOfPinsThrownException extends RuntimeException {
        public InvalidNumberOfPinsThrownException(String message) {
            super(message);
        }
    }
}
