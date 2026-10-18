package top.gtb520.java.gnu.java.utils.library;

public class version {
    // 项目的版本信息，不代表实时运行环境
    // 与 pom.xml 保持一致；构建 JAR 时优先读取 MANIFEST 的 Implementation-Version
    public static final String VERSION = "1.6";
    public static final String JAVA_VERSION = "25";
    public static final String OpenJDK_VERSION = "25.0.0.0.1";
    public static final String OpenJDK_Runtime_Environment = "25.0.0.0.1+36-GA";
    public static final String OpenJDK_64Bit_Server_VM = "25.0.0.0.1+36-GA";

    public String GetVersion() {
        // 打包为 JAR 时读取 MANIFEST 中的 Implementation-Version，类路径运行（如测试）时回退到常量
        Package pkg = version.class.getPackage();
        String implementationVersion = (pkg == null) ? null : pkg.getImplementationVersion();
        return (implementationVersion == null || implementationVersion.isEmpty()) ? VERSION : implementationVersion;
    }
    public String GetJavaVersion() {
        return JAVA_VERSION;
    }
    public String GetOpenJDKVersion() {
        return OpenJDK_VERSION;
    }
    public String GetOpenJDKRuntimeEnvironment() {
        return OpenJDK_Runtime_Environment;
    }
    public String GetOpenJDK64BitServerVM() {
        return OpenJDK_64Bit_Server_VM;
    }
}
