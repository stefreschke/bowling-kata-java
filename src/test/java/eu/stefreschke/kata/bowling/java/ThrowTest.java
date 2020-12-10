package eu.stefreschke.kata.bowling.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ThrowTest {

    @DisplayName("throw invalid number of Pins: Throws Exception")
    @ParameterizedTest(name = "{0} pins thrown: Exception is thrown")
    @ValueSource(ints = {-1, 11})
    void invalidNumberOfPinsThrown_throwException(int numberOfPinsThrown) {
        Assertions.assertThrows(Throw.InvalidNumberOfPinsThrownException.class, () ->
                new Throw(numberOfPinsThrown));
    }

}