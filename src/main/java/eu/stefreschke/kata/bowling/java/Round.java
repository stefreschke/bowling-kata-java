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
        availableThrows = calculateRemainingThrows(numberOfPinsThrown);
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

    private int calculateRemainingThrows(int numberOfPinsThrown) {
        if (numberOfPinsThrown == remainingPins) {
            markRoundAsStrikeOrSpare(numberOfPinsThrown);
            if (isLastRound()) {
                return remainingThrowsForSpecialThrowsInLastRound();
            } else {
                setBonusCounterAcordingly();
                return 0;
            }
        } else {
            return availableThrows - 1;
        }
    }

    private int remainingThrowsForSpecialThrowsInLastRound() {
        if (bonusThrowsAvailable == 0) {
            return givenExtraThrows();
        } else {
            return --bonusThrowsAvailable;
        }
    }

    private int givenExtraThrows() {
        updateSpecialThrowsGivenBasedOnRoundCategory();
        return bonusThrowsAvailable;
    }

    private void updateSpecialThrowsGivenBasedOnRoundCategory() {
        bonusThrowsAvailable = roundCategory.getNumberOfBonusRounds();
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
