package top.gtb520.java.gnu.java.utils.library.utils.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 压缩包
 */
public class compress {
    /**
     * 解压Tar.Gz压缩包
     *
     * @param sourceFilePanth   压缩包路径
     * @param destFilePanth     解压路径
     * @throws Exception
     */
    public static void UnTarGz(String sourceFilePanth, String destFilePanth) throws Exception {
        Path destPath = Paths.get(destFilePanth);
        if (!Files.isDirectory(destPath)) {
            throw new IllegalArgumentException("目标路径必须是有效的目录");
        }
        Path destRoot = destPath.toAbsolutePath().normalize();

        try (InputStream fileIn = Files.newInputStream(Paths.get(sourceFilePanth));
             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn);
             GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bufferedIn);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {

            TarArchiveEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                // 拒绝符号链接/硬链接条目，防止解压逃逸
                if (entry.isSymbolicLink() || entry.getLinkFlag() == TarConstants.LF_LINK) {
                    throw new IllegalArgumentException("压缩包包含不支持的链接条目: " + entry.getName());
                }
                Path targetPath = resolveInside(destRoot, entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(tarIn, targetPath);
                }
            }
        }
    }

    /**
     * 将压缩包条目名解析为解压目录内的路径；
     * 条目越过目标目录（如 ../ 或绝对路径）时抛出 IllegalArgumentException。
     */
    private static Path resolveInside(Path destRoot, String entryName) {
        Path targetPath = destRoot.resolve(entryName).normalize();
        if (!targetPath.startsWith(destRoot)) {
            throw new IllegalArgumentException("压缩包包含非法路径条目: " + entryName);
        }
        return targetPath;
    }

    /**
     * 压缩Tar.Gz压缩包
     *
     * @param sourceFilePanth   源文件或目录路径
     * @param destFilePanth     压缩包路径
     * @throws Exception
     */
    public static void MakeTarGz(String sourceFilePanth, String destFilePanth) throws Exception {
        Path sourcePath = Paths.get(sourceFilePanth);
        if (!Files.exists(sourcePath)) {
            throw new IllegalArgumentException("源路径不存在");
        }

        try (OutputStream fileOut = Files.newOutputStream(Paths.get(destFilePanth));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);
             GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(bufferedOut);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut)) {

            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            if (Files.isDirectory(sourcePath)) {
                Files.walkFileTree(sourcePath, new java.nio.file.SimpleFileVisitor<Path>() {
                    @Override
                    public java.nio.file.FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws java.io.IOException {
                        String entryName = sourcePath.relativize(file).toString().replace('\\', '/');
                        TarArchiveEntry entry = new TarArchiveEntry(file, entryName);
                        tarOut.putArchiveEntry(entry);
                        Files.copy(file, tarOut);
                        tarOut.closeArchiveEntry();
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }

                    @Override
                    public java.nio.file.FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws java.io.IOException {
                        if (!dir.equals(sourcePath)) {
                            String entryName = sourcePath.relativize(dir).toString().replace('\\', '/') + "/";
                            TarArchiveEntry entry = new TarArchiveEntry(entryName);
                            tarOut.putArchiveEntry(entry);
                            tarOut.closeArchiveEntry();
                        }
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }
                });
            } else {
                TarArchiveEntry entry = new TarArchiveEntry(sourcePath, sourcePath.getFileName().toString());
                tarOut.putArchiveEntry(entry);
                Files.copy(sourcePath, tarOut);
                tarOut.closeArchiveEntry();
            }
        }
    }

    /**
     * 压缩.zip压缩包
     *
     * @param sourceFilePanth   源文件或目录路径
     * @param destFilePanth     压缩包路径
     * @throws Exception
     */
    public static void MakeZip(String sourceFilePanth, String destFilePanth) throws Exception {
        Path sourcePath = Paths.get(sourceFilePanth);
        if (!Files.exists(sourcePath)) {
            throw new IllegalArgumentException("源路径不存在");
        }
    
        try (OutputStream fileOut = Files.newOutputStream(Paths.get(destFilePanth));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);
             ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(bufferedOut)) {
    
            if (Files.isDirectory(sourcePath)) {
                Files.walkFileTree(sourcePath, new java.nio.file.SimpleFileVisitor<Path>() {
                    @Override
                    public java.nio.file.FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws java.io.IOException {
                        String entryName = sourcePath.relativize(file).toString().replace('\\', '/');
                        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                        zipOut.putArchiveEntry(entry);
                        Files.copy(file, zipOut);
                        zipOut.closeArchiveEntry();
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }
    
                    @Override
                    public java.nio.file.FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws java.io.IOException {
                        if (!dir.equals(sourcePath)) {
                            String entryName = sourcePath.relativize(dir).toString().replace('\\', '/') + "/";
                            ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                            zipOut.putArchiveEntry(entry);
                            zipOut.closeArchiveEntry();
                        }
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }
                });
            } else {
                ZipArchiveEntry entry = new ZipArchiveEntry(sourcePath.getFileName().toString());
                zipOut.putArchiveEntry(entry);
                Files.copy(sourcePath, zipOut);
                zipOut.closeArchiveEntry();
            }
        }
    }
    
    /**
     * 解压.zip压缩包
     *
     * @param sourceFilePanth   压缩包路径
     * @param destFilePanth     解压路径
     * @throws Exception
     */
    public static void UnZip(String sourceFilePanth, String destFilePanth) throws Exception {
        Path destPath = Paths.get(destFilePanth);
        if (!Files.isDirectory(destPath)) {
            throw new IllegalArgumentException("目标路径必须是有效的目录");
        }
        Path destRoot = destPath.toAbsolutePath().normalize();

        try (InputStream fileIn = Files.newInputStream(Paths.get(sourceFilePanth));
             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn);
             ZipArchiveInputStream zipIn = new ZipArchiveInputStream(bufferedIn)) {

            ZipArchiveEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                // 拒绝符号链接条目，防止解压逃逸
                if (entry.isUnixSymlink()) {
                    throw new IllegalArgumentException("压缩包包含不支持的符号链接条目: " + entry.getName());
                }
                Path targetPath = resolveInside(destRoot, entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(zipIn, targetPath);
                }
            }
        }
    }

}
