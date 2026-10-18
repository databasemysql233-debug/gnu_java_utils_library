package top.gtb520.java.gnu.java.utils.library.utils.print;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EchoTest {

    /**
     * 捕获 echo 工具类打印到 System.out 的内容
     */
    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(original);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    // ==================== ColorPrintln ====================

    @Test
    void colorPrintlnUsesAnsiCode() {
        String out = capture(() -> echo.ColorPrintln("x", ColorNameEnum.RED));
        assertTrue(out.contains("\033[31m"));
        assertTrue(out.contains("x"));
        assertTrue(out.contains("\033[0m"));
    }

    @Test
    void colorPrintlnNullEnumFallsBackToPlain() {
        String out = capture(() -> echo.ColorPrintln("plain", null));
        assertTrue(out.contains("plain"));
        assertFalse(out.contains("\033["));
    }

    // ==================== ColorHexPrintln ====================

    @Test
    void hexColorRRGGBB() {
        String out = capture(() -> echo.ColorHexPrintln("x", "#FFC0CB"));
        assertTrue(out.contains("\033[38;2;255;192;203m"));
    }

    @Test
    void hexColorShorthand() {
        String out = capture(() -> echo.ColorHexPrintln("x", "#F0F"));
        assertTrue(out.contains("\033[38;2;255;0;255m"));
    }

    @Test
    void ansiCodePassthrough() {
        String out = capture(() -> echo.ColorHexPrintln("x", "38;5;208"));
        assertTrue(out.contains("\033[38;5;208m"));
    }

    @Test
    void invalidHexThrows() {
        assertThrows(IllegalArgumentException.class, () -> echo.ColorHexPrintln("x", "#GGGGGG"));
        assertThrows(IllegalArgumentException.class, () -> echo.ColorHexPrintln("x", "#12345"));
    }

    @Test
    void nullColorThrows() {
        assertThrows(IllegalArgumentException.class, () -> echo.ColorHexPrintln("x", null));
    }

    @Test
    void maliciousAnsiCodeThrows() {
        // 防止注入任意转义序列
        assertThrows(IllegalArgumentException.class, () -> echo.ColorHexPrintln("x", "31;0m\u001b[2J"));
    }

    // ==================== ColorPrintlnPlus ====================

    @Test
    void plusNullInputPrintsEmptyLine() {
        String out = capture(() -> echo.ColorPrintlnPlus(null));
        assertEquals(System.lineSeparator(), out);
    }

    @Test
    void plusSingleColorCode() {
        String out = capture(() -> echo.ColorPrintlnPlus("&4Red Text"));
        assertTrue(out.contains("\033[31m"));
        assertTrue(out.contains("Red Text"));
        assertFalse(out.contains("&4"));
    }

    @Test
    void plusMultipleColorCodes() {
        String out = capture(() -> echo.ColorPrintlnPlus("&6Welcome &2to &4Project"));
        assertTrue(out.contains("\033[38;5;214m"));
        assertTrue(out.contains("\033[32m"));
        assertTrue(out.contains("\033[31m"));
        assertTrue(out.contains("Welcome"));
        assertTrue(out.contains("to"));
        assertTrue(out.contains("Project"));
    }

    @Test
    void plusStacksColorAndFormat() {
        // 修复：颜色码 + 格式码应叠加生效（粗体红色）
        String out = capture(() -> echo.ColorPrintlnPlus("&4&lBoldRed"));
        int redIndex = out.indexOf("\033[31m");
        int boldIndex = out.indexOf("\033[1m");
        assertTrue(redIndex >= 0, "应包含红色序列");
        assertTrue(boldIndex > redIndex, "加粗序列应叠加在颜色之后");
        assertTrue(out.contains("BoldRed"));
    }

    @Test
    void plusFormatCodes() {
        assertTrue(capture(() -> echo.ColorPrintlnPlus("&lBold")).contains("\033[1m"));
        assertTrue(capture(() -> echo.ColorPrintlnPlus("&nUnder")).contains("\033[4m"));
        assertTrue(capture(() -> echo.ColorPrintlnPlus("&oItalic")).contains("\033[3m"));
        assertTrue(capture(() -> echo.ColorPrintlnPlus("&mStrike")).contains("\033[9m"));
    }

    @Test
    void plusResetCode() {
        String out = capture(() -> echo.ColorPrintlnPlus("&4Red&r Normal"));
        assertTrue(out.contains("\033[0m"));
        assertTrue(out.contains("Normal"));
    }

    @Test
    void plusObfuscatedMode() {
        String out = capture(() -> echo.ColorPrintlnPlus("&kSecret"));
        assertTrue(out.contains("\033[8m"));
        assertFalse(out.contains("Secret"));
    }

    @Test
    void plusObfuscationTurnsOffOnNewCode() {
        String out = capture(() -> echo.ColorPrintlnPlus("&kHidden&aVisible"));
        assertTrue(out.contains("\033[92m"));
        assertTrue(out.contains("Visible"));
        assertFalse(out.contains("Hidden"));
    }

    @Test
    void plusInvalidCodeKeptAsIs() {
        String out = capture(() -> echo.ColorPrintlnPlus("&zUnknown"));
        assertTrue(out.contains("&zUnknown"));
    }

    @Test
    void plusAmpersandAtEndKept() {
        String out = capture(() -> echo.ColorPrintlnPlus("Text&"));
        assertTrue(out.contains("Text&"));
    }

    @Test
    void plusAllColorCodes() {
        String[][] colorMap = {
                {"0", "\033[30m"}, {"1", "\033[34m"}, {"2", "\033[32m"}, {"3", "\033[36m"},
                {"4", "\033[31m"}, {"5", "\033[35m"}, {"6", "\033[38;5;214m"}, {"7", "\033[37m"},
                {"8", "\033[90m"}, {"9", "\033[94m"}, {"a", "\033[92m"}, {"b", "\033[96m"},
                {"c", "\033[91m"}, {"d", "\033[95m"}, {"e", "\033[93m"}, {"f", "\033[97m"}
        };
        for (String[] pair : colorMap) {
            String out = capture(() -> echo.ColorPrintlnPlus("&" + pair[0] + "x"));
            assertTrue(out.contains(pair[1]), "&" + pair[0] + " 应转换为 " + pair[1]);
        }
    }
}
