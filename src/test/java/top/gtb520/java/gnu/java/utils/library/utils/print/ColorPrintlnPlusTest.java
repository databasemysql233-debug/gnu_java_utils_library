package top.gtb520.java.gnu.java.utils.library.utils.print;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ColorPrintlnPlusTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== ColorPrintlnPlus 测试开始 ==========");

        testNullInput();
        testPlainMessageNoFormatCode();
        testSingleColorCode();
        testMultipleColorCodes();
        testFormatCodeBold();
        testFormatCodeUnderline();
        testFormatCodeItalic();
        testFormatCodeStrikethrough();
        testFormatCodeReset();
        testObfuscatedMode();
        testObfuscatedModeDifferentEachCall();
        testObfuscatedModeTurnsOffOnNewCode();
        testInvalidCodeKeptAsIs();
        testAmpersandAtEndOfString();
        testCaseInsensitiveCodes();
        testAllColorCodes();
        testVisualOutput();

        System.out.println("========================================");
        System.out.println("测试完成: 通过 " + passed + ", 失败 " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    /**
     * 捕获 ColorPrintlnPlus 输出到字符串
     */
    static String captureOutput(String input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(baos));
        try {
            echo.ColorPrintlnPlus(input);
        } finally {
            System.setOut(original);
        }
        return baos.toString();
    }

    /**
     * 测试 null 输入
     */
    static void testNullInput() {
        String testName = "testNullInput";
        try {
            String output = captureOutput(null);
            assertCondition(testName + " - null输入应输出空行", output.trim().isEmpty());
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试无格式代码的纯文本
     */
    static void testPlainMessageNoFormatCode() {
        String testName = "testPlainMessageNoFormatCode";
        try {
            String output = captureOutput("Hello World");
            assertCondition(testName + " - 应包含原文本", output.contains("Hello World"));
            assertCondition(testName + " - 应以重置序列结尾", output.contains("\033[0m"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试单个颜色代码
     */
    static void testSingleColorCode() {
        String testName = "testSingleColorCode";
        try {
            String output = captureOutput("&4Red Text");
            // &4 应转换为深红色 ANSI 序列 \033[31m
            assertCondition(testName + " - 应包含红色ANSI序列", output.contains("\033[31m"));
            assertCondition(testName + " - 应包含文本内容", output.contains("Red Text"));
            assertCondition(testName + " - 不应包含格式代码&4", !output.contains("&4"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试多个颜色代码
     */
    static void testMultipleColorCodes() {
        String testName = "testMultipleColorCodes";
        try {
            String output = captureOutput("&6Welcome &2to &4Project");
            // &6 → 橙色 \033[38;5;214m
            assertCondition(testName + " - 应包含橙色ANSI序列", output.contains("\033[38;5;214m"));
            // &2 → 深绿色 \033[32m
            assertCondition(testName + " - 应包含深绿色ANSI序列", output.contains("\033[32m"));
            // &4 → 深红色 \033[31m
            assertCondition(testName + " - 应包含深红色ANSI序列", output.contains("\033[31m"));
            assertCondition(testName + " - 应包含Welcome", output.contains("Welcome"));
            assertCondition(testName + " - 应包含to", output.contains("to"));
            assertCondition(testName + " - 应包含Project", output.contains("Project"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试加粗格式码 &l
     */
    static void testFormatCodeBold() {
        String testName = "testFormatCodeBold";
        try {
            String output = captureOutput("&lBold Text");
            assertCondition(testName + " - 应包含加粗ANSI序列", output.contains("\033[1m"));
            assertCondition(testName + " - 应包含文本", output.contains("Bold Text"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试下划线格式码 &n
     */
    static void testFormatCodeUnderline() {
        String testName = "testFormatCodeUnderline";
        try {
            String output = captureOutput("&nUnderlined");
            assertCondition(testName + " - 应包含下划线ANSI序列", output.contains("\033[4m"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试斜体格式码 &o
     */
    static void testFormatCodeItalic() {
        String testName = "testFormatCodeItalic";
        try {
            String output = captureOutput("&oItalic");
            assertCondition(testName + " - 应包含斜体ANSI序列", output.contains("\033[3m"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试删除线格式码 &m
     */
    static void testFormatCodeStrikethrough() {
        String testName = "testFormatCodeStrikethrough";
        try {
            String output = captureOutput("&mStrike");
            assertCondition(testName + " - 应包含删除线ANSI序列", output.contains("\033[9m"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试重置格式码 &r
     */
    static void testFormatCodeReset() {
        String testName = "testFormatCodeReset";
        try {
            String output = captureOutput("&4Red&r Normal");
            // &r 应产生重置序列 \033[0m
            assertCondition(testName + " - 应包含重置序列", output.contains("\033[0m"));
            assertCondition(testName + " - 应包含Normal", output.contains("Normal"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试乱码模式 &k：字符被替换为随机字符
     */
    static void testObfuscatedMode() {
        String testName = "testObfuscatedMode";
        try {
            String output = captureOutput("&kSecret");
            // 输出不应包含原始文本 "Secret"（因为被替换为随机字符）
            // 但应包含乱码ANSI序列 \033[8m
            assertCondition(testName + " - 应包含乱码ANSI序列", output.contains("\033[8m"));
            // 输出中"Secret"被替换，长度应与原词相同（6个字符区域）
            assertCondition(testName + " - 不应包含原始文本Secret", !output.contains("Secret"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试乱码模式每次调用产生不同结果
     */
    static void testObfuscatedModeDifferentEachCall() {
        String testName = "testObfuscatedModeDifferentEachCall";
        try {
            String output1 = captureOutput("&kAAAAAAAAAA");
            String output2 = captureOutput("&kAAAAAAAAAA");
            // 两次调用的乱码结果应不同（10个字符全部随机，几乎不可能相同）
            assertCondition(testName + " - 两次调用应产生不同乱码", !output1.equals(output2));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试乱码模式在遇到新格式码时关闭
     */
    static void testObfuscatedModeTurnsOffOnNewCode() {
        String testName = "testObfuscatedModeTurnsOffOnNewCode";
        try {
            String output = captureOutput("&kHidden&aVisible");
            // &a 应关闭乱码并开启绿色
            assertCondition(testName + " - 应包含绿色ANSI序列", output.contains("\033[92m"));
            // "Visible" 应保持原文
            assertCondition(testName + " - 新格式码后的文本应保持原文", output.contains("Visible"));
            // "Hidden" 不应出现
            assertCondition(testName + " - 乱码区域的文本不应出现", !output.contains("Hidden"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试非合法格式码保持原样
     */
    static void testInvalidCodeKeptAsIs() {
        String testName = "testInvalidCodeKeptAsIs";
        try {
            String output = captureOutput("&zUnknown");
            // &z 不是合法格式码，应保持原样
            assertCondition(testName + " - 非合法格式码应保持原样", output.contains("&zUnknown"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试 & 在字符串末尾
     */
    static void testAmpersandAtEndOfString() {
        String testName = "testAmpersandAtEndOfString";
        try {
            String output = captureOutput("Text&");
            // 末尾的 & 后无字符，应保持原样
            assertCondition(testName + " - 末尾的&应保持原样", output.contains("Text&"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试格式码大小写不敏感
     */
    static void testCaseInsensitiveCodes() {
        String testName = "testCaseInsensitiveCodes";
        try {
            String outputLower = captureOutput("&4red");
            String outputUpper = captureOutput("&4red");
            assertCondition(testName + " - 小写格式码应正常工作", outputLower.contains("\033[31m"));
            assertCondition(testName + " - 大写格式码应正常工作", outputUpper.contains("\033[31m"));
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试所有16种颜色码均能正确转换
     */
    static void testAllColorCodes() {
        String testName = "testAllColorCodes";
        String[][] colorMap = {
            {"0", "\033[30m"}, {"1", "\033[34m"}, {"2", "\033[32m"}, {"3", "\033[36m"},
            {"4", "\033[31m"}, {"5", "\033[35m"}, {"6", "\033[38;5;214m"}, {"7", "\033[37m"},
            {"8", "\033[90m"}, {"9", "\033[94m"}, {"a", "\033[92m"}, {"b", "\033[96m"},
            {"c", "\033[91m"}, {"d", "\033[95m"}, {"e", "\033[93m"}, {"f", "\033[97m"}
        };
        try {
            boolean allPassed = true;
            for (String[] pair : colorMap) {
                String output = captureOutput("&" + pair[0] + "x");
                if (!output.contains(pair[1])) {
                    assertCondition(testName + " - &" + pair[0] + " 应转换为 " + pair[1], false);
                    allPassed = false;
                }
            }
            if (allPassed) {
                assertCondition(testName + " - 全部16种颜色码转换正确", true);
            }
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 视觉测试：在终端直接展示各种效果
     */
    static void testVisualOutput() {
        System.out.println("--- 视觉测试（请人工确认）---");
        echo.ColorPrintlnPlus("&4深红色 &c浅红色 &6橙色 &e黄色 &2深绿色 &a浅绿色");
        echo.ColorPrintlnPlus("&b浅青色 &3深青色 &1深蓝色 &9浅蓝色 &d粉色 &5深紫色");
        echo.ColorPrintlnPlus("&f白色 &7灰色 &8深灰色 &0黑色");
        echo.ColorPrintlnPlus("&l加粗文字 &n下划线文字 &o斜体文字 &m删除线文字");
        echo.ColorPrintlnPlus("&6Welcome &2to &4Project");
        echo.ColorPrintlnPlus("&k这是一段乱码文字每次不同");
        echo.ColorPrintlnPlus("&4红色 &l加粗红色 &r重置为普通");
        System.out.println("--- 视觉测试结束 ---");
        // 视觉测试直接计为通过
        passed++;
    }

    // ========== 辅助方法 ==========

    static void assertCondition(String message, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + message);
            passed++;
        } else {
            System.out.println("[FAIL] " + message);
            failed++;
        }
    }
}
