package top.gtb520.java.gnu.java.utils.library;

import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;

public interface main {
    // 压缩包操作相关接口与方法
    interface compress {
        // 解压Tar.Gz压缩包
        static void UnTarGz(String sourceFilePanth, String destFilePanth) throws Exception {
            top.gtb520.java.gnu.java.utils.library.utils.compress.compress.UnTarGz(sourceFilePanth, destFilePanth);
        }

        // 压缩文件或目录为Tar.Gz压缩包
        static void TarGz(String sourceFilePanth, String destFilePanth) throws Exception {
            top.gtb520.java.gnu.java.utils.library.utils.compress.compress.MakeTarGz(sourceFilePanth, destFilePanth);
        }

        // 压缩文件或目录为Zip压缩包
        static void MakeZip(String sourceFilePanth, String destFilePanth) throws Exception {
            top.gtb520.java.gnu.java.utils.library.utils.compress.compress.MakeZip(sourceFilePanth, destFilePanth);
        }

        // 解压Zip压缩包
        static void UnZip(String sourceFilePanth, String destFilePanth) throws Exception {
            top.gtb520.java.gnu.java.utils.library.utils.compress.compress.UnZip(sourceFilePanth, destFilePanth);
        }
    }

    // 终端打印接口与方法
    interface echo {
        // 使用预定义的颜色名称在终端打印彩色文本
        static void Println(String Message, ColorNameEnum ColorName) {
            top.gtb520.java.gnu.java.utils.library.utils.print.echo.ColorPrintln(Message, ColorName);
        }

        // 使用自定义十六进制颜色码在终端打印彩色文本
        static void ColorHexPrintln(String Message, String ColorCode) {
            top.gtb520.java.gnu.java.utils.library.utils.print.echo.ColorHexPrintln(Message, ColorCode);
        }

        // 在单行内打印多种颜色的文字，包含Minecraft格式代码的文本，仿照Minecraft类型的文本终端打印
        static void ColorPrintlnPlus(String Message) {
            top.gtb520.java.gnu.java.utils.library.utils.print.echo.ColorPrintlnPlus(Message);
        }
    }

    // 随机数生成接口与方法
    interface random {
        // 默认随机数生成
        static int defaule(int start_scope, int end_scope) {
            return top.gtb520.java.gnu.java.utils.library.utils.processing.random.defaults.random(start_scope, end_scope);
        }

        // 生成指定长度的随机字符串
        static String randomString(int length) {
            return top.gtb520.java.gnu.java.utils.library.utils.processing.random.defaults.randomString(length);
        }
    }

    // 硬件信息获取接口与方法，HWID
    interface hwid {
        static String getHWID() {
            return top.gtb520.java.gnu.java.utils.library.utils.processing.hwid.getHWID.generateHWID();
        }
    }

    // 项目版本信息获取接口与方法
    interface version {
        static String getVersion() {
            return top.gtb520.java.gnu.java.utils.library.version.VERSION;
        }
        static String getJavaVersion() {
            return top.gtb520.java.gnu.java.utils.library.version.JAVA_VERSION;
        }
        static String getOpenJDKVersion() {
            return top.gtb520.java.gnu.java.utils.library.version.OpenJDK_VERSION;
        }
        static String getOpenJDKRuntimeEnvironment() {
            return top.gtb520.java.gnu.java.utils.library.version.OpenJDK_Runtime_Environment;
        }
        static String getOpenJDK64BitServerVM() {
            return top.gtb520.java.gnu.java.utils.library.version.OpenJDK_64Bit_Server_VM;
        }
    }

    // 运行时信息获取接口与方法
    interface runtime {
        static String getJarPath() {
            return top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv.getJarPath();
        }
        static String getJavaVersion() {
            return top.gtb520.java.gnu.java.utils.library.utils.runtime.JavaRunTimeEnv.JavaVersion();
        }
        static String getJavaRuntimeEnvironmentVersion() {
            return top.gtb520.java.gnu.java.utils.library.utils.runtime.JavaRunTimeEnv.JavaRuntimeEnvironmentVersion();
        }
        static String getJavaVMVersion() {
            return top.gtb520.java.gnu.java.utils.library.utils.runtime.JavaRunTimeEnv.JavaVMVersion();
        }
        static String getJavaBits() {
            return top.gtb520.java.gnu.java.utils.library.utils.runtime.JavaRunTimeEnv.JavaBits();
        }
        static String getJavaVMName() {
            return top.gtb520.java.gnu.java.utils.library.utils.runtime.JavaRunTimeEnv.JavaVMName();
        }

        // 运行时等相关方法与接口
        interface runenv {
            // 获取运行时临时目录
            static String newJarThisTempDir() {
                return top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv.newJarThisTempDir();
            }

            // 新建临时目录
            static String newTempDir() {
                return top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv.newTempDir();
            }

            // 获取临时目录
            static String getTempDir() {
                return top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv.getTempDir();
            }

            // 获取当前运行的JAR文件路径
            static String getJarThisTempDir() {
                return top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv.newJarThisTempDir();
            }
        }
    }
}
