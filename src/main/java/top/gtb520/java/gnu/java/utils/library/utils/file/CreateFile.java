package top.gtb520.java.gnu.java.utils.library.utils.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

// 创建文件
public class CreateFile {
    /**
     * 创建文件，创建空白文件。
     * isBin为True时创建一个空白的二进制文件，否则创建一个空白的普通文件。
     *
     * 注意：文件已存在时不覆盖、不写入文件头，直接返回该文件；
     * 创建失败（如路径无权限）返回 null。
     *
     * @param FileName 文件名
     * @param isBin 是否二进制文件
     * @param binFileType 二进制文件类型，如果是普通文件则不需要设置次值，将其设置为null即可，
     *                    参考isBin参数，isBin参数为False则此参数设置为null
     *
     * @return 文件
     */
    public static File NewFile(String FileName, boolean isBin, BinFileType binFileType) {
        if (FileName == null) {
            return null;
        }
        File targetFile;
        try {
            targetFile = new File(FileName);
            if (targetFile.createNewFile()) {
                if (isBin && binFileType != null) {
                    // 根据二进制文件类型写入文件头标识
                    byte[] header = switch (binFileType) {
                        case ELF -> new byte[]{0x7F, 'E', 'L', 'F'};
                        case PE -> new byte[]{'M', 'Z'};
                    };
                    try (OutputStream os = Files.newOutputStream(targetFile.toPath(),
                            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        os.write(header);
                        os.flush();
                    }
                }
            }
            return targetFile;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 以字节流方式保存文件到目标路径（覆盖同名文件）。
     *
     * @param file 源文件
     * @param TargePath 目标路径
     * @param FileName 文件名
     *
     * @return 是否保存成功，True为保存成功，False为保存失败
     */
    public static boolean StreamSaveFile(File file, Path TargePath, String FileName) {
        if (file == null || !file.isFile() || TargePath == null || FileName == null || FileName.isEmpty()) {
            return false;
        }
        Path targetFile = TargePath.resolve(FileName);
        try {
            Files.copy(file.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public enum BinFileType {
        ELF, PE;
    }
}
