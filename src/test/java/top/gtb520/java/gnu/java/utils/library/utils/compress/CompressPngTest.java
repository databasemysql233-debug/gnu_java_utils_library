package top.gtb520.java.gnu.java.utils.library.utils.compress;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CompressPngTest {

    @TempDir
    Path tempDir;

    private File createTestPng(String name) throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                image.setRGB(x, y, new Color(x * 2, y * 2, (x + y)).getRGB());
            }
        }
        g2d.dispose();
        File file = tempDir.resolve(name + ".png").toFile();
        ImageIO.write(image, "png", file);
        return file;
    }

    @Test
    void compressWithEachThreshold() throws Exception {
        for (CompressPNG.PNGThreshold threshold : CompressPNG.PNGThreshold.values()) {
            File pngFile = createTestPng("test_" + threshold.name());
            File result = CompressPNG.PNG(pngFile, threshold);

            assertNotNull(result, threshold + " 压缩应返回文件");
            assertEquals(pngFile, result, "应返回原文件引用");
            assertTrue(result.exists() && result.length() > 0);

            // 压缩后仍是有效 PNG，尺寸保持不变
            BufferedImage readBack = ImageIO.read(pngFile);
            assertNotNull(readBack);
            assertEquals(100, readBack.getWidth());
            assertEquals(100, readBack.getHeight());
        }
    }

    @Test
    void nonPngFileReturnsNull() throws Exception {
        File txt = tempDir.resolve("note.txt").toFile();
        Files.writeString(txt.toPath(), "this is not a png");
        assertNull(CompressPNG.PNG(txt, CompressPNG.PNGThreshold.THRESHOLD_3));
    }

    @Test
    void missingFileReturnsNull() {
        File missing = tempDir.resolve("missing.png").toFile();
        assertNull(CompressPNG.PNG(missing, CompressPNG.PNGThreshold.THRESHOLD_3));
    }

    @Test
    void nullThresholdThrows() throws Exception {
        File pngFile = createTestPng("null_threshold");
        assertThrows(IllegalArgumentException.class,
                () -> CompressPNG.PNG(pngFile, null));
    }
}
