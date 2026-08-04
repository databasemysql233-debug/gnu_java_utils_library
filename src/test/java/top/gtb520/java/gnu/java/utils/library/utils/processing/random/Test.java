package top.gtb520.java.gnu.java.utils.library.utils.processing.random;

import top.gtb520.java.gnu.java.utils.library.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

// 毫无意义的随机数测试代码，在循环中执行随机数生成 10 亿次
public class Test {
    public static void main(String[] args) {
        int totalTasks = 1_000_000_000;
        // 根据CPU核心数创建固定大小的线程池，避免创建过多线程导致上下文切换开销
        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // 将总任务量平均分配给每个线程
        int tasksPerThread = totalTasks / threadCount;

        for (int t = 0; t < threadCount; t++) {
            final int start = t * tasksPerThread;
            // 处理余数，确保最后一个线程承担剩余任务
            final int end = (t == threadCount - 1) ? totalTasks : start + tasksPerThread;

            executor.submit(() -> {
                // 【核心优化1】使用 ThreadLocalRandom 替代共享 Random
                // 每个线程拥有独立的 seed，完全无锁，避免高并发下的 CAS 竞争
                ThreadLocalRandom random = ThreadLocalRandom.current();

                // 【核心优化2】将 I/O 与计算分离
                // 在实际业务中，应只返回计算结果，而不是在循环中打印
                for (int i = start; i < end; i++) {
                    int random_i = random.nextInt(1, 1000000001); // [1, 1000000000]

                    // 如果确实需要输出，建议降低频率（例如每1000万次输出一次）
//                    if (i % 10000 == 0) {
//                        System.out.println("随机数: " + random_i + " 当前循环位置: " + i);
//                    }
                    System.out.println("随机数: " + random_i + " 当前循环位置: " + i);
                }
            });
        }

        // 优雅关闭线程池
        executor.shutdown();
    }
}
