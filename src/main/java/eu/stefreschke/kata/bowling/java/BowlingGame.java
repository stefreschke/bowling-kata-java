package eu.stefreschke.kata.bowling.java;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class BowlingGame implements ThrowPinsUseCase {
    @Getter
    private int currentRoundNumber;
    private boolean isFinished;
    @Getter
    private final List<Round> rounds;
    @Getter
    private Round currentRound;

    public BowlingGame() {
        this.rounds = new ArrayList<>();
        this.currentRound = new Round();
        this.rounds.add(currentRound);
        this.currentRoundNumber = 1;
    }

    @Override
    public int remainingPins() {
        return currentRound.remainingPins();
    }

    @Override
    public boolean isThrowAvailable() {
        return currentRound.isThrowAvailable();
    }

    @Override
    public void newThrow(Throw newThrow) {
        int numberOfPins = newThrow.getNumberOfPins();
        currentRound.newThrow(numberOfPins);
        if (!currentRound.isThrowAvailable()) {
            updateInternalState();
        }

    }

    private void updateInternalState() {
        if (isInLastRound()) {
            isFinished = true;
        } else {
            moveToNextRound();
        }

    }

    private void moveToNextRound() {
        currentRound = new Round(currentRound);
        rounds.add(currentRound);
        currentRoundNumber += 1;
    }

    public Round accessRound(int i) {
        try {
            return rounds.get(i - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Round " + i + " was not played, currently playing" +
                    " round " + currentRoundNumber);
        }
    }

    public boolean isInLastRound() {
        return currentRoundNumber == 10;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int getCurrentPoints() {
        return rounds.stream()
                .map(Round::totalPoints)
                .reduce(0, Integer::sum);
    }
}
