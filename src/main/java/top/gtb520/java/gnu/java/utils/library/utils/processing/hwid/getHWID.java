package top.gtb520.java.gnu.java.utils.library.utils.processing.hwid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class getHWID {
    private final static boolean Debug = false;

    /**
     * 生成硬件标识符(HWID)
     * 组合CPU、主板、MAC地址等信息生成唯一标识
     */
    public static String generateHWID() {
        try {
            List<String> hardwareInfo = new ArrayList<>();

            // 1. 获取CPU信息
            String cpuInfo = getCPUInfo();
            if (cpuInfo != null && !cpuInfo.isEmpty()) {
                hardwareInfo.add("CPU:" + cpuInfo);
            }

            // 2. 获取主板序列号
            String motherboardInfo = getMotherboardInfo();
            if (motherboardInfo != null && !motherboardInfo.isEmpty()) {
                hardwareInfo.add("MB:" + motherboardInfo);
            }

            // 3. 获取MAC地址
            String macAddress = getMacAddress();
            if (macAddress != null && !macAddress.isEmpty()) {
                hardwareInfo.add("MAC:" + macAddress);
            }

            // 4. 获取硬盘序列号
            String diskInfo = getDiskSerialNumber();
            if (diskInfo != null && !diskInfo.isEmpty()) {
                hardwareInfo.add("DISK:" + diskInfo);
            }

            // 5. 获取操作系统信息
            String osInfo = System.getProperty("os.name") + "_" +
                    System.getProperty("os.version");
            hardwareInfo.add("OS:" + osInfo);

            // 6. 组合所有硬件信息并生成哈希
            StringBuilder combined = new StringBuilder();
            for (String info : hardwareInfo) {
                if (Debug) {
                    System.out.println("info: " + info);
                    System.out.println("hardwareInfo: " + hardwareInfo);
                    System.out.println("combined: " + combined);
                }
                combined.append(info).append("|");
            }

            if (Debug) {
                System.out.println("HWID combined: " + combined.toString());
            }

            // 使用SHA-256生成固定长度的哈希值
            return generateHash(combined.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN_HWID";
        }
    }

    /**
     * 检测是否为Android系统
     */
    private static boolean isAndroid() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String javaVmName = System.getProperty("java.vm.name", "").toLowerCase();
            String javaSpecVendor = System.getProperty("java.specification.vendor", "").toLowerCase();

            // 通过多个特征判断是否为Android
            return osName.contains("android") ||
                    javaVmName.contains("android") ||
                    javaSpecVendor.contains("android") ||
                    new java.io.File("/system/bin/app_process").exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取CPU信息
     */
    private static String getCPUInfo() {
        try {
            // Android系统特殊处理
            if (isAndroid()) {
                return getAndroidCPUInfo();
            }

            String os = System.getProperty("os.name").toLowerCase();
            Process process;

            if (os.contains("win")) {
                // Windows: 获取ProcessorId
                process = Runtime.getRuntime().exec(
                        new String[]{"wmic", "cpu", "get", "ProcessorId"}
                );
            } else if (os.contains("mac")) {
                // macOS
                process = Runtime.getRuntime().exec(
                        new String[]{"/usr/sbin/sysctl", "-n", "machdep.cpu.brand_string"}
                );
            } else {
                // Linux: 获取CPU型号
                process = Runtime.getRuntime().exec(
                        new String[]{"cat", "/proc/cpuinfo"}
                );
            }

            process.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            StringBuilder result = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (os.contains("win") && !line.equals("ProcessorId")) {
                        return line;
                    } else if (os.contains("mac")) {
                        return line;
                    } else if (line.startsWith("model name")) {
                        return line.split(":", 2)[1].trim();
                    }
                }
            }

        } catch (Exception e) {
            // 静默失败，返回null
        }
        return null;
    }

    /**
     * 获取Android CPU信息
     */
    private static String getAndroidCPUInfo() {
        StringBuilder cpuInfo = new StringBuilder();

        try {
            // 方法1: 读取 /proc/cpuinfo
            BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line;
            List<String> infoList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 提取关键信息
                if (line.startsWith("Processor") || line.startsWith("model name")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add("Processor:" + parts[1].trim());
                    }
                } else if (line.startsWith("Hardware")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add("Hardware:" + parts[1].trim());
                    }
                } else if (line.startsWith("CPU implementer")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add("Implementer:" + parts[1].trim());
                    }
                } else if (line.startsWith("CPU architecture")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add("Architecture:" + parts[1].trim());
                    }
                } else if (line.startsWith("CPU variant")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add("Variant:" + parts[1].trim());
                    }
                } else if (line.startsWith("CPU part")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        infoList.add("Part:" + parts[1].trim());
                    }
                }
            }
            reader.close();

            if (!infoList.isEmpty()) {
                return String.join("|", infoList);
            }

        } catch (Exception e) {
            // 静默失败
        }

        // 方法2: 如果读取文件失败，使用System.getProperty
        try {
            String abi = System.getProperty("os.arch", "unknown");
            String cores = Runtime.getRuntime().availableProcessors() + "";
            return "ABI:" + abi + "|Cores:" + cores;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取主板信息
     */
    private static String getMotherboardInfo() {
        try {
            // Android系统特殊处理
            if (isAndroid()) {
                return getAndroidMotherboardInfo();
            }

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Process process = Runtime.getRuntime().exec(
                        new String[]{"wmic", "baseboard", "get", "SerialNumber"}
                );
                process.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("SerialNumber")) {
                        return line;
                    }
                }
            }

        } catch (Exception e) {
            // 静默失败
        }
        return null;
    }

    /**
     * 获取Android主板信息
     */
    private static String getAndroidMotherboardInfo() {
        try {
            List<String> infoList = new ArrayList<>();

            // 读取Android设备特性
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

            if (!infoList.isEmpty()) {
                return String.join("|", infoList);
            }

        } catch (Exception e) {
            // 静默失败
        }
        return null;
    }

    /**
     * 获取MAC地址
     */
    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces =
                    NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                byte[] mac = network.getHardwareAddress();

                if (mac != null && mac.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i],
                                (i < mac.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                }
            }

        } catch (Exception e) {
            // 静默失败
        }
        return null;
    }

    /**
     * 获取硬盘序列号
     */
    private static String getDiskSerialNumber() {
        try {
            // Android系统特殊处理
            if (isAndroid()) {
                return getAndroidStorageInfo();
            }

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Process process = Runtime.getRuntime().exec(
                        new String[]{"wmic", "diskdrive", "get", "SerialNumber"}
                );
                process.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equals("SerialNumber")) {
                        return line;
                    }
                }
            } else if (os.contains("mac") || os.contains("linux")) {
                Process process = Runtime.getRuntime().exec(
                        new String[]{"sh", "-c", "system_profiler SPSerialATADataType | grep 'Serial Number'"}
                );
                process.waitFor();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                String line;
                if ((line = reader.readLine()) != null) {
                    if (line.contains(":")) {
                        return line.split(":", 2)[1].trim();
                    }
                }
            }

        } catch (Exception e) {
            // 静默失败
        }
        return null;
    }

    /**
     * 获取Android存储信息
     */
    private static String getAndroidStorageInfo() {
        try {
            // 读取 /proc/mounts 或使用系统属性
            List<String> infoList = new ArrayList<>();

            // 尝试获取存储相关信息
            String internalStorage = System.getProperty("user.dir", "");
            if (!internalStorage.isEmpty()) {
                infoList.add("InternalDir:" + internalStorage);
            }

            // 检查外部存储是否可用
            java.io.File externalStorage = new java.io.File("/sdcard");
            if (externalStorage.exists()) {
                infoList.add("ExternalStorage:available");
            } else {
                infoList.add("ExternalStorage:not_available");
            }

            // 获取数据目录
            java.io.File dataDir = new java.io.File("/data");
            if (dataDir.exists()) {
                infoList.add("DataDir:exists");
            }

            if (!infoList.isEmpty()) {
                return String.join("|", infoList);
            }

        } catch (Exception e) {
            // 静默失败
        }
        return null;
    }

    /**
     * 使用SHA-256生成哈希值
     */
    private static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return input.hashCode() + "";
        }
    }
}
