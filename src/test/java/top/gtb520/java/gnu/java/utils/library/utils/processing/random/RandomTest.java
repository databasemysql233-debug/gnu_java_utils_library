package top.gtb520.java.gnu.java.utils.library.utils.processing.random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomTest {

    @Test
    void randomWithinRange() {
        for (int i = 0; i < 1000; i++) {
            int result = defaults.random(1, 10);
            assertTrue(result >= 1 && result <= 10, "随机数应在[1,10]范围内: " + result);
        }
    }

    @Test
    void randomSameStartAndEnd() {
        assertEquals(5, defaults.random(5, 5));
    }

    @Test
    void randomNegativeRange() {
        for (int i = 0; i < 1000; i++) {
            int result = defaults.random(-10, -1);
            assertTrue(result >= -10 && result <= -1);
        }
    }

    @Test
    void randomMaxValueBoundaryDoesNotOverflow() {
        // 上界为 Integer.MAX_VALUE 时 +1 不再溢出
        for (int i = 0; i < 1000; i++) {
            int result = defaults.random(0, Integer.MAX_VALUE);
            assertTrue(result >= 0);
        }
        int result = defaults.random(Integer.MAX_VALUE - 10, Integer.MAX_VALUE);
        assertTrue(result >= Integer.MAX_VALUE - 10 && result <= Integer.MAX_VALUE);
    }

    @Test
    void randomReversedRangeThrows() {
        assertThrows(IllegalArgumentException.class, () -> defaults.random(10, 1));
    }

    @Test
    void randomStringLengthAndCharset() {
        String result = defaults.randomString(16);
        assertEquals(16, result.length());
        assertTrue(result.matches("[A-Za-z0-9]{16}"), "应只包含大小写字母和数字: " + result);
    }

    @Test
    void randomStringZeroLength() {
        assertEquals("", defaults.randomString(0));
    }

    @Test
    void randomStringNegativeLengthThrows() {
        assertThrows(IllegalArgumentException.class, () -> defaults.randomString(-1));
    }
}
