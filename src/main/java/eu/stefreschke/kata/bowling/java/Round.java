package eu.stefreschke.kata.bowling.java;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

public class Round implements ThrowPinsUseCase {
    private final Round previous;
    private int availableThrows = 2;
    private int remainingPins = 10;
    private int bonusCounter = 0;
    @Getter
    private int bonusPoints = 0;

    private RoundCategory roundCategory = RoundCategory.NORMAL;
    private boolean lastRound = false;

    public Round() {
        this.previous = null;
    }

    public Round(@NonNull Round previous) {
        this.previous = previous;
    }

    @Override
    public int remainingPins() {
        return remainingPins;
    }

    @Override
    public boolean isThrowAvailable() {
        return availableThrows > 0;
    }

    @Override
    public void newThrow(Throw newThrow) {
        assertStillAbleToThrowThisRound();
        int numberOfPinsThrown = newThrow.getNumberOfPins();
        assertNumberOfPinsThrownIsValid(numberOfPinsThrown);
        countThrow(numberOfPinsThrown);
    }

    private void countThrow(int numberOfPinsThrown) {
        availableThrows = calculateRemainingThrows(numberOfPinsThrown);
        remainingPins -= numberOfPinsThrown;
        if (previous != null) {
            previous.notifyAboutThrow(numberOfPinsThrown);
        }
    }

    private void notifyAboutThrow(int numberOfPinsThrown) {
        if (bonusCounter > 0) {
            bonusPoints += numberOfPinsThrown;
            if (previous != null) {
                previous.notifyAboutThrow(numberOfPinsThrown);
            }
        }
        bonusCounter--;
    }

    private int calculateRemainingThrows(int numberOfPinsThrown) {
        if (numberOfPinsThrown == remainingPins) {
            markRoundAsStrikeOrSpare(numberOfPinsThrown);
            setBonusCounterAcordingly();
            return 0;
        } else {
            return availableThrows - 1;
        }
    }

    private void setBonusCounterAcordingly() {
        bonusCounter = roundCategory.getNumberOfBonusRounds();
    }

    private void markRoundAsStrikeOrSpare(int numberOfPinsThrown) {
        if (numberOfPinsThrown == 10) {
            roundCategory = RoundCategory.STRIKE;
        } else {
            roundCategory = RoundCategory.SPARE;
        }
    }

    private void assertNumberOfPinsThrownIsValid(int numberOfPinsThrown) {
        if (numberOfPinsThrown > remainingPins) {
            throw new ThrewMorePinsThanPossibleException("Cannot throw " + numberOfPinsThrown +
                    " pins if only " + remainingPins + " pins available");
        }
    }

    private void assertStillAbleToThrowThisRound() {
        if (availableThrows <= 0) {
            throw new ThrewOnFinishedRoundException("Cannot throw on completed round");
        }
    }

    public RoundCategory category() {
        return roundCategory;
    }

    public int totalPoints() {
        return 10 - remainingPins + bonusPoints;
    }

    public Optional<Round> getPrevious() {
        if (previous == null) {
            return Optional.empty();
        } else {
            return Optional.of(previous);
        }
    }

    public void setAsLastRound() {
        this.lastRound = true;

    }

    public boolean isLastRound() {
        return lastRound;
    }

    public static class ThrewOnFinishedRoundException extends RuntimeException {
        public ThrewOnFinishedRoundException(String message) {
            super(message);
        }
    }

    public static class ThrewMorePinsThanPossibleException extends RuntimeException {
        public ThrewMorePinsThanPossibleException(String message) {
            super(message);
        }
    }
}
