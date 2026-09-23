package top.gtb520.java.gnu.java.utils.library.utils.compress;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class CompressPNG {
    /***
     * 压缩PNG图片
     * 传入未压缩的PNG图片文件，和压缩阈值，返回压缩后的PNG图片文件
     * 5个等级的压缩阈值，从低到高，THRESHOLD_1 最低，THRESHOLD_5 最高
     *
     * @param pngFile 未压缩的PNG图片文件
     * @param threshold 压缩阈值
     * @return 压缩后的PNG图片文件
     */
    public static File PNG(File pngFile, PNGThreshold threshold) {
        if (!pngFile.exists()) {
            System.out.println("文件不存在");
            return null;
        }
        if (pngFile.getName().endsWith(".png") || pngFile.getName().endsWith(".PNG")) {
            try {
                BufferedImage image = ImageIO.read(pngFile);
                if (image == null) {
                    System.out.println("无法读取PNG文件");
                    return null;
                }

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
                if (!writers.hasNext()) {
                    System.out.println("未找到PNG写入器");
                    return null;
                }
                ImageWriter writer = writers.next();

                try (ImageOutputStream ios = ImageIO.createImageOutputStream(pngFile)) {
                    writer.setOutput(ios);

                    ImageWriteParam param = writer.getDefaultWriteParam();
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(getCompressionQuality(threshold));

                    writer.write(null, new IIOImage(image, null, null), param);
                } finally {
                    writer.dispose();
                }

                return pngFile;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            System.out.println("文件不是PNG格式，无法压缩");
        }
        return null;
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
