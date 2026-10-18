package top.gtb520.java.gnu.java.utils.library.utils.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionTest {

    @TempDir
    Path tempDir;

    private File tempFile(String name, String content) throws Exception {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toFile();
    }

    private Path newKeyDir(String name) throws Exception {
        return Files.createDirectory(tempDir.resolve(name));
    }

    @Test
    void aesRoundTrip() throws Exception {
        File file = tempFile("aes.txt", "Hello AES-GCM! 测试内容123");
        String original = Files.readString(file.toPath());
        Path keyPath = newKeyDir("aes_keys");

        assertNotNull(EncryptionAndDecryption.EncryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, keyPath));

        // 加密后内容与原文件不同
        assertFalse(Arrays.equals(original.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file.toPath())));

        // 密钥文件已生成且非空
        File keyFile = keyPath.resolve("aes.txt.key").toFile();
        assertTrue(keyFile.exists());
        assertTrue(keyFile.length() > 0);

        assertNotNull(EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, keyFile));
        assertEquals(original, Files.readString(file.toPath()));
    }

    @Test
    @SuppressWarnings("deprecation")
    void desRoundTrip() throws Exception {
        File file = tempFile("des.txt", "Hello DES! 兼容旧版本");
        String original = Files.readString(file.toPath());
        Path keyPath = newKeyDir("des_keys");

        assertNotNull(EncryptionAndDecryption.EncryptionFile(
                file, EncryptionAndDecryption.EncryptionType.DES, keyPath));

        File keyFile = keyPath.resolve("des.txt.key").toFile();
        assertNotNull(EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.DES, keyFile));
        assertEquals(original, Files.readString(file.toPath()));
    }

    @Test
    void rsaRoundTrip() throws Exception {
        File file = tempFile("rsa.txt", "Hello RSA hybrid! 混合加密测试");
        String original = Files.readString(file.toPath());
        Path keyPath = newKeyDir("rsa_keys");

        assertNotNull(EncryptionAndDecryption.EncryptionFile(
                file, EncryptionAndDecryption.EncryptionType.RSA, keyPath));
        assertFalse(Arrays.equals(original.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file.toPath())));

        File keyFile = keyPath.resolve("rsa.txt.key").toFile();
        assertTrue(keyFile.length() > 0);

        assertNotNull(EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.RSA, keyFile));
        assertEquals(original, Files.readString(file.toPath()));
    }

    @Test
    void largeFileRoundTripIsStreamed() throws Exception {
        // 8MB 文件验证流式加解密不会 OOM
        File file = tempFile("large.bin", "init");
        byte[] chunk = new byte[64 * 1024];
        for (int i = 0; i < chunk.length; i++) {
            chunk[i] = (byte) (i * 31 + 7);
        }
        try (var out = Files.newOutputStream(file.toPath())) {
            for (int i = 0; i < 128; i++) {
                out.write(chunk);
            }
        }
        byte[] original = Files.readAllBytes(file.toPath());
        Path keyPath = newKeyDir("large_keys");

        assertNotNull(EncryptionAndDecryption.EncryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, keyPath));
        File keyFile = keyPath.resolve("large.bin.key").toFile();
        assertNotNull(EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, keyFile));
        assertArrayEquals(original, Files.readAllBytes(file.toPath()));
    }

    @Test
    void invalidInputsReturnNull() throws Exception {
        File file = tempFile("valid.txt", "valid");
        assertNull(EncryptionAndDecryption.EncryptionFile(null,
                EncryptionAndDecryption.EncryptionType.AES, tempDir));
        assertNull(EncryptionAndDecryption.EncryptionFile(
                tempDir.resolve("not_exist.txt").toFile(),
                EncryptionAndDecryption.EncryptionType.AES, tempDir));
        assertNull(EncryptionAndDecryption.EncryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, null));
        assertNull(EncryptionAndDecryption.EncryptionFile(
                file, null, tempDir));
        assertNull(EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, null));
        assertNull(EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES,
                tempDir.resolve("not_exist.key").toFile()));
    }

    @Test
    void wrongKeyThrowsAndDoesNotCorruptFile() throws Exception {
        File file = tempFile("target.txt", "secret content");
        File other = tempFile("other.txt", "other content");
        Path keyPath = newKeyDir("keys");

        EncryptionAndDecryption.EncryptionFile(file, EncryptionAndDecryption.EncryptionType.AES, keyPath);
        EncryptionAndDecryption.EncryptionFile(other, EncryptionAndDecryption.EncryptionType.AES, keyPath);

        byte[] encryptedBytes = Files.readAllBytes(file.toPath());
        File wrongKey = keyPath.resolve("other.txt.key").toFile();

        // GCM 认证失败 → 抛 RuntimeException，且原密文不被破坏
        assertThrows(RuntimeException.class, () -> EncryptionAndDecryption.DecryptionFile(
                file, EncryptionAndDecryption.EncryptionType.AES, wrongKey));
        assertArrayEquals(encryptedBytes, Files.readAllBytes(file.toPath()));
    }
}
