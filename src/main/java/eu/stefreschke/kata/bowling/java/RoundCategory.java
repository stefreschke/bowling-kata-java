package eu.stefreschke.kata.bowling.java;

import lombok.Getter;

public enum RoundCategory {
    STRIKE(2), SPARE(1), NORMAL(0);

    @Getter
    private final int numberOfBonusRounds;

    RoundCategory(int bonusRounds) {
        this.numberOfBonusRounds = bonusRounds;
    }
}
