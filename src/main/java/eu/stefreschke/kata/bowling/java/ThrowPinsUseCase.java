package eu.stefreschke.kata.bowling.java;

public interface ThrowPinsUseCase {
    default void newThrow(int numberOfPins) {
        this.newThrow(new Throw(numberOfPins));
    }

    int remainingPins();

    boolean isThrowAvailable();

    void newThrow(Throw newThrow);
}
