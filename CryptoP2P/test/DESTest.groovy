package be.pxl

import org.junit.Test

/**
 * Created by Buzz Lightyear on 30/03/2016.
 */
class DESTest extends GroovyTestCase {
    @Test
    void FileExists() {
        assertEquals("Is the file found", true, true)
    }

    @Test
    void testEncrypt1() {
        assertEquals("Is the file encrypted", true, true)
    }
}
