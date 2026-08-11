import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import top.gtb520.java.gnu.java.utils.library.utils.file.EncryptionAndDecryption;
import top.gtb520.java.gnu.java.utils.library.utils.file.EncryptionAndDecryption.EncryptionType;

public class EncryptionAndDecryptionTest {

    static int passCount = 0;
    static int failCount = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("========== EncryptionAndDecryption 测试开始 ==========");

        // 正常场景：AES 加密/解密
        testEncryptDecryptAES();
        // 正常场景：DES 加密/解密
        testEncryptDecryptDES();
        // 正常场景：RSA 加密/解密
        testEncryptDecryptRSA();
        // 正常场景：验证密钥文件生成
        testKeyFileGenerated();
        // 正常场景：加密后文件内容发生变化
        testFileContentChangedAfterEncrypt();

        // 异常场景：file 为 null
        testNullFile();
        // 异常场景：keyPath 为 null
        testNullKeyPath();
        // 异常场景：keyfile 为 null（解密）
        testNullKeyFile();
        // 异常场景：文件不存在
        testFileNotExist();
        // 异常场景：密钥文件不存在（解密）
        testKeyFileNotExist();

        System.out.println("========== 测试结果 ==========");
        System.out.println("通过: " + passCount);
        System.out.println("失败: " + failCount);
        System.out.println("总计: " + (passCount + failCount));

        if (failCount > 0) {
            System.out.println("存在失败的测试用例！");
            System.exit(1);
        } else {
            System.out.println("全部测试通过！");
        }
    }

    /**
     * 创建临时测试文件并写入测试内容
     */
    static File createTempFile(String prefix) throws Exception {
        Path tempDir = Files.createTempDirectory("enc_test_");
        File tempFile = tempDir.resolve(prefix + ".txt").toFile();
        Files.writeString(tempFile.toPath(), "Hello, EncryptionAndDecryption Test! 测试内容123");
        return tempFile;
    }

    /**
     * 创建临时密钥目录
     */
    static Path createTempKeyPath() throws Exception {
        return Files.createTempDirectory("enc_key_");
    }

    static void assertEquals(Object expected, Object actual, String testName) {
        if (expected == null && actual == null) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else if (expected != null && expected.equals(actual)) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName + " | 期望: " + expected + ", 实际: " + actual);
        }
    }

    static void assertTrue(boolean condition, String testName) {
        if (condition) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName);
        }
    }

    // ==================== 正常场景 ====================

    static void testEncryptDecryptAES() throws Exception {
        String testName = "AES 加密后解密，内容还原";
        File file = createTempFile("aes_test");
        Path keyPath = createTempKeyPath();
        String originalContent = Files.readString(file.toPath());

        File encrypted = EncryptionAndDecryption.EncryptionFile(file, EncryptionType.AES, keyPath);
        assertTrue(encrypted != null, testName + " - 加密返回非null");

        File keyFile = keyPath.resolve(file.getName() + ".key").toFile();
        File decrypted = EncryptionAndDecryption.DecryptionFile(file, EncryptionType.AES, keyFile);
        assertTrue(decrypted != null, testName + " - 解密返回非null");

        String decryptedContent = Files.readString(file.toPath());
        assertEquals(originalContent, decryptedContent, testName);
    }

    static void testEncryptDecryptDES() throws Exception {
        String testName = "DES 加密后解密，内容还原";
        File file = createTempFile("des_test");
        Path keyPath = createTempKeyPath();
        String originalContent = Files.readString(file.toPath());

        File encrypted = EncryptionAndDecryption.EncryptionFile(file, EncryptionType.DES, keyPath);
        assertTrue(encrypted != null, testName + " - 加密返回非null");

        File keyFile = keyPath.resolve(file.getName() + ".key").toFile();
        File decrypted = EncryptionAndDecryption.DecryptionFile(file, EncryptionType.DES, keyFile);
        assertTrue(decrypted != null, testName + " - 解密返回非null");

        String decryptedContent = Files.readString(file.toPath());
        assertEquals(originalContent, decryptedContent, testName);
    }

    static void testEncryptDecryptRSA() throws Exception {
        String testName = "RSA 加密后解密，内容还原";
        File file = createTempFile("rsa_test");
        Path keyPath = createTempKeyPath();
        String originalContent = Files.readString(file.toPath());

        File encrypted = EncryptionAndDecryption.EncryptionFile(file, EncryptionType.RSA, keyPath);
        assertTrue(encrypted != null, testName + " - 加密返回非null");

        File keyFile = keyPath.resolve(file.getName() + ".key").toFile();
        File decrypted = EncryptionAndDecryption.DecryptionFile(file, EncryptionType.RSA, keyFile);
        assertTrue(decrypted != null, testName + " - 解密返回非null");

        String decryptedContent = Files.readString(file.toPath());
        assertEquals(originalContent, decryptedContent, testName);
    }

    static void testKeyFileGenerated() throws Exception {
        String testName = "加密后密钥文件正确生成";
        File file = createTempFile("key_test");
        Path keyPath = createTempKeyPath();

        EncryptionAndDecryption.EncryptionFile(file, EncryptionType.AES, keyPath);

        File keyFile = keyPath.resolve(file.getName() + ".key").toFile();
        assertTrue(keyFile.exists(), testName + " - 密钥文件存在");
        assertTrue(keyFile.length() > 0, testName + " - 密钥文件非空");
    }

    static void testFileContentChangedAfterEncrypt() throws Exception {
        String testName = "加密后文件内容与原文不同";
        File file = createTempFile("content_test");
        Path keyPath = createTempKeyPath();
        byte[] originalBytes = Files.readAllBytes(file.toPath());

        EncryptionAndDecryption.EncryptionFile(file, EncryptionType.AES, keyPath);

        byte[] encryptedBytes = Files.readAllBytes(file.toPath());
        assertTrue(!java.util.Arrays.equals(originalBytes, encryptedBytes), testName);
    }

    // ==================== 异常场景 ====================

    static void testNullFile() throws Exception {
        String testName = "file 为 null 时加密返回 null";
        Path keyPath = createTempKeyPath();
        File result = EncryptionAndDecryption.EncryptionFile(null, EncryptionType.AES, keyPath);
        assertEquals(null, result, testName);
    }

    static void testNullKeyPath() throws Exception {
        String testName = "keyPath 为 null 时加密返回 null";
        File file = createTempFile("null_keypath");
        File result = EncryptionAndDecryption.EncryptionFile(file, EncryptionType.AES, null);
        assertEquals(null, result, testName);
    }

    static void testNullKeyFile() throws Exception {
        String testName = "keyfile 为 null 时解密返回 null";
        File file = createTempFile("null_keyfile");
        File result = EncryptionAndDecryption.DecryptionFile(file, EncryptionType.AES, null);
        assertEquals(null, result, testName);
    }

    static void testFileNotExist() throws Exception {
        String testName = "文件不存在时加密返回 null";
        Path keyPath = createTempKeyPath();
        File notExist = new File("non_existent_file_12345.txt");
        File result = EncryptionAndDecryption.EncryptionFile(notExist, EncryptionType.AES, keyPath);
        assertEquals(null, result, testName);
    }

    static void testKeyFileNotExist() throws Exception {
        String testName = "密钥文件不存在时解密返回 null";
        File file = createTempFile("no_keyfile");
        File notExistKey = new File("non_existent_key_12345.key");
        File result = EncryptionAndDecryption.DecryptionFile(file, EncryptionType.AES, notExistKey);
        assertEquals(null, result, testName);
    }
}
