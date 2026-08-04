package top.gtb520.java.gnu.java.utils.library;

import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;
import top.gtb520.java.gnu.java.utils.library.utils.processing.random.defaults;

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
    }

    // 随机数生成接口与方法
    interface random {
        static int defaule(int start_scope, int end_scope) {
            return defaults.random(start_scope, end_scope);
        }
    }
}
