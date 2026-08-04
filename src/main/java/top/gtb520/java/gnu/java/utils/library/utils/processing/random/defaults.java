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
}
