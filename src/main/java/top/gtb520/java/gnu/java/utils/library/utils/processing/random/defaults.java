package top.gtb520.java.gnu.java.utils.library.utils.processing.random;

import java.util.concurrent.ThreadLocalRandom;

public class defaults {
    /**
     * 根据输入的范围生成随机数
     * @param start_scope 起始范围
     * @param end_scope 结束范围
     * @return 随机数
     */
    public static int random(int start_scope, int end_scope) {
        int random_double;

        // 处理边界，确保起始范围小于等于结束范围
        if (start_scope > end_scope) {
            System.out.println("起始范围不能大于结束范围");
            throw new IllegalArgumentException("起始范围不能大于结束范围");
        }
        // 生成随机数
        random_double = ThreadLocalRandom.current().nextInt(start_scope, end_scope + 1);

        return random_double;
    }

    /**
     * 生成指定长度的随机字符串
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        // create a super set of all characters
        String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase + numbers;
        // initialize a string to hold result
        StringBuffer randomString = new StringBuffer();
        // loop for 10 times
        for (int i = 0; i < length; i++) {
            // generate a random number between 0 and length of all characters
            int randomIndex = (int)(Math.random() * allCharacters.length());
            // retrieve character at index and add it to result
            randomString.append(allCharacters.charAt(randomIndex));
        }
        return randomString.toString();
    }
}
