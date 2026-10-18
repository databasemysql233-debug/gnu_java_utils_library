package top.gtb520.java.gnu.java.utils.library.utils.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CompressTest {

    @TempDir
    Path tempDir;

    private Path writeFile(String name, String content) throws Exception {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file;
    }

    private Path newDir(String name) throws Exception {
        return Files.createDirectory(tempDir.resolve(name));
    }

    // ==================== Zip ====================

    @Test
    void zipRoundTripSingleFile() throws Exception {
        Path source = writeFile("source.txt", "Hello, Zip!");
        Path zip = tempDir.resolve("output.zip");
        compress.MakeZip(source.toString(), zip.toString());
        assertTrue(Files.exists(zip));

        Path dest = newDir("unzip");
        compress.UnZip(zip.toString(), dest.toString());

        Path result = dest.resolve("source.txt");
        assertTrue(Files.exists(result));
        assertEquals("Hello, Zip!", Files.readString(result));
    }

    @Test
    void zipRoundTripDirectory() throws Exception {
        Path source = tempDir.resolve("source");
        Files.createDirectories(source.resolve("sub"));
        Files.writeString(source.resolve("a.txt"), "file a");
        Files.writeString(source.resolve("sub/b.txt"), "file b");

        Path zip = tempDir.resolve("dir_output.zip");
        compress.MakeZip(source.toString(), zip.toString());

        Path dest = newDir("unzip_dir");
        compress.UnZip(zip.toString(), dest.toString());

        assertEquals("file a", Files.readString(dest.resolve("a.txt")));
        assertEquals("file b", Files.readString(dest.resolve("sub/b.txt")));
    }

    @Test
    void makeZipSourceNotExistThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> compress.MakeZip(tempDir.resolve("not_exist").toString(),
                        tempDir.resolve("out.zip").toString()));
    }

    @Test
    void unZipDestNotDirectoryThrows() throws Exception {
        Path notDir = writeFile("not_a_dir.txt", "I am a file");
        assertThrows(IllegalArgumentException.class,
                () -> compress.UnZip(tempDir.resolve("any.zip").toString(), notDir.toString()));
    }

    @Test
    void unZipRejectsPathTraversal() throws Exception {
        // 构造包含 ../evil.txt 条目的恶意 zip
        Path zip = tempDir.resolve("evil.zip");
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(zip));
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream(out)) {
            ZipArchiveEntry entry = new ZipArchiveEntry("../evil.txt");
            zos.putArchiveEntry(entry);
            zos.write("evil".getBytes(StandardCharsets.UTF_8));
            zos.closeArchiveEntry();
        }

        Path dest = newDir("dest");
        assertThrows(IllegalArgumentException.class, () -> compress.UnZip(zip.toString(), dest.toString()));
        // 不得写出目标目录之外
        assertFalse(Files.exists(tempDir.resolve("evil.txt")));
    }

    // ==================== Tar.Gz ====================

    @Test
    void tarGzRoundTripSingleFile() throws Exception {
        Path source = writeFile("source.txt", "Hello, TarGz!");
        Path tarGz = tempDir.resolve("output.tar.gz");
        compress.MakeTarGz(source.toString(), tarGz.toString());
        assertTrue(Files.exists(tarGz));

        Path dest = newDir("untar");
        compress.UnTarGz(tarGz.toString(), dest.toString());

        Path result = dest.resolve("source.txt");
        assertTrue(Files.exists(result));
        assertEquals("Hello, TarGz!", Files.readString(result));
    }

    @Test
    void tarGzRoundTripDirectory() throws Exception {
        Path source = tempDir.resolve("source");
        Files.createDirectories(source.resolve("sub"));
        Files.writeString(source.resolve("a.txt"), "file a");
        Files.writeString(source.resolve("sub/b.txt"), "file b");

        Path tarGz = tempDir.resolve("dir_output.tar.gz");
        compress.MakeTarGz(source.toString(), tarGz.toString());

        Path dest = newDir("untar_dir");
        compress.UnTarGz(tarGz.toString(), dest.toString());

        assertEquals("file a", Files.readString(dest.resolve("a.txt")));
        assertEquals("file b", Files.readString(dest.resolve("sub/b.txt")));
    }

    @Test
    void makeTarGzSourceNotExistThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> compress.MakeTarGz(tempDir.resolve("not_exist").toString(),
                        tempDir.resolve("out.tar.gz").toString()));
    }

    @Test
    void unTarGzDestNotDirectoryThrows() throws Exception {
        Path notDir = writeFile("not_a_dir.txt", "I am a file");
        assertThrows(IllegalArgumentException.class,
                () -> compress.UnTarGz(tempDir.resolve("any.tar.gz").toString(), notDir.toString()));
    }

    @Test
    void unTarGzRejectsPathTraversal() throws Exception {
        // 构造包含 ../evil.txt 条目的恶意 tar.gz
        Path tarGz = tempDir.resolve("evil.tar.gz");
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(tarGz));
             GzipCompressorOutputStream gz = new GzipCompressorOutputStream(out);
             TarArchiveOutputStream tos = new TarArchiveOutputStream(gz)) {
            TarArchiveEntry entry = new TarArchiveEntry("../evil.txt");
            entry.setSize(4);
            tos.putArchiveEntry(entry);
            tos.write("evil".getBytes(StandardCharsets.UTF_8));
            tos.closeArchiveEntry();
            tos.finish();
        }

        Path dest = newDir("dest");
        assertThrows(IllegalArgumentException.class, () -> compress.UnTarGz(tarGz.toString(), dest.toString()));
        assertFalse(Files.exists(tempDir.resolve("evil.txt")));
    }
}
