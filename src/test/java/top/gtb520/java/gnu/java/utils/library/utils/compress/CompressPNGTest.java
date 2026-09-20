package top.gtb520.java.gnu.java.utils.library.utils.compress;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CompressPNGTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== CompressPNG 测试开始 ==========");

        testCompressWithEachThreshold();
        testNonPNGFile();
        testNonExistentFile();
        testCompressedFileIsValidPNG();
        testCompressedFileSmallerOrEqual();

        System.out.println("========================================");
        System.out.println("测试完成: 通过 " + passed + ", 失败 " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    /**
     * 测试每个压缩阈值均能正常压缩并返回原文件
     */
    static void testCompressWithEachThreshold() {
        String testName = "testCompressWithEachThreshold";
        for (CompressPNG.PNGThreshold threshold : CompressPNG.PNGThreshold.values()) {
            File pngFile = null;
            try {
                pngFile = createTestPNG("test_threshold_" + threshold.name());
                long originalSize = pngFile.length();

                File result = CompressPNG.PNG(pngFile, threshold);

                boolean returnedSameFile = result != null && result.equals(pngFile);
                assertCondition(testName + " [" + threshold.name() + "] - 应返回原文件引用", returnedSameFile);

                boolean fileExists = result != null && result.exists() && result.length() > 0;
                assertCondition(testName + " [" + threshold.name() + "] - 压缩后文件应存在且非空", fileExists);

                System.out.println("  [" + threshold.name() + "] 原始: " + originalSize + " bytes, 压缩后: " + (result != null ? result.length() : "N/A") + " bytes");
            } catch (Exception e) {
                assertCondition(testName + " [" + threshold.name() + "] - 不应抛出异常: " + e.getMessage(), false);
            } finally {
                deleteTestFile(pngFile);
            }
        }
    }

    /**
     * 测试传入非PNG文件应返回null
     */
    static void testNonPNGFile() {
        String testName = "testNonPNGFile";
        File txtFile = null;
        try {
            txtFile = File.createTempFile("test_not_png", ".txt");
            java.nio.file.Files.writeString(txtFile.toPath(), "this is not a png");

            File result = CompressPNG.PNG(txtFile, CompressPNG.PNGThreshold.THRESHOLD_3);
            assertCondition(testName + " - 非PNG文件应返回null", result == null);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        } finally {
            deleteTestFile(txtFile);
        }
    }

    /**
     * 测试传入不存在的文件应返回null
     */
    static void testNonExistentFile() {
        String testName = "testNonExistentFile";
        try {
            File nonExistent = new File(System.getProperty("java.io.tmpdir"), "non_existent_file.png");
            File result = CompressPNG.PNG(nonExistent, CompressPNG.PNGThreshold.THRESHOLD_3);
            assertCondition(testName + " - 不存在的文件应返回null", result == null);
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        }
    }

    /**
     * 测试压缩后的文件仍然是有效的PNG图片
     */
    static void testCompressedFileIsValidPNG() {
        String testName = "testCompressedFileIsValidPNG";
        File pngFile = null;
        try {
            pngFile = createTestPNG("test_valid_png");
            CompressPNG.PNG(pngFile, CompressPNG.PNGThreshold.THRESHOLD_3);

            BufferedImage compressed = ImageIO.read(pngFile);
            boolean isValidPNG = compressed != null;
            assertCondition(testName + " - 压缩后的文件应是有效的PNG图片", isValidPNG);

            if (isValidPNG) {
                assertCondition(testName + " - 压缩后图片宽度应一致", compressed.getWidth() == 100);
                assertCondition(testName + " - 压缩后图片高度应一致", compressed.getHeight() == 100);
            }
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        } finally {
            deleteTestFile(pngFile);
        }
    }

    /**
     * 测试压缩后文件大小不超过原始文件
     */
    static void testCompressedFileSmallerOrEqual() {
        String testName = "testCompressedFileSmallerOrEqual";
        File pngFile = null;
        try {
            // 创建一个较大的、有压缩空间的测试图片（纯色+渐变）
            pngFile = createLargeTestPNG("test_compression_ratio", 500, 500);
            long originalSize = pngFile.length();

            File result = CompressPNG.PNG(pngFile, CompressPNG.PNGThreshold.THRESHOLD_5);

            if (result != null) {
                long compressedSize = result.length();
                assertCondition(testName + " - 压缩后文件大小(" + compressedSize + ")应不超过原始大小(" + originalSize + ")",
                        compressedSize <= originalSize);
                System.out.println("  原始: " + originalSize + " bytes, THRESHOLD_5压缩后: " + compressedSize + " bytes");
            } else {
                assertCondition(testName + " - 压缩不应返回null", false);
            }
        } catch (Exception e) {
            assertCondition(testName + " - 不应抛出异常: " + e.getMessage(), false);
        } finally {
            deleteTestFile(pngFile);
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 创建一个100x100的测试PNG图片
     */
    static File createTestPNG(String name) throws IOException {
        File file = File.createTempFile(name, ".png");
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        // 绘制渐变图案，使图片有一定的压缩空间
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                image.setRGB(x, y, new Color(x * 2, y * 2, (x + y)).getRGB());
            }
        }
        g2d.dispose();
        ImageIO.write(image, "png", file);
        return file;
    }

    /**
     * 创建一个指定尺寸的测试PNG图片（包含更多颜色变化以增加压缩效果差异）
     */
    static File createLargeTestPNG(String name, int width, int height) throws IOException {
        File file = File.createTempFile(name, ".png");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        // 绘制大面积纯色区域（压缩率高）
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, width / 2, height / 2);
        g2d.setColor(Color.BLUE);
        g2d.fillRect(width / 2, 0, width / 2, height / 2);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, height / 2, width / 2, height / 2);
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(width / 2, height / 2, width / 2, height / 2);
        g2d.dispose();
        ImageIO.write(image, "png", file);
        return file;
    }

    /**
     * 安全删除测试文件
     */
    static void deleteTestFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    static void assertCondition(String message, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + message);
            passed++;
        } else {
            System.out.println("[FAIL] " + message);
            failed++;
        }
    }
}
