package top.gtb520.java.gnu.java.utils.library.utils.file;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncryptionAndDecryption {
    /***
     * 文件加密
     * 异步执行加密操作
     * 加密后的文件保存为原文件名加 .enc 后缀，位于同目录下
     *
     * @param file 文件
     * @param type 加密类型
     * @return 加密后的文件，失败时返回 null
     */
    public static File EncryptionFile(File file, EncryptionType type) {
        File TargetFile = null;

        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        try {
            TargetFile = CompletableFuture.supplyAsync(() -> {
                try {
                    // 读取原文件内容
                    byte[] plainBytes = Files.readAllBytes(file.toPath());

                    // 根据加密类型生成对称密钥
                    SecretKey secretKey = generateSecretKey(type);
                    byte[] encryptedBytes = encryptBytes(plainBytes, secretKey, type);

                    // 写入加密后的文件，文件格式：[1字节密钥长度][密钥字节][加密数据]
                    byte[] keyBytes = secretKey.getEncoded();
                    String encryptedFileName = file.getName() + ".enc";
                    File encryptedFile = new File(file.getParentFile(), encryptedFileName);
                    try (OutputStream os = Files.newOutputStream(encryptedFile.toPath(),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        os.write(keyBytes.length);
                        os.write(keyBytes);
                        os.write(encryptedBytes);
                        os.flush();
                    }
                    return encryptedFile;
                } catch (Exception e) {
                    return null;
                }
            }).get();
        } catch (Exception e) {
            TargetFile = null;
        }

        return TargetFile;
    }

    /***
     * 生成对称密钥
     *
     * @param type 加密类型
     * @return 对称密钥
     */
    private static SecretKey generateSecretKey(EncryptionType type) throws NoSuchAlgorithmException {
        String algorithm = switch (type) {
            case AES -> "AES";
            case DES -> "DES";
            case RSA -> "AES"; // RSA 为非对称加密，此处用 AES 做对称加密替代
        };
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        int keySize = switch (type) {
            case AES -> 128;
            case DES -> 56;
            case RSA -> 128;
        };
        keyGen.init(keySize);
        return keyGen.generateKey();
    }

    /***
     * 对字节数据进行加密
     *
     * @param data 原始数据
     * @param key  密钥
     * @param type 加密类型
     * @return 加密后的数据
     */
    private static byte[] encryptBytes(byte[] data, SecretKey key, EncryptionType type)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String algorithm = switch (type) {
            case AES -> "AES/ECB/PKCS5Padding";
            case DES -> "DES/ECB/PKCS5Padding";
            case RSA -> "AES/ECB/PKCS5Padding";
        };
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /***
     * 文件解密
     * 异步执行解密操作
     * 解密后的文件去除 .enc 后缀，保存在同目录下
     *
     * @param file 加密文件（.enc）
     * @param type 加密类型，须与加密时一致
     * @return 解密后的文件，失败时返回 null
     */
    public static File DecryptionFile(File file, EncryptionType type) {
        File TargetFile = null;

        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        try {
            TargetFile = CompletableFuture.supplyAsync(() -> {
                try {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());

                    // 读取文件头中的密钥长度和密钥
                    int keyLength = fileBytes[0] & 0xFF;
                    byte[] keyBytes = new byte[keyLength];
                    System.arraycopy(fileBytes, 1, keyBytes, 0, keyLength);

                    // 还原密钥
                    String algorithm = switch (type) {
                        case AES -> "AES";
                        case DES -> "DES";
                        case RSA -> "AES";
                    };
                    SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, algorithm);

                    // 提取加密数据部分
                    byte[] encryptedBytes = new byte[fileBytes.length - 1 - keyLength];
                    System.arraycopy(fileBytes, 1 + keyLength, encryptedBytes, 0, encryptedBytes.length);

                    byte[] decryptedBytes = decryptBytes(encryptedBytes, secretKey, type);

                    // 生成解密后的文件名，去除 .enc 后缀
                    String fileName = file.getName();
                    String decryptedFileName = fileName.endsWith(".enc")
                            ? fileName.substring(0, fileName.length() - 4)
                            : fileName + ".dec";
                    File decryptedFile = new File(file.getParentFile(), decryptedFileName);
                    try (OutputStream os = Files.newOutputStream(decryptedFile.toPath(),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        os.write(decryptedBytes);
                        os.flush();
                    }
                    return decryptedFile;
                } catch (Exception e) {
                    return null;
                }
            }).get();
        } catch (Exception e) {
            TargetFile = null;
        }

        return TargetFile;
    }

    /***
     * 对字节数据进行解密
     *
     * @param data 加密数据
     * @param key  密钥
     * @param type 加密类型
     * @return 解密后的数据
     */
    private static byte[] decryptBytes(byte[] data, SecretKey key, EncryptionType type)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String algorithm = switch (type) {
            case AES -> "AES/ECB/PKCS5Padding";
            case DES -> "DES/ECB/PKCS5Padding";
            case RSA -> "AES/ECB/PKCS5Padding";
        };
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public enum EncryptionType {
        AES, DES, RSA;
    }
}
