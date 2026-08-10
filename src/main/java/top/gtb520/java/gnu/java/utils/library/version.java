package top.gtb520.java.gnu.java.utils.library;

public class version {
    // 项目的版本信息，不代表实时运行环境
    public static final String VERSION = "1.3";
    public static final String JAVA_VERSION = "25";
    public static final String OpenJDK_VERSION = "25.0.0.0.1";
    public static final String OpenJDK_Runtime_Environment = "25.0.0.0.1+36-GA";
    public static final String OpenJDK_64Bit_Server_VM = "25.0.0.0.1+36-GA";

    public String GetVersion() {
        return VERSION;
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
