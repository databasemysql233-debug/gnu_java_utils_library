package top.gtb520.java.gnu.java.utils.library.utils.file;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * 加密后覆盖原文件，不添加额外后缀
     * 密钥保存到 keyPath 目录下，密钥名为 原文件名.key
     *
     * @param file 文件
     * @param type 加密类型
     * @param keyPath 密钥文件目录路径，密钥名为 文件名.key
     * @return 加密后的文件（原文件），失败时返回 null
     */
    public static File EncryptionFile(File file, EncryptionType type, Path keyPath) {
        File TargetFile = null;

        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        if (keyPath == null) {
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

                    // 将密钥写入 keyPath 目录，密钥名为 文件名.key
                    Files.createDirectories(keyPath);
                    String keyFileName = file.getName() + ".key";
                    Path keyFilePath = keyPath.resolve(keyFileName);
                    try (OutputStream kos = Files.newOutputStream(keyFilePath,
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                        kos.write(secretKey.getEncoded());
                        kos.flush();
                    }

                    // 加密后覆盖写回原文件
                    try (OutputStream os = Files.newOutputStream(file.toPath(),
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                        os.write(encryptedBytes);
                        os.flush();
                    }
                    return file;
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
     * 解密后覆盖写回原文件，不添加额外后缀
     *
     * @param file 加密文件
     * @param type 加密类型，须与加密时一致
     * @param keyfile 密钥文件，从中读取解密密钥
     * @return 解密后的文件（原文件），失败时返回 null
     */
    public static File DecryptionFile(File file, EncryptionType type, File keyfile) {
        File TargetFile = null;

        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        if (keyfile == null || !keyfile.exists() || !keyfile.isFile()) {
            return null;
        }

        try {
            TargetFile = CompletableFuture.supplyAsync(() -> {
                try {
                    // 从密钥文件读取密钥
                    byte[] keyBytes = Files.readAllBytes(keyfile.toPath());
                    String algorithm = switch (type) {
                        case AES -> "AES";
                        case DES -> "DES";
                        case RSA -> "AES";
                    };
                    SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, algorithm);

                    // 读取加密文件内容
                    byte[] encryptedBytes = Files.readAllBytes(file.toPath());

                    // 解密
                    byte[] decryptedBytes = decryptBytes(encryptedBytes, secretKey, type);

                    // 解密后覆盖写回原文件
                    try (OutputStream os = Files.newOutputStream(file.toPath(),
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                        os.write(decryptedBytes);
                        os.flush();
                    }
                    return file;
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
