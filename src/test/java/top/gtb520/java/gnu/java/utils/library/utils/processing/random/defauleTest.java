package top.gtb520.java.gnu.java.utils.library.utils.processing.random;

public class defauleTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== defaults.random 测试开始 ==========");

        testRandomInRange();
        testRandomSameStartAndEnd();
        testRandomNegativeRange();
        testRandomReversedRange();
        testRandomLargeRange();
        testRandomDistribution();

        System.out.println("========================================");
        System.out.println("测试完成: 通过 " + passed + ", 失败 " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    /**
     * 测试正常范围内的随机数
     */
    static void testRandomInRange() {
        String testName = "testRandomInRange";
        try {
            for (int i = 0; i < 100; i++) {
                int result = defaults.random(1, 10);
                if (result < 1 || result > 10) {
                    assertCondition(testName + " - 随机数应在[1,10]范围内，实际: " + result, false);
                    return;
                }
            }
            assertCondition(testName + " - 100次随机数均在[1,10]范围内", true);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试起始值等于结束值
     */
    static void testRandomSameStartAndEnd() {
        String testName = "testRandomSameStartAndEnd";
        try {
            int result = defaults.random(5, 5);
            assertCondition(testName + " - 结果应等于5，实际: " + result, result == 5);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试负数范围
     */
    static void testRandomNegativeRange() {
        String testName = "testRandomNegativeRange";
        try {
            for (int i = 0; i < 100; i++) {
                int result = defaults.random(-10, -1);
                if (result < -10 || result > -1) {
                    assertCondition(testName + " - 随机数应在[-10,-1]范围内，实际: " + result, false);
                    return;
                }
            }
            assertCondition(testName + " - 100次随机数均在[-10,-1]范围内", true);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试起始值大于结束值应抛出异常
     */
    static void testRandomReversedRange() {
        String testName = "testRandomReversedRange";
        try {
            defaults.random(10, 1);
            assertCondition(testName + " - 应抛出IllegalArgumentException", false);
        } catch (IllegalArgumentException e) {
            assertCondition(testName + " - 应抛出IllegalArgumentException", true);
        } catch (Exception e) {
            assertCondition(testName + " - 异常类型不正确: " + e.getClass().getName(), false);
        }
    }

    /**
     * 测试大范围随机数
     */
    static void testRandomLargeRange() {
        String testName = "testRandomLargeRange";
        try {
            for (int i = 0; i < 100; i++) {
                int result = defaults.random(-1000000, 1000000);
                if (result < -1000000 || result > 1000000) {
                    assertCondition(testName + " - 随机数超出范围，实际: " + result, false);
                    return;
                }
            }
            assertCondition(testName + " - 100次随机数均在[-1000000,1000000]范围内", true);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试随机数分布（验证不会总是返回同一个值）
     */
    static void testRandomDistribution() {
        String testName = "testRandomDistribution";
        try {
            int first = defaults.random(1, 1000000);
            boolean hasDifferent = false;
            for (int i = 0; i < 100; i++) {
                int result = defaults.random(1, 1000000);
                if (result != first) {
                    hasDifferent = true;
                    break;
                }
            }
            assertCondition(testName + " - 100次调用应产生不同随机数", hasDifferent);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
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
