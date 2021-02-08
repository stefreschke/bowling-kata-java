package eu.stefreschke.kata.bowling.java;

import lombok.Getter;

public enum RoundCategory {
    STRIKE(2), SPARE(1), NORMAL(0);

    @Getter
    private final int numberOfBonusRounds;

    RoundCategory(int bonusRounds) {
        this.numberOfBonusRounds = bonusRounds;
    }

    public static RoundCategory given(Round round, int pinsThrown) {
        int remainingPins = round.remainingPins();
        boolean roundIsSpecial = remainingPins == pinsThrown;
        if (roundIsSpecial) {
            if (pinsThrown == 10) {
                return STRIKE;
            } else {
                return SPARE;
            }
        } else {
            return NORMAL;
        }
    }
}
