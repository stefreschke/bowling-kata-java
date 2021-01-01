package eu.stefreschke.kata.bowling.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("rounds 1 and 2: 2 holds reference to 1")
    void roundKnowsAboutPreviousRound() {
        Round round = new Round();
        Optional<Round> previousRound = round.getPrevious();
        assertThat(previousRound).isEmpty();
    }

    @Test
    @DisplayName("2 rounds, strike on 1st: both throws of round 2 are doubled")
    void roundAfterStrike_previousRoundDoublesAllPoints() {
        Round first = new Round();
        Round second = new Round(first);
        first.newThrow(10);
        second.newThrow(5);
        second.newThrow(5);
        assertThat(first.totalPoints()).isEqualTo(20);
    }

    @Test
    @DisplayName("3 rounds, strike on 1st: both throws round 2 doubled, round 3 singled")
    void roundsAfterStrike_twoNextThrowsAreDoubled() {
        Round first = new Round();
        Round second = new Round(first);
        Round third = new Round(second);
        first.newThrow(10);
        second.newThrow(7);
        second.newThrow(2);
        third.newThrow(3);
        assertAll(
                () -> assertThat(first.totalPoints()).isEqualTo(19),
                () -> assertThat(second.totalPoints()).isEqualTo(9),
                () -> assertThat(third.totalPoints()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("2 rounds, spare on 1st: first throw on 2nd is doubled, second throw normal")
    void spareFirstRound_onlyFirstThrowOfRoundTwoDoubled() {
        Round first = new Round();
        Round second = new Round(first);
        first.newThrow(5);
        first.newThrow(5);
        second.newThrow(7);
        second.newThrow(3);
        assertAll(
                () -> assertThat(first.totalPoints()).isEqualTo(17),
                () -> assertThat(second.totalPoints()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("last round: is marked as last round")
    void round_canBeSetAsLastRound() {
        Round round = new Round();
        round.setAsLastRound();
        assertThat(round.isLastRound()).isTrue();
    }

    @Test
    @DisplayName("new Round: not marked as last round")
    void round_notMarkedAsLastRound() {
        Round round = new Round();
        assertThat(round.isLastRound()).isFalse();
    }

    @Test
    @DisplayName("last round: points are counted normally")
    void lastRound_PointsAreCountedNormally() {
        Round round = new Round();
        round.setAsLastRound();
        round.newThrow(5);
        round.newThrow(5);
        assertThat(round.totalPoints()).isEqualTo(10);
    }

    @Test
    @DisplayName("last round: spare, can throw on")
    void lastRound_CanThrowOnAfterSpare() {
        Round round = new Round();
        round.setAsLastRound();
        round.newThrow(5);
        round.newThrow(5);
        assertDoesNotThrow(() -> round.newThrow(5));
    }

    @Test
    @DisplayName("last round: spare + 2 throws, exception thrown")
    void lastRound_cannotThrowOnAfterOneThrowAfterSpareInLastRow() {
        Round round = new Round();
        round.setAsLastRound();
        round.newThrow(5);
        round.newThrow(5);
        round.newThrow(5);
        assertThrows(Round.ThrewOnFinishedRoundException.class, () -> round.newThrow(5));
    }

    @Test
    @DisplayName("last round: three strikes possible")
    void lastRound_canThrowThreeStrikesInLastRound() {
        Round round = new Round();
        round.setAsLastRound();
        round.newThrow(10);
        round.newThrow(10);
        round.newThrow(10);
        assertThat(round.isThrowAvailable()).isFalse();
    }

    @Test
    @DisplayName("last round: strike-spare, cannot throw on after")
    void lastRound_cannotThrowOnAfterStrikeSpare() {
        Round round = new Round();
        round.setAsLastRound();
        round.newThrow(10);
        round.newThrow(5);
        round.newThrow(5);
        assertThat(round.isThrowAvailable()).isFalse();
    }
}