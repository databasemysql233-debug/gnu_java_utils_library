package top.gtb520.java.gnu.java.utils.library.utils.runtime;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RunEnvTest {

    @Test
    void getTempDirNotNull() {
        assertNotNull(RunEnv.getTempDir());
        assertFalse(RunEnv.getTempDir().isEmpty());
    }

    @Test
    void newTempDirCreatesUniqueDirectories() throws Exception {
        String first = RunEnv.newTempDir();
        String second = RunEnv.newTempDir();
        try {
            assertNotNull(first);
            assertNotNull(second);
            assertNotEquals(first, second, "连续两次调用应得到不同的目录");
            assertTrue(Files.isDirectory(Path.of(first)));
            assertTrue(Files.isDirectory(Path.of(second)));
        } finally {
            Files.deleteIfExists(Path.of(first));
            Files.deleteIfExists(Path.of(second));
        }
    }

    @Test
    void getJarPathResolvesToExistingLocation() {
        // 测试环境下 code source 指向 target/test-classes 或 classes 目录
        String jarPath = RunEnv.getJarPath();
        assertNotNull(jarPath);
        assertFalse(jarPath.isEmpty());
        assertTrue(Files.exists(Path.of(jarPath)), "getJarPath 应返回存在的路径: " + jarPath);
    }

    @Test
    void newJarThisTempDirCreatesDirectory() throws Exception {
        Path parent = Path.of(RunEnv.getJarPath()).getParent();
        if (parent == null || !Files.isDirectory(parent)) {
            // 类路径解析出的位置没有父目录时跳过
            return;
        }
        String created = RunEnv.newJarThisTempDir();
        try {
            assertNotNull(created);
            assertTrue(Files.isDirectory(Path.of(created)));
            assertEquals(parent.toAbsolutePath().normalize(),
                    Path.of(created).getParent().toAbsolutePath().normalize());
        } finally {
            if (created != null) {
                Files.deleteIfExists(Path.of(created));
            }
        }
    }
}
