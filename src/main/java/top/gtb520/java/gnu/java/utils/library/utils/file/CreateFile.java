package top.gtb520.java.gnu.java.utils.library.utils.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

// 创建文件
public class CreateFile {
    /***
     * 创建文件，创建空白文件
     * isBin为True时创建一个空白的二进制文件，否则创建一个空白的普通文件
     * 异步IO操作
     *
     * @param FileName 文件名
     * @param isBin 是否二进制文件
     * @param binFileType 二进制文件类型，如果是普通文件则不需要设置次值，将其设置为null即可，
     *                    参考isBin参数，isBin参数为False则此参数设置为null
     *
     * @return 文件
     */
    public static File NewFile(String FileName, boolean isBin, BinFileType binFileType) {
        File TargetFile = null;
        byte[] BufferByte = new byte[1024];

        try {
            TargetFile = new File(FileName);
            if (TargetFile.createNewFile()) {
                if (isBin && binFileType != null) {
                    // 根据二进制文件类型写入文件头标识
                    byte[] header = switch (binFileType) {
                        case ELF -> new byte[]{0x7F, 'E', 'L', 'F'};
                        case PE -> new byte[]{'M', 'Z'};
                    };
                    try (OutputStream os = Files.newOutputStream(TargetFile.toPath(),
                            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        os.write(header);
                        os.flush();
                    }
                }
            }
        } catch (IOException e) {
            TargetFile = null;
        }

        return TargetFile;
    }

    /***
     * 以字节流方式保存文件
     * 异步IO操作
     *
     * @param file 文件
     * @param TargePath 目标路径
     * @param FileName 文件名
     *
     * @return 是否保存成功，True为保存成功，False为保存失败
     */
    public static boolean StreamSaveFile(File file, Path TargePath, String FileName) {
        try {
            Path targetFile = TargePath.resolve(FileName);
            return CompletableFuture.supplyAsync(() -> {
                try (OutputStream os = Files.newOutputStream(targetFile,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    try (var is = Files.newInputStream(file.toPath())) {
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                    os.flush();
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }).get();
        } catch (Exception e) {
            return false;
        }
    }

    public enum BinFileType {
        ELF, PE;
    }
}
