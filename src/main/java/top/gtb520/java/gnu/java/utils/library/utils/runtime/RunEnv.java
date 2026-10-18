package top.gtb520.java.gnu.java.utils.library.utils.runtime;

import top.gtb520.java.gnu.java.utils.library.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 获取运行时的信息
public class RunEnv {

    private static final String TEMP_DIR_PREFIX = "gjul-";

    /**
     * 获取当前运行的 JAR 文件路径。
     * 非 JAR 运行（IDE/类路径）时返回类路径目录；解析失败时回退到当前工作目录。
     */
    public static String getJarPath() {
        try {
            java.net.URL location = main.class.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) {
                return Paths.get("").toAbsolutePath().toString();
            }
            // 通过 URI 解析路径，自动处理 %20 等 URL 编码
            return Paths.get(location.toURI()).toString();
        } catch (URISyntaxException | IllegalArgumentException e) {
            try {
                String path = main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                if (path != null && !path.isEmpty()) {
                    return path;
                }
            } catch (Exception ignored) {
                // 忽略，走最终回退
            }
            return Paths.get("").toAbsolutePath().toString();
        }
    }

    // 获取临时目录
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 在系统临时目录下新建唯一临时子目录，返回其路径。
     */
    public static String newTempDir() {
        return createTempDir(Path.of(getTempDir()));
    }

    /**
     * 在当前运行的 JAR 文件所在目录下新建唯一临时子目录，返回其路径；
     * JAR 路径无父目录时返回 null。
     */
    public static String newJarThisTempDir() {
        Path parent = Path.of(getJarPath()).getParent();
        if (parent == null) {
            return null;
        }
        return createTempDir(parent);
    }

    /**
     * 在指定目录下创建唯一的临时子目录，返回目录路径；创建失败抛出 RuntimeException。
     */
    private static String createTempDir(Path parent) {
        try {
            return Files.createTempDirectory(parent, TEMP_DIR_PREFIX).toString();
        } catch (IOException e) {
            throw new RuntimeException("在 " + parent + " 下创建临时目录失败", e);
        }
    }
}
