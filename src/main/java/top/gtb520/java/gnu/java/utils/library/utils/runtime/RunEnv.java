package top.gtb520.java.gnu.java.utils.library.utils.runtime;

import top.gtb520.java.gnu.java.utils.library.main;

import java.nio.file.Path;

// 获取运行时的信息
public class RunEnv {
    // 获取当前运行的JAR文件路径
    public static String getJarPath() {
        String path = main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").startsWith("Windows")) {
            path = path.replace('/', '\\');
            //去除路径最前面的 "\\"
            path = path.substring(1);
        }
        return path;
    }

    // 获取临时目录
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    // 新建临时目录，失败时返回 null
    public static String newTempDir() {
        return createTempDir(Path.of(getTempDir()));
    }

    // 在 当前运行的JAR文件路径 下 新建临时目录，失败时返回 null
    public static String newJarThisTempDir() {
        Path parent = Path.of(getJarPath()).getParent();
        if (parent == null) {
            return null;
        }
        return createTempDir(parent);
    }

    // 在指定目录下创建以时间戳命名的临时目录，返回目录路径；目录已存在时返回 null
    private static String createTempDir(Path parent) {
        Path tempDir = parent.resolve(String.valueOf(System.currentTimeMillis()));
        if (!tempDir.toFile().exists()) {
            try {
                tempDir.toFile().mkdirs();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return tempDir.toString();
        }
        return null;
    }
}
