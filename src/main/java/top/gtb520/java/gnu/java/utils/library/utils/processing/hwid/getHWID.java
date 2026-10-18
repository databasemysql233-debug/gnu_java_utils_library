package top.gtb520.java.gnu.java.utils.library.utils.processing.hwid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 硬件标识符（HWID）生成器。
 *
 * <p>组合 CPU、主板、MAC 地址、硬盘序列号与操作系统信息，通过 SHA-256 生成 64 位十六进制标识。
 * 所有外部命令通过 {@link ProcessBuilder} 执行，带超时并并发排空输出流，避免挂起。
 * 部分硬件信息获取失败时自动降级（跳过该组件），全部失败时返回 {@code "UNKNOWN_HWID"}。</p>
 */
public class getHWID {
    private final static boolean Debug = false;
    private static final long COMMAND_TIMEOUT_SECONDS = 10;

    /**
     * 生成硬件标识符(HWID)
     * 组合CPU、主板、MAC地址等信息生成唯一标识
     */
    public static String generateHWID() {
        try {
            List<String> hardwareInfo = new ArrayList<>();

            addIfPresent(hardwareInfo, "CPU", getCPUInfo());
            addIfPresent(hardwareInfo, "MB", getMotherboardInfo());
            addIfPresent(hardwareInfo, "MAC", getMacAddress());
            addIfPresent(hardwareInfo, "DISK", getDiskSerialNumber());

            String osInfo = System.getProperty("os.name") + "_" + System.getProperty("os.version");
            hardwareInfo.add("OS:" + osInfo);

            StringBuilder combined = new StringBuilder();
            for (String info : hardwareInfo) {
                if (Debug) {
                    System.out.println("info: " + info);
                }
                combined.append(info).append("|");
            }
            if (Debug) {
                System.out.println("HWID combined: " + combined);
            }

            // 使用SHA-256生成固定长度的哈希值
            return generateHash(combined.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN_HWID";
        }
    }

    private static void addIfPresent(List<String> list, String label, String value) {
        if (value != null && !value.isEmpty()) {
            list.add(label + ":" + value);
        }
    }

    /**
     * 检测是否为Android系统
     */
    private static boolean isAndroid() {
        try {
            String osName = System.getProperty("os.name", "").toLowerCase();
            String javaVmName = System.getProperty("java.vm.name", "").toLowerCase();
            String javaSpecVendor = System.getProperty("java.specification.vendor", "").toLowerCase();

            // 通过多个特征判断是否为Android
            return osName.contains("android") ||
                    javaVmName.contains("android") ||
                    javaSpecVendor.contains("android") ||
                    Files.exists(Path.of("/system/bin/app_process"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取CPU信息
     */
    private static String getCPUInfo() {
        // Android系统特殊处理
        if (isAndroid()) {
            return getAndroidCPUInfo();
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            // Windows：优先使用 PowerShell CIM（wmic 在较新 Windows 中已被移除）
            String id = firstLine(runCommand(List.of(
                    "powershell", "-NoProfile", "-Command",
                    "Get-CimInstance Win32_Processor | Select-Object -ExpandProperty ProcessorId")));
            if (id == null || id.isEmpty()) {
                id = firstNonHeaderLine(runCommand(List.of("wmic", "cpu", "get", "ProcessorId")), "ProcessorId");
            }
            return id;
        } else if (os.contains("mac")) {
            return firstLine(runCommand(List.of("/usr/sbin/sysctl", "-n", "machdep.cpu.brand_string")));
        } else {
            // Linux：直接读取 /proc/cpuinfo，避免外部进程
            return readLinuxCpuInfo();
        }
    }

    private static String readLinuxCpuInfo() {
        try (BufferedReader reader = Files.newBufferedReader(Path.of("/proc/cpuinfo"), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("model name")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        return parts[1].trim();
                    }
                }
            }
        } catch (IOException ignored) {
            // 读取失败，返回 null
        }
        return null;
    }

    /**
     * 获取Android CPU信息
     */
    private static String getAndroidCPUInfo() {
        List<String> infoList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of("/proc/cpuinfo"), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Processor") || line.startsWith("model name")
                        || line.startsWith("Hardware") || line.startsWith("CPU implementer")
                        || line.startsWith("CPU architecture") || line.startsWith("CPU variant")
                        || line.startsWith("CPU part")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add(parts[0].trim() + ":" + parts[1].trim());
                    }
                }
            }
        } catch (IOException ignored) {
            // 静默失败
        }

        if (!infoList.isEmpty()) {
            return String.join("|", infoList);
        }

        // 方法2: 如果读取文件失败，使用系统属性
        String abi = System.getProperty("os.arch", "unknown");
        String cores = Runtime.getRuntime().availableProcessors() + "";
        return "ABI:" + abi + "|Cores:" + cores;
    }

    /**
     * 获取主板信息
     */
    private static String getMotherboardInfo() {
        // Android系统特殊处理
        if (isAndroid()) {
            return getAndroidMotherboardInfo();
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String sn = firstLine(runCommand(List.of(
                    "powershell", "-NoProfile", "-Command",
                    "Get-CimInstance Win32_BaseBoard | Select-Object -ExpandProperty SerialNumber")));
            if (sn == null || sn.isEmpty()) {
                sn = firstNonHeaderLine(runCommand(List.of("wmic", "baseboard", "get", "SerialNumber")), "SerialNumber");
            }
            return sn;
        }
        return null;
    }

    /**
     * 获取Android主板信息
     *
     * <p>注意：Android 系统属性 ro.product.* 无法通过 System.getProperty 获取，
     * 需要 android.os.Build；此处仅作为尽力而为的尝试。</p>
     */
    private static String getAndroidMotherboardInfo() {
        List<String> infoList = new ArrayList<>();

        String brand = System.getProperty("ro.product.brand", "");
        String manufacturer = System.getProperty("ro.product.manufacturer", "");
        String model = System.getProperty("ro.product.model", "");
        String device = System.getProperty("ro.product.device", "");
        String board = System.getProperty("ro.product.board", "");

        if (!brand.isEmpty()) infoList.add("Brand:" + brand);
        if (!manufacturer.isEmpty()) infoList.add("Manufacturer:" + manufacturer);
        if (!model.isEmpty()) infoList.add("Model:" + model);
        if (!device.isEmpty()) infoList.add("Device:" + device);
        if (!board.isEmpty()) infoList.add("Board:" + board);

        return infoList.isEmpty() ? null : String.join("|", infoList);
    }

    /**
     * 获取MAC地址
     * 优先返回非回环、非虚拟网卡的 MAC，避免虚拟网卡导致 HWID 漂移；全部为虚拟网卡时回退到第一个可用 MAC。
     */
    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                return null;
            }
            String fallback = null;
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                try {
                    byte[] mac = network.getHardwareAddress();
                    if (mac == null || mac.length == 0) {
                        continue;
                    }
                    String formatted = formatMac(mac);
                    if (fallback == null) {
                        fallback = formatted;
                    }
                    if (network.isLoopback() || network.isVirtual()) {
                        continue;
                    }
                    return formatted;
                } catch (IOException ignored) {
                    // 该网卡信息不可用，继续尝试下一个
                }
            }
            return fallback;
        } catch (Exception e) {
            return null;
        }
    }

    private static String formatMac(byte[] mac) {
        StringBuilder sb = new StringBuilder(mac.length * 3);
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }

    /**
     * 获取硬盘序列号
     */
    private static String getDiskSerialNumber() {
        // Android系统特殊处理
        if (isAndroid()) {
            return getAndroidStorageInfo();
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String sn = firstLine(runCommand(List.of(
                    "powershell", "-NoProfile", "-Command",
                    "Get-CimInstance Win32_DiskDrive | Select-Object -ExpandProperty SerialNumber")));
            if (sn == null || sn.isEmpty()) {
                sn = firstNonHeaderLine(runCommand(List.of("wmic", "diskdrive", "get", "SerialNumber")), "SerialNumber");
            }
            return sn;
        } else if (os.contains("mac")) {
            String out = runCommand(List.of(
                    "sh", "-c", "system_profiler SPSerialATADataType | grep 'Serial Number'"));
            if (out != null) {
                for (String line : out.split("\\R")) {
                    if (line.contains(":")) {
                        return line.split(":", 2)[1].trim();
                    }
                }
            }
        } else if (os.contains("linux")) {
            // Linux：lsblk 输出物理磁盘序列号
            return firstLine(runCommand(List.of("lsblk", "-dno", "serial")));
        }
        return null;
    }

    /**
     * 获取Android存储信息
     */
    private static String getAndroidStorageInfo() {
        List<String> infoList = new ArrayList<>();

        String internalStorage = System.getProperty("user.dir", "");
        if (!internalStorage.isEmpty()) {
            infoList.add("InternalDir:" + internalStorage);
        }

        infoList.add(Files.exists(Path.of("/sdcard")) ? "ExternalStorage:available" : "ExternalStorage:not_available");
        if (Files.exists(Path.of("/data"))) {
            infoList.add("DataDir:exists");
        }

        return infoList.isEmpty() ? null : String.join("|", infoList);
    }

    /**
     * 执行外部命令：带超时、并发排空 stdout/stderr，超时强制终止。
     *
     * @param command 命令与参数
     * @return 命令 stdout 内容（trim 后）；启动失败或超时返回 null
     */
    private static String runCommand(List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            return null;
        }

        // 并发排空 stderr，避免进程因管道写满而阻塞
        Thread stderrDrain = new Thread(() -> drain(process.getErrorStream()), "hwid-stderr-drain");
        stderrDrain.setDaemon(true);
        stderrDrain.start();

        StringBuilder stdout = new StringBuilder();
        try {
            drainInto(process.getInputStream(), stdout);
            if (!process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return null;
        }
        return stdout.toString().trim();
    }

    /**
     * 排空输入流（丢弃内容），用于 stderr。
     */
    private static void drain(InputStream in) {
        try (InputStream ignored = in) {
            while (in.read() != -1) {
                // 丢弃
            }
        } catch (IOException ignored) {
            // 忽略
        }
    }

    /**
     * 将输入流按行读取到 StringBuilder。
     */
    private static void drainInto(InputStream in, StringBuilder out) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() > 0) {
                    out.append(System.lineSeparator());
                }
                out.append(line);
            }
        } catch (IOException ignored) {
            // 忽略
        }
    }

    /**
     * 取输出中的第一行（trim 后）；输出为空时返回 null。
     */
    private static String firstLine(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        int idx = text.indexOf('\n');
        return (idx < 0 ? text : text.substring(0, idx)).trim();
    }

    /**
     * 取输出中第一个非空且不等于表头的行（wmic 输出首行是列名）。
     */
    private static String firstNonHeaderLine(String text, String header) {
        if (text == null) {
            return null;
        }
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.equals(header)) {
                return trimmed;
            }
        }
        return null;
    }

    /**
     * 使用SHA-256生成哈希值
     */
    private static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(hash.length * 2);

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 摘要不可用", e);
        }
    }
}
