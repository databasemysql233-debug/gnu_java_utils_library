package top.gtb520.java.gnu.java.utils.library.utils.compress;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class CompressPNG {
    /**
     * 压缩PNG图片
     * 传入未压缩的PNG图片文件，和压缩阈值，返回压缩后的PNG图片文件
     * 5个等级的压缩阈值，从低到高，THRESHOLD_1 最低，THRESHOLD_5 最高
     *
     * 注意：写入过程先写同目录临时文件、成功后原子替换原文件，避免写入失败损坏原图；
     * JDK 内置 PNG writer 对 compressionQuality 的支持取决于实现，档位效果可能有限。
     *
     * @param pngFile 未压缩的PNG图片文件
     * @param threshold 压缩阈值，不能为 null
     * @return 压缩后的PNG图片文件；文件不存在或不是 PNG 时返回 null
     * @throws IllegalArgumentException 当 threshold 为 null 时
     */
    public static File PNG(File pngFile, PNGThreshold threshold) {
        if (threshold == null) {
            throw new IllegalArgumentException("压缩阈值不能为 null");
        }
        if (pngFile == null || !pngFile.exists()) {
            return null;
        }
        if (!pngFile.getName().toLowerCase().endsWith(".png")) {
            return null;
        }

        File tmp = null;
        try {
            BufferedImage image = ImageIO.read(pngFile);
            if (image == null) {
                return null;
            }

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            if (!writers.hasNext()) {
                return null;
            }
            ImageWriter writer = writers.next();

            try {
                tmp = File.createTempFile(pngFile.getName() + ".", ".tmp",
                        pngFile.getAbsoluteFile().getParentFile());
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(tmp)) {
                    writer.setOutput(ios);

                    ImageWriteParam param = writer.getDefaultWriteParam();
                    try {
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(getCompressionQuality(threshold));
                    } catch (UnsupportedOperationException e) {
                        // 部分 PNG writer 不支持显式压缩质量，退回到默认参数
                        param = writer.getDefaultWriteParam();
                    }

                    writer.write(null, new IIOImage(image, null, null), param);
                }

                // 原子替换原文件，避免原地写入失败损坏原图
                try {
                    Files.move(tmp.toPath(), pngFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(tmp.toPath(), pngFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                writer.dispose();
                if (tmp != null) {
                    try {
                        Files.deleteIfExists(tmp.toPath());
                    } catch (IOException ignored) {
                        // 清理失败不影响结果
                    }
                }
            }

            return pngFile;
        } catch (Exception e) {
            return null;
        }
    }

    private static float getCompressionQuality(PNGThreshold threshold) {
        return switch (threshold) {
            case THRESHOLD_1 -> 0.7f;
            case THRESHOLD_2 -> 0.5f;
            case THRESHOLD_3 -> 0.3f;
            case THRESHOLD_4 -> 0.1f;
            case THRESHOLD_5 -> 0.0f;
        };
    }

    public enum PNGThreshold {
        THRESHOLD_1,
        THRESHOLD_2,
        THRESHOLD_3,
        THRESHOLD_4,
        THRESHOLD_5;
    }
}
