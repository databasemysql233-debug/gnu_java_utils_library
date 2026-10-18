package top.gtb520.java.gnu.java.utils.library.utils.processing.random;

import java.util.concurrent.ThreadLocalRandom;

public class defaults {

    private static final String ALL_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 根据输入的范围生成随机数（包含两端）。
     *
     * @param start_scope 起始范围
     * @param end_scope   结束范围
     * @return 随机数
     * @throws IllegalArgumentException 当起始值大于结束值时
     */
    public static int random(int start_scope, int end_scope) {
        if (start_scope > end_scope) {
            throw new IllegalArgumentException("起始范围不能大于结束范围: " + start_scope + " > " + end_scope);
        }
        // 使用 long 上界，避免 end_scope == Integer.MAX_VALUE 时 +1 溢出为负数
        return (int) ThreadLocalRandom.current().nextLong(start_scope, (long) end_scope + 1);
    }

    /**
     * 生成指定长度的随机字符串（大小写字母 + 数字）。
     *
     * @param length 字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 当长度为负数时
     */
    public static String randomString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("字符串长度不能为负数: " + length);
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }
        return result.toString();
    }
}
