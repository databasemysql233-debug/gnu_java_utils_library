package top.gtb520.java.gnu.java.utils.library.utils.runtime;

// 运行时Java环境信息
public class JavaRunTimeEnv {

    // Java版本号
    public static String JavaVersion() {
        return System.getProperty("java.version");
    }

    // Java运行时环境版本号
    public static String JavaRuntimeEnvironmentVersion() {
        return System.getProperty("java.runtime.version");
    }

    // 获取虚拟机版本
    public static String JavaVMVersion() {
        return System.getProperty("java.vm.version");
    }

    // 获取 JDK 位数，64 位通常包含 amd64 或 64
    public static String JavaBits() {
        return System.getProperty("sun.arch.data.model");
    }

    // 获取JavaVM名称
    public static String JavaVMName() {
        return System.getProperty("java.vm.name");
    }
}
