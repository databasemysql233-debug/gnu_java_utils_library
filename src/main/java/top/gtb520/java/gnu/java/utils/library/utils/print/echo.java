package top.gtb520.java.gnu.java.utils.library.utils.print;

/**
 * 终端彩色输出工具类
 * 提供基于 ANSI 转义序列的终端彩色打印功能
 */
public class echo {
    // 终端彩色打印

    /**
     * 使用自定义颜色码在终端打印彩色文本
     * 支持三种格式：
     *   - "#RRGGBB" 十六进制颜色（如 "#FFC0CB"）
     *   - ANSI 颜色代码（如 "31" 表示红色）
     *   - ANSI 扩展色（如 "38;5;208" 表示扩展色）
     *
     * @param Message   要打印的文本内容
     * @param HexColor  颜色代码，支持 "#RRGGBB"、ANSI 颜色代码或扩展色
     */
    public static void ColorHexPrintln(String Message, String HexColor) {
        if (HexColor.startsWith("#") && (HexColor.length() == 7 || HexColor.length() == 4)) {
            // #RRGGBB 或 #RGB 格式，转换为 ANSI 真彩色序列
            String hex = HexColor.substring(1);
            int r, g, b;
            if (hex.length() == 3) {
                // #RGB 简写格式，将每个字符扩展为两位（如 F -> FF）
                r = Integer.parseInt(String.valueOf(hex.charAt(0)), 16) * 17;
                g = Integer.parseInt(String.valueOf(hex.charAt(1)), 16) * 17;
                b = Integer.parseInt(String.valueOf(hex.charAt(2)), 16) * 17;
            } else {
                r = Integer.parseInt(hex.substring(0, 2), 16);
                g = Integer.parseInt(hex.substring(2, 4), 16);
                b = Integer.parseInt(hex.substring(4, 6), 16);
            }
            System.out.println("\033[38;2;" + r + ";" + g + ";" + b + "m" + Message + "\033[0m");
        } else {
            // 原有 ANSI 颜色代码直接拼接
            System.out.println("\033[" + HexColor + "m" + Message + "\033[0m");
        }
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
