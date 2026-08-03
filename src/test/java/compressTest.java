import top.gtb520.java.gnu.java.utils.library.utils.compress.compress;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class compressTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("========== compress 测试开始 ==========");

        testMakeZipAndUnZipSingleFile();
        testMakeZipAndUnZipDirectory();
        testMakeTarGzAndUnTarGzSingleFile();
        testMakeTarGzAndUnTarGzDirectory();
        testMakeZipSourceNotExist();
        testUnZipDestNotDirectory();
        testMakeTarGzSourceNotExist();
        testUnTarGzDestNotDirectory();

        System.out.println("========================================");
        System.out.println("测试完成: 通过 " + passed + ", 失败 " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    /**
     * 测试单文件的 Zip 压缩与解压
     */
    static void testMakeZipAndUnZipSingleFile() throws Exception {
        String testName = "testMakeZipAndUnZipSingleFile";
        Path tempDir = Files.createTempDirectory("compress_test_");
        try {
            // 准备源文件
            Path sourceFile = tempDir.resolve("source.txt");
            Files.writeString(sourceFile, "Hello, Zip!");

            // 压缩
            Path zipFile = tempDir.resolve("output.zip");
            compress.MakeZip(sourceFile.toString(), zipFile.toString());
            assertCondition(testName + " - 压缩文件应存在", Files.exists(zipFile));

            // 解压
            Path unzipDir = tempDir.resolve("unzip");
            Files.createDirectories(unzipDir);
            compress.UnZip(zipFile.toString(), unzipDir.toString());

            Path resultFile = unzipDir.resolve("source.txt");
            assertCondition(testName + " - 解压文件应存在", Files.exists(resultFile));
            assertCondition(testName + " - 内容应一致",
                    "Hello, Zip!".equals(Files.readString(resultFile)));

        } finally {
            deleteTempDir(tempDir);
        }
    }

    /**
     * 测试目录的 Zip 压缩与解压
     */
    static void testMakeZipAndUnZipDirectory() throws Exception {
        String testName = "testMakeZipAndUnZipDirectory";
        Path tempDir = Files.createTempDirectory("compress_test_");
        try {
            // 准备源目录结构
            Path sourceDir = tempDir.resolve("source");
            Files.createDirectories(sourceDir.resolve("sub"));
            Files.writeString(sourceDir.resolve("a.txt"), "file a");
            Files.writeString(sourceDir.resolve("sub/b.txt"), "file b");

            // 压缩
            Path zipFile = tempDir.resolve("dir_output.zip");
            compress.MakeZip(sourceDir.toString(), zipFile.toString());
            assertCondition(testName + " - 压缩文件应存在", Files.exists(zipFile));

            // 解压
            Path unzipDir = tempDir.resolve("unzip_dir");
            Files.createDirectories(unzipDir);
            compress.UnZip(zipFile.toString(), unzipDir.toString());

            assertCondition(testName + " - 文件a应存在",
                    Files.exists(unzipDir.resolve("a.txt")));
            assertCondition(testName + " - 文件b应存在",
                    Files.exists(unzipDir.resolve("sub/b.txt")));
            assertCondition(testName + " - 文件a内容应一致",
                    "file a".equals(Files.readString(unzipDir.resolve("a.txt"))));
            assertCondition(testName + " - 文件b内容应一致",
                    "file b".equals(Files.readString(unzipDir.resolve("sub/b.txt"))));

        } finally {
            deleteTempDir(tempDir);
        }
    }

    /**
     * 测试单文件的 Tar.Gz 压缩与解压
     */
    static void testMakeTarGzAndUnTarGzSingleFile() throws Exception {
        String testName = "testMakeTarGzAndUnTarGzSingleFile";
        Path tempDir = Files.createTempDirectory("compress_test_");
        try {
            // 准备源文件
            Path sourceFile = tempDir.resolve("source.txt");
            Files.writeString(sourceFile, "Hello, TarGz!");

            // 压缩
            Path tarGzFile = tempDir.resolve("output.tar.gz");
            compress.MakeTarGz(sourceFile.toString(), tarGzFile.toString());
            assertCondition(testName + " - 压缩文件应存在", Files.exists(tarGzFile));

            // 解压
            Path untarDir = tempDir.resolve("untar");
            Files.createDirectories(untarDir);
            compress.UnTarGz(tarGzFile.toString(), untarDir.toString());

            Path resultFile = untarDir.resolve("source.txt");
            assertCondition(testName + " - 解压文件应存在", Files.exists(resultFile));
            assertCondition(testName + " - 内容应一致",
                    "Hello, TarGz!".equals(Files.readString(resultFile)));

        } finally {
            deleteTempDir(tempDir);
        }
    }

    /**
     * 测试目录的 Tar.Gz 压缩与解压
     */
    static void testMakeTarGzAndUnTarGzDirectory() throws Exception {
        String testName = "testMakeTarGzAndUnTarGzDirectory";
        Path tempDir = Files.createTempDirectory("compress_test_");
        try {
            // 准备源目录结构
            Path sourceDir = tempDir.resolve("source");
            Files.createDirectories(sourceDir.resolve("sub"));
            Files.writeString(sourceDir.resolve("a.txt"), "file a");
            Files.writeString(sourceDir.resolve("sub/b.txt"), "file b");

            // 压缩
            Path tarGzFile = tempDir.resolve("dir_output.tar.gz");
            compress.MakeTarGz(sourceDir.toString(), tarGzFile.toString());
            assertCondition(testName + " - 压缩文件应存在", Files.exists(tarGzFile));

            // 解压
            Path untarDir = tempDir.resolve("untar_dir");
            Files.createDirectories(untarDir);
            compress.UnTarGz(tarGzFile.toString(), untarDir.toString());

            assertCondition(testName + " - 文件a应存在",
                    Files.exists(untarDir.resolve("a.txt")));
            assertCondition(testName + " - 文件b应存在",
                    Files.exists(untarDir.resolve("sub/b.txt")));
            assertCondition(testName + " - 文件a内容应一致",
                    "file a".equals(Files.readString(untarDir.resolve("a.txt"))));
            assertCondition(testName + " - 文件b内容应一致",
                    "file b".equals(Files.readString(untarDir.resolve("sub/b.txt"))));

        } finally {
            deleteTempDir(tempDir);
        }
    }

    /**
     * 测试压缩不存在的源路径应抛出异常
     */
    static void testMakeZipSourceNotExist() {
        String testName = "testMakeZipSourceNotExist";
        try {
            compress.MakeZip("not_exist_path", "output.zip");
            assertCondition(testName + " - 应抛出异常", false);
        } catch (IllegalArgumentException e) {
            assertCondition(testName + " - 应抛出IllegalArgumentException", true);
        } catch (Exception e) {
            assertCondition(testName + " - 异常类型不正确: " + e.getClass().getName(), false);
        }
    }

    /**
     * 测试解压到非目录路径应抛出异常
     */
    static void testUnZipDestNotDirectory() throws Exception {
        String testName = "testUnZipDestNotDirectory";
        Path tempDir = Files.createTempDirectory("compress_test_");
        try {
            // 创建一个文件作为解压目标（非目录）
            Path notDir = tempDir.resolve("not_a_dir.txt");
            Files.writeString(notDir, "I am a file");

            // 先创建一个合法zip
            Path sourceFile = tempDir.resolve("src.txt");
            Files.writeString(sourceFile, "test");
            Path zipFile = tempDir.resolve("test.zip");
            compress.MakeZip(sourceFile.toString(), zipFile.toString());

            try {
                compress.UnZip(zipFile.toString(), notDir.toString());
                assertCondition(testName + " - 应抛出异常", false);
            } catch (IllegalArgumentException e) {
                assertCondition(testName + " - 应抛出IllegalArgumentException", true);
            } catch (Exception e) {
                assertCondition(testName + " - 异常类型不正确: " + e.getClass().getName(), false);
            }
        } finally {
            deleteTempDir(tempDir);
        }
    }

    /**
     * 测试TarGz压缩不存在的源路径应抛出异常
     */
    static void testMakeTarGzSourceNotExist() {
        String testName = "testMakeTarGzSourceNotExist";
        try {
            compress.MakeTarGz("not_exist_path", "output.tar.gz");
            assertCondition(testName + " - 应抛出异常", false);
        } catch (IllegalArgumentException e) {
            assertCondition(testName + " - 应抛出IllegalArgumentException", true);
        } catch (Exception e) {
            assertCondition(testName + " - 异常类型不正确: " + e.getClass().getName(), false);
        }
    }

    /**
     * 测试TarGz解压到非目录路径应抛出异常
     */
    static void testUnTarGzDestNotDirectory() throws Exception {
        String testName = "testUnTarGzDestNotDirectory";
        Path tempDir = Files.createTempDirectory("compress_test_");
        try {
            Path notDir = tempDir.resolve("not_a_dir.txt");
            Files.writeString(notDir, "I am a file");

            // 先创建一个合法tar.gz
            Path sourceFile = tempDir.resolve("src.txt");
            Files.writeString(sourceFile, "test");
            Path tarGzFile = tempDir.resolve("test.tar.gz");
            compress.MakeTarGz(sourceFile.toString(), tarGzFile.toString());

            try {
                compress.UnTarGz(tarGzFile.toString(), notDir.toString());
                assertCondition(testName + " - 应抛出异常", false);
            } catch (IllegalArgumentException e) {
                assertCondition(testName + " - 应抛出IllegalArgumentException", true);
            } catch (Exception e) {
                assertCondition(testName + " - 异常类型不正确: " + e.getClass().getName(), false);
            }
        } finally {
            deleteTempDir(tempDir);
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

    static void deleteTempDir(Path dir) {
        try {
            Files.walk(dir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
