package com.comyr.pg18.sevenhearts;

import com.comyr.pg18.sevenhearts.game.resources.Game;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.CardNotFoundException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGame() throws CardNotFoundException {
        String p1 = new String("Pranav");
        String p2 = new String("Advait");
        String p3 = new String("Ahuja");
        Game game = new Game(p1, p2, p3);
        game.start();
    }
}