package eu.stefreschke.kata.bowling.java;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

public class Round implements ThrowPinsUseCase {
    private final Round previous;
    private int availableThrows = 2;
    private int remainingPins = 10;
    private int bonusCounter = 0;
    private int pointsScored = 0;
    private int bonusThrowsAvailable = 0;
    private int bonusThrowsGiven = 0;

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
        notifyPreviousRoundIfPresent(numberOfPinsThrown);
        updateRemainingThrows(numberOfPinsThrown);
        updateNumberOfRemainingPins(numberOfPinsThrown);
        pointsScored += numberOfPinsThrown;
    }

    private void updateNumberOfRemainingPins(int numberOfPinsThrown) {
        if (isLastRound() && roundCategory != RoundCategory.NORMAL) {
            resetRemainingPinsAndRoundCategory();
        } else {
            remainingPins -= numberOfPinsThrown;
        }
    }

    private void resetRemainingPinsAndRoundCategory() {
        remainingPins = 10;
        roundCategory = RoundCategory.NORMAL;
    }

    private void notifyAboutThrow(int pinsThrown) {
        // method is invoked by other instances of Round
        // this is hacky, but 'private' allows it, so why not...
        if (bonusCounter > 0) {
            bonusPoints += pinsThrown;
            notifyPreviousRoundIfPresent(pinsThrown);
        }
        bonusCounter--;
    }

    private void notifyPreviousRoundIfPresent(int numberOfPinsThrown) {
        if (previous != null) {
            previous.notifyAboutThrow(numberOfPinsThrown);
        }
    }

    private void updateRemainingThrows(int numberOfPinsThrown) {
        boolean roundIsSpecial = numberOfPinsThrown == remainingPins;
        if (roundIsSpecial) {
            updateRoundCategory(numberOfPinsThrown);
            if (isLastRound()) {
                updateBonusThrows();
                availableThrows =  bonusThrowsAvailable;
            } else {
                updateBonusCounterAccordingly();
                availableThrows = 0;
            }
        } else {
            availableThrows = availableThrows - 1;
        }
    }

    private void updateBonusThrows() {
        if (bonusThrowsGiven == 0) {
            updateSpecialThrowsGivenBasedOnRoundCategory();
        } else {
            --bonusThrowsAvailable;
        }
    }

    private void updateSpecialThrowsGivenBasedOnRoundCategory() {
        int numberOfBonusThrows = roundCategory.getNumberOfBonusRounds();
        bonusThrowsAvailable = numberOfBonusThrows;
        bonusThrowsGiven = numberOfBonusThrows;
    }

    private void updateBonusCounterAccordingly() {
        bonusCounter = roundCategory.getNumberOfBonusRounds();
    }

    private void updateRoundCategory(int numberOfPinsThrown) {
        roundCategory = RoundCategory.given(this, numberOfPinsThrown);
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
        return pointsScored + bonusPoints;
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
