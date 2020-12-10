package eu.stefreschke.kata.bowling.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoundTest {
    @Test
    @DisplayName("new Round: can throw pins on this round")
    void newRound_throwIsAvailable() {
        ThrowPinsUseCase round = new Round();
        assertThat(round.isThrowAvailable()).isTrue();
    }

    @Test
    @DisplayName("new Round: cannot throw more than twice")
    void newRound_cannotThrowMoreThanTwice() {
        ThrowPinsUseCase round = new Round();
        round.newThrow(5);
        round.newThrow(5);
        Assertions.assertThrows(Round.ThrewOnFinishedRoundException.class, () -> round.newThrow(5));
    }

    @Test
    @DisplayName("new Round: cannot throw more than twice")
    void roundAfterToThrows_noMoreThrowsAvailable() {
        ThrowPinsUseCase round = new Round();
        round.newThrow(5);
        round.newThrow(5);
        assertThat(round.isThrowAvailable()).isFalse();
    }

    @DisplayName("First throw with n pins: Second throw has 10-n pins standing")
    @ParameterizedTest(name = "first {0} pins: second has 10-{0} pins")
    @ValueSource(ints = {0, 1, 9, 10})
    void afterNPinsThrown_TenMinusNPinsStanding(int firstThrow) {
        Round round = new Round();
        round.newThrow(firstThrow);
        int remainingPins = round.remainingPins();
        assertThat(remainingPins).isEqualTo(10 - firstThrow);
    }

    @Test
    @DisplayName("throw 5, than 6: exception is thrown")
    void firstThrow5Pins() {
        Round round = new Round();
        round.newThrow(5);
        Assertions.assertThrows(Round.ThrewMorePinsThanPossibleException.class, () ->
                round.newThrow(6));
    }

    @Test
    @DisplayName("Strike: cannot throw anymore")
    void strike_RoundHasFinished() {
        Round round = new Round();
        round.newThrow(10);
        assertThat(round.isThrowAvailable()).isFalse();
    }

    @Test
    @DisplayName("strike: round is labeled a strike")
    void strike_roundIsLabeledAsStrike() {
        Round round = new Round();
        round.newThrow(10);
        assertThat(round.category()).isEqualTo(RoundCategory.STRIKE);
    }

    @Test
    @DisplayName("strike: round is labeled a spare")
    void spare_roundIsLabeledAsSpare() {
        Round round = new Round();
        round.newThrow(6);
        round.newThrow(4);
        assertThat(round.category()).isEqualTo(RoundCategory.SPARE);
    }

    @Test
    @DisplayName("spared with 6+4: bonus points 0, total points 10")
    void finishedSingleRound_hasNoBonusPoints() {
        Round round = new Round();
        round.newThrow(6);
        round.newThrow(4);
        assertAll(
                () -> assertThat(round.getBonusPoints()).isZero(),
                () -> assertThat(round.totalPoints()).isEqualTo(10)
        );
    }

    @Test
    void roundKnowsAboutPreviousRound() {
        Round round = new Round();
        Optional<Round> previousRound = round.getPrevious();
        assertThat(previousRound).isEmpty();
    }
}