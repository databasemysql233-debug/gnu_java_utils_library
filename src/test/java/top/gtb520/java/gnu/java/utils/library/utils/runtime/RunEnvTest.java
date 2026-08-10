package top.gtb520.java.gnu.java.utils.library.utils.runtime;

import static top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv.*;

class RunEnvTest {
    public static void main() {
        System.out.println(getJarPath());
        System.out.printf(newJarThisTempDir() + "\n");
        System.out.println(newTempDir());
    }
}