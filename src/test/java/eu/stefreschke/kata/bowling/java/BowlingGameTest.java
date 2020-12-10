package eu.stefreschke.kata.bowling.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class BowlingGameTest {

    @Test
    @DisplayName("new Game: Points are zero and Round is one")
    void newSingleBowlingGame_PointsAreZero_RoundIsOne() {
        BowlingGame bowlingGame = new BowlingGame();
        assertThat(bowlingGame.getCurrentRoundNumber()).isEqualTo(1);
        assertThat(bowlingGame.getCurrentPoints()).isZero();
    }

    @DisplayName("n pins thrown: n points")
    @ParameterizedTest(name = "{0} pins thrown: {0} points")
    @ValueSource(ints = {1, 4, 6, 8, 10})
    void newGame_throwSomePoints(int pinNumber) {
        BowlingGame bowlingGame = new BowlingGame();
        bowlingGame.newThrow(pinNumber);
        assertThat(bowlingGame.getCurrentPoints()).isEqualTo(pinNumber);
    }

    @Test
    @DisplayName("new Game: throw is available")
    void newBowlingGame_throwAvailable() {
        ThrowPinsUseCase game = new BowlingGame();
        assertThat(game.isThrowAvailable()).isTrue();
    }

    @Test
    @DisplayName("round completed: next round starts")
    void roundCompleted_newRoundStarts() {
        BowlingGame game = new BowlingGame();
        game.newThrow(5);
        game.newThrow(4);
        assertAll(
                () -> assertThat(game.remainingPins()).isEqualTo(10),
                () -> assertThat(game.getCurrentRound().isThrowAvailable()).isTrue(),
                () -> assertThat(game.getCurrentRoundNumber()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("two rounds played: first round can be accessed")
    void twoRoundsDone_accessOfFirstRoundIsPossible() {
        BowlingGame game = new BowlingGame();
        game.newThrow(5);
        game.newThrow(4);
        game.newThrow(6);
        assertAll(
                () -> assertThat(game.remainingPins()).isEqualTo(4),
                () -> assertThat(game.accessRound(1).remainingPins()).isEqualTo(1)
        );
    }

    @DisplayName("n rounds played: round n+1 is ongoing, n+2 not existing")
    @ParameterizedTest(name = "{0} played:  {0}+1 is ongoing, {0}+2 not existing")
    @ValueSource(ints = {5, 8})
    void nRoundsPlayed_accessOfRoundNPlusOneThrowsException(int numberOfRoundsPlayed) {
        BowlingGame game = new BowlingGame();
        playRounds(game, numberOfRoundsPlayed);
        Round lastRound = game.accessRound(numberOfRoundsPlayed);
        Round currentRound = game.accessRound(numberOfRoundsPlayed + 1);
        assertAll(
                () -> assertThat(lastRound.remainingPins()).isEqualTo(3),
                () -> assertThat(currentRound.remainingPins()).isEqualTo(10),
                () -> Assertions.assertThrows(IllegalArgumentException.class,
                        () -> game.accessRound(numberOfRoundsPlayed + 2))
        );
    }

    @Test
    @DisplayName("8 rounds played: next round is not the last round")
    void eightRoundsPlayed_nextRoundIsLastRound() {
        BowlingGame game = new BowlingGame();
        playRounds(game, 8);
        assertThat(game.isInLastRound()).isFalse();
    }

    @Test
    @DisplayName("9 rounds played: next round is last round")
    void nineRoundsPlayed_nextRoundIsLastRound() {
        BowlingGame game = new BowlingGame();
        playRounds(game, 9);
        assertThat(game.isInLastRound()).isTrue();
    }

    @Test
    void lastRoundPlayed_cannotPlayOn() {
        BowlingGame game = new BowlingGame();
        playRounds(game, 10);
        assertThat(game.isThrowAvailable()).isFalse();
    }

    @Test
    void lastRoundPlayed_GameIsMarkedFinished() {
        BowlingGame game = new BowlingGame();
        playRounds(game, 10);
        assertThat(game.isFinished()).isTrue();
    }

    @Test
    void nineRoundsPlayed_GameIsNotMarkedFinished() {
        BowlingGame game = new BowlingGame();
        playRounds(game, 9);
        assertThat(game.isFinished()).isFalse();
    }

    private void playRounds(BowlingGame game, int i) {
        IntStream.range(1, i + 1).forEach(simulatedPlayingOneRound(game));
    }

    private IntConsumer simulatedPlayingOneRound(BowlingGame game) {
        return roundNumber -> {
            game.newThrow(4);
            game.newThrow(3);
        };
    }
}