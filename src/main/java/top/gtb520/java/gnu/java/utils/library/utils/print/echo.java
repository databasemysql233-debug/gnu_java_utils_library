package top.gtb520.java.gnu.java.utils.library.utils.print;

/**
 * 终端彩色输出工具类
 * 提供基于 ANSI 转义序列的终端彩色打印功能
 */
public class echo {
    // 终端彩色打印

    /**
     * 使用自定义十六进制颜色码在终端打印彩色文本
     *
     * @param Message   要打印的文本内容
     * @param HexColor  ANSI 颜色代码（如 "31" 表示红色，"38;5;208" 表示扩展色）
     */
    public static void ColorHexPrintln(String Message, String HexColor) {
        System.out.println("\033[" + HexColor + "m" + Message + "\033[0m");
    }

    /**
     * 使用预定义的颜色名称在终端打印彩色文本
     *
     * @param Message   要打印的文本内容
     * @param ColorName 颜色名称，支持：RED、GREEN、YELLOW、BLUE、PURPLE、CYAN；传入不支持的名称则按默认颜色输出
     */
    public static void ColorPrintln(String Message, ColorNameEnum ColorName) {
        // 根据颜色名称映射对应的 ANSI 颜色转义序列
        switch (ColorName) {
            case RED:
                System.out.println("\033[31m" + Message + "\033[0m");
                break;
            case GREEN:
                System.out.println("\033[32m" + Message + "\033[0m");
                break;
            case YELLOW:
                System.out.println("\033[33m" + Message + "\033[0m");
                break;
            case BLUE:
                System.out.println("\033[34m" + Message + "\033[0m");
                break;
            case PURPLE:
                System.out.println("\033[35m" + Message + "\033[0m");
                break;
            case CYAN:
                System.out.println("\033[36m" + Message + "\033[0m");
                break;
            // 未匹配到已知颜色名称时，以默认颜色输出
            default:
                System.out.println(Message);
        }
    }
}
