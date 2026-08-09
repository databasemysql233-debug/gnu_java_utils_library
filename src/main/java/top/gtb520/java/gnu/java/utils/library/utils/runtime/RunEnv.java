package top.gtb520.java.gnu.java.utils.library.utils.runtime;

import top.gtb520.java.gnu.java.utils.library.main;

// 获取运行时的信息
public class RunEnv {
    // 获取当前运行的JAR文件路径
    public static String getJarPath() {
        String path = main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").startsWith("Windows")) {
            path = path.replace('/', '\\');
            //去除路径最前面的 “\”
            path = path.substring(1);
        }
        return path;
    }
}
