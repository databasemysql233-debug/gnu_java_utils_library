package top.gtb520.java.gnu.java.utils.library.utils.file;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 文件加密/解密工具类。
 *
 * <ul>
 *   <li>{@link EncryptionType#AES}：AES-256/GCM，随机 12 字节 IV + 128 位认证标签，密文 = IV + 密文；</li>
 *   <li>{@link EncryptionType#RSA}：RSA-2048 混合加密（RSA-OAEP 包裹随机 AES-256 密钥，数据用 AES-GCM 加密），
 *       密钥文件保存 PKCS#8 私钥；</li>
 *   <li>{@link EncryptionType#DES}：仅兼容旧版本，密钥太短，已废弃，新代码请使用 AES。</li>
 * </ul>
 *
 * 加密/解密均为流式处理（支持大文件），先写临时文件再原子替换原文件，避免中途失败损坏数据。
 * 非法入参（文件/密钥路径不存在、为 null）返回 {@code null}，实际加解密失败抛出 {@link RuntimeException}。
 */
public class EncryptionAndDecryption {

    private static final int AES_KEY_BITS = 256;
    private static final int DES_KEY_BITS = 56;
    private static final int RSA_KEY_BITS = 2048;
    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final String DES_ECB = "DES/ECB/PKCS5Padding";
    private static final String RSA_WRAP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * 文件加密。加密后覆盖原文件（原子替换），密钥保存到 keyPath 目录下，密钥名为 原文件名.key。
     *
     * @param file    要加密的文件
     * @param type    加密类型
     * @param keyPath 密钥文件目录路径
     * @return 加密后的文件（原文件路径）；入参非法时返回 null
     */
    public static File EncryptionFile(File file, EncryptionType type, Path keyPath) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        if (type == null || keyPath == null) {
            return null;
        }
        try {
            Files.createDirectories(keyPath);
            Path keyFilePath = keyPath.resolve(file.getName() + ".key");

            switch (type) {
                case AES -> {
                    SecretKey key = generateKey("AES", AES_KEY_BITS);
                    writeKeyFile(keyFilePath, key.getEncoded());
                    encryptAesGcm(file, key);
                }
                case DES -> {
                    SecretKey key = generateKey("DES", DES_KEY_BITS);
                    writeKeyFile(keyFilePath, key.getEncoded());
                    transform(file, buildCipher(DES_ECB, Cipher.ENCRYPT_MODE, key, null), true);
                }
                case RSA -> {
                    KeyPair keyPair = generateRsaKeyPair();
                    // 密钥文件保存 PKCS#8 私钥
                    writeKeyFile(keyFilePath, keyPair.getPrivate().getEncoded());
                    encryptRsaHybrid(file, keyPair.getPublic());
                }
                default -> throw new IllegalArgumentException("不支持的加密类型: " + type);
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException("加密失败: " + file, e);
        }
    }

    /**
     * 文件解密。解密后覆盖写回原文件（原子替换）。
     *
     * @param file    加密文件
     * @param type    加密类型，须与加密时一致
     * @param keyfile 密钥文件，从中读取解密密钥
     * @return 解密后的文件（原文件路径）；入参非法时返回 null
     */
    public static File DecryptionFile(File file, EncryptionType type, File keyfile) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        if (type == null || keyfile == null || !keyfile.exists() || !keyfile.isFile()) {
            return null;
        }
        try {
            byte[] keyBytes = Files.readAllBytes(keyfile.toPath());
            switch (type) {
                case AES -> decryptAesGcm(file, new SecretKeySpec(keyBytes, "AES"));
                case DES -> transform(file, buildCipher(DES_ECB, Cipher.DECRYPT_MODE,
                        new SecretKeySpec(keyBytes, "DES"), null), false);
                case RSA -> {
                    PrivateKey privateKey = KeyFactory.getInstance("RSA")
                            .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
                    decryptRsaHybrid(file, privateKey);
                }
                default -> throw new IllegalArgumentException("不支持的加密类型: " + type);
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException("解密失败: " + file, e);
        }
    }

    // ==================== AES-GCM ====================

    private static void encryptAesGcm(File file, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_BYTES];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = buildCipher(AES_GCM, Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));

        Path tmp = tempSibling(file);
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp))) {
            out.write(iv); // 密文格式: IV(12) || 密文
            out.flush();
            try (CipherOutputStream cos = new CipherOutputStream(out, cipher);
                 InputStream in = Files.newInputStream(file.toPath())) {
                in.transferTo(cos);
            }
        }
        moveAtomically(tmp, file);
    }

    private static void decryptAesGcm(File file, SecretKey key) throws Exception {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            byte[] iv = in.readNBytes(GCM_IV_BYTES);
            if (iv.length != GCM_IV_BYTES) {
                throw new IOException("密文过短或已损坏，无法读取 IV");
            }
            Cipher cipher = buildCipher(AES_GCM, Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            Path tmp = tempSibling(file);
            try (CipherInputStream cis = new CipherInputStream(in, cipher);
                 OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp))) {
                cis.transferTo(out);
            }
            moveAtomically(tmp, file);
        }
    }

    // ==================== RSA 混合加密 ====================

    private static void encryptRsaHybrid(File file, PublicKey publicKey) throws Exception {
        // 随机 AES-256 会话密钥，RSA-OAEP 包裹后存入文件头
        SecretKey sessionKey = generateKey("AES", AES_KEY_BITS);
        byte[] iv = new byte[GCM_IV_BYTES];
        new SecureRandom().nextBytes(iv);

        Cipher wrapCipher = Cipher.getInstance(RSA_WRAP);
        wrapCipher.init(Cipher.WRAP_MODE, publicKey);
        byte[] wrappedKey = wrapCipher.wrap(sessionKey);

        Cipher dataCipher = buildCipher(AES_GCM, Cipher.ENCRYPT_MODE, sessionKey,
                new GCMParameterSpec(GCM_TAG_BITS, iv));

        // 密文格式: 包裹密钥长度(4字节) || 包裹密钥 || IV(12) || 密文
        Path tmp = tempSibling(file);
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp))) {
            out.write(ByteBuffer.allocate(4).putInt(wrappedKey.length).array());
            out.write(wrappedKey);
            out.write(iv);
            out.flush();
            try (CipherOutputStream cos = new CipherOutputStream(out, dataCipher);
                 InputStream in = Files.newInputStream(file.toPath())) {
                in.transferTo(cos);
            }
        }
        moveAtomically(tmp, file);
    }

    private static void decryptRsaHybrid(File file, PrivateKey privateKey) throws Exception {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file.toPath())))) {
            int wrappedKeyLength = in.readInt();
            if (wrappedKeyLength <= 0 || wrappedKeyLength > 4096) {
                throw new IOException("密文头损坏: 包裹密钥长度非法");
            }
            byte[] wrappedKey = in.readNBytes(wrappedKeyLength);
            if (wrappedKey.length != wrappedKeyLength) {
                throw new IOException("密文头损坏: 包裹密钥不完整");
            }
            byte[] iv = in.readNBytes(GCM_IV_BYTES);
            if (iv.length != GCM_IV_BYTES) {
                throw new IOException("密文头损坏: IV 不完整");
            }

            Cipher unwrapCipher = Cipher.getInstance(RSA_WRAP);
            unwrapCipher.init(Cipher.UNWRAP_MODE, privateKey);
            SecretKey sessionKey = (SecretKey) unwrapCipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

            Cipher dataCipher = buildCipher(AES_GCM, Cipher.DECRYPT_MODE, sessionKey,
                    new GCMParameterSpec(GCM_TAG_BITS, iv));
            Path tmp = tempSibling(file);
            try (CipherInputStream cis = new CipherInputStream(in, dataCipher);
                 OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp))) {
                cis.transferTo(out);
            }
            moveAtomically(tmp, file);
        }
    }

    // ==================== 通用工具 ====================

    private static SecretKey generateKey(String algorithm, int keyBits) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(keyBits);
        return keyGen.generateKey();
    }

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(RSA_KEY_BITS);
        return keyGen.generateKeyPair();
    }

    private static Cipher buildCipher(String algorithm, int mode, SecretKey key,
                                      java.security.spec.AlgorithmParameterSpec params) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        if (params == null) {
            cipher.init(mode, key);
        } else {
            cipher.init(mode, key, params);
        }
        return cipher;
    }

    /**
     * 流式加/解密：读原文件 -> Cipher 流 -> 临时文件 -> 原子替换原文件。
     */
    private static void transform(File file, Cipher cipher, boolean encrypt) throws Exception {
        Path tmp = tempSibling(file);
        if (encrypt) {
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp))) {
                try (CipherOutputStream cos = new CipherOutputStream(out, cipher);
                     InputStream in = Files.newInputStream(file.toPath())) {
                    in.transferTo(cos);
                }
            }
        } else {
            try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
                try (CipherInputStream cis = new CipherInputStream(in, cipher);
                     OutputStream out = new BufferedOutputStream(Files.newOutputStream(tmp))) {
                    cis.transferTo(out);
                }
            }
        }
        moveAtomically(tmp, file);
    }

    /**
     * 在原文件同目录创建临时文件，保证原子移动（rename）时在同一文件系统内。
     */
    private static Path tempSibling(File file) throws IOException {
        Path dir = file.toPath().toAbsolutePath().getParent();
        return Files.createTempFile(dir, file.getName() + ".", ".tmp");
    }

    /**
     * 原子替换原文件；文件系统不支持原子移动时回退为普通覆盖移动。
     */
    private static void moveAtomically(Path tmp, File target) throws IOException {
        try {
            Files.move(tmp, target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void writeKeyFile(Path keyFilePath, byte[] keyBytes) throws IOException {
        try (OutputStream os = Files.newOutputStream(keyFilePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(keyBytes);
            os.flush();
        }
        restrictKeyFilePermissions(keyFilePath);
    }

    /**
     * POSIX 系统上将密钥文件限制为仅属主可读写；Windows 等不支持的文件系统跳过。
     */
    private static void restrictKeyFilePermissions(Path keyFilePath) throws IOException {
        try {
            Files.setPosixFilePermissions(keyFilePath, PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException ignored) {
            // Windows 等不支持 POSIX 权限的文件系统，无需处理
        }
    }

    public enum EncryptionType {
        AES,

        /**
         * 已废弃：DES 密钥仅 56 位，已被暴力破解，仅用于兼容旧版本，新代码请使用 {@link #AES}。
         */
        @Deprecated
        DES,

        /**
         * RSA 混合加密：RSA-2048/OAEP 包裹随机 AES-256 会话密钥，数据使用 AES-GCM 加密。
         */
        RSA;
    }
}
