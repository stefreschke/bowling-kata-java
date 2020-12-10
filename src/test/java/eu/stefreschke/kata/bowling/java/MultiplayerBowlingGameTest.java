package eu.stefreschke.kata.bowling.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MultiplayerBowlingGameTest {
    @Test
    @DisplayName("new Game: has at least one player")
    void newGame_onePlayer() {
        MultiplayerBowlingGame multiplayerBowlingGame = new MultiplayerBowlingGame();
        assertThat(multiplayerBowlingGame.getNumberOfPlayers()).isPositive();
    }

    @ParameterizedTest(name = "{0} players: Game has {0} players")
    @DisplayName("new Game with n players: Game has n players")
    @ValueSource(ints = {1, 2, 3, 4})
    void newGame_moreThanOnePlayerPossible(int numberOfPlayers) {
        MultiplayerBowlingGame multiplayerBowlingGame = new MultiplayerBowlingGame(numberOfPlayers);
        assertThat(multiplayerBowlingGame.getNumberOfPlayers()).isEqualTo(numberOfPlayers);
    }

    @ParameterizedTest(name = "{0} players: Game creation throws Exception")
    @DisplayName("new Game with n players: Game throws Exception")
    @ValueSource(ints = {-2, -1, 0})
    void newGameWithLessThanOnePlayer_ExceptionIsThrown(int numberOfPlayers) {
        Assertions.assertThrows(MultiplayerBowlingGame.InvalidNumberOfPlayersException.class, () ->
                new MultiplayerBowlingGame(numberOfPlayers));
    }

}
