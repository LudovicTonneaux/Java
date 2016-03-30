package be.pxl;

import static org.junit.Assert.assertEquals;

/**
 * Created by Samy Coenen on 30/03/2016.
 */
public class Test {
    @Test
    public void multiplicationOfZeroIntegersShouldReturnZero() {

        // MyClass is tested


        // assert statements
        assertEquals("10 x 0 must be 0", 0, 10 * 0);
        assertEquals("0 x 10 must be 0", 0, 0 * 10);
        assertEquals("0 x 0 must be 0", 0, 0 * 0);
    }
}
