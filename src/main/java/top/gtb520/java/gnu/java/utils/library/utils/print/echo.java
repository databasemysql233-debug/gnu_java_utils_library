package top.gtb520.java.gnu.java.utils.library.utils.print;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 终端彩色输出工具类
 * 提供基于 ANSI 转义序列的终端彩色打印功能
 */
public class echo {
    // 终端彩色打印

    /**
     * 使用自定义颜色码在终端打印彩色文本
     * 支持三种格式：
     *   - "#RRGGBB" / "#RGB" 十六进制颜色（如 "#FFC0CB"）
     *   - ANSI 颜色代码（如 "31" 表示红色）
     *   - ANSI 扩展色（如 "38;5;208" 表示扩展色）
     *
     * @param Message   要打印的文本内容
     * @param HexColor  颜色代码，支持 "#RRGGBB"、ANSI 颜色代码或扩展色
     * @throws IllegalArgumentException 当颜色代码格式非法或为 null 时
     */
    public static void ColorHexPrintln(String Message, String HexColor) {
        if (Message == null) {
            Message = "";
        }
        String ansi = toAnsiColorCode(HexColor);
        System.out.println("\033[" + ansi + "m" + Message + "\033[0m");
    }

    /**
     * 将颜色代码转换为 ANSI 颜色序列中的参数部分，并对输入做合法性校验。
     *
     * @param hexColor 颜色代码："#RRGGBB"、"#RGB" 或纯数字/分号的 ANSI 参数
     * @return ANSI 参数（如 "38;2;255;192;203"、"31"）
     */
    private static String toAnsiColorCode(String hexColor) {
        if (hexColor == null) {
            throw new IllegalArgumentException("颜色码不能为 null");
        }
        if (hexColor.startsWith("#")) {
            String hex = hexColor.substring(1);
            if ((hex.length() != 3 && hex.length() != 6) || !isHex(hex)) {
                throw new IllegalArgumentException("无效的颜色格式: " + hexColor);
            }
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
            return "38;2;" + r + ";" + g + ";" + b;
        }
        // 仅允许数字与分号，防止向终端注入任意转义序列
        if (!hexColor.matches("[0-9]+(;[0-9]+)*")) {
            throw new IllegalArgumentException("无效的ANSI颜色码: " + hexColor);
        }
        return hexColor;
    }

    private static boolean isHex(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 使用预定义的颜色名称在终端打印彩色文本
     *
     * @param Message   要打印的文本内容
     * @param ColorName 颜色名称，支持：RED、GREEN、YELLOW、BLUE、PURPLE、CYAN；传入 null 则按默认颜色输出
     */
    public static void ColorPrintln(String Message, ColorNameEnum ColorName) {
        if (ColorName == null) {
            System.out.println(Message);
            return;
        }
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

    /**
     * 在单行内打印多种颜色的文字
     *
     * 颜色代码仿照Minecraft格式，如：
     * &4（红色）、&c（浅红色）、
     * &6（橙色）、&e（黄色）、
     * &2（绿色）、&a（浅绿色）、
     * &b（浅青色）、&3（深青色）、
     * &1（蓝色）、&9（浅蓝色）、
     * &d（粉色）、&5（紫色）、
     * &f（白色）、&7（灰色）、
     * &8（深灰色）、&0（黑色）、
     * &l（加粗）、&n（下划线）、
     * &o（斜体）、&k（乱码）、
     * &m（删除线）、&r（重置）。
     *
     * 颜色码会重置之前的格式；加粗/下划线/斜体/删除线等格式码互相叠加；
     * 例如 &4&l 得到"粗体红色"，&n&9 得到"下划线蓝色"。
     *
     * 示例：&6Welcome &2to &4Project
     *
     * @param Message 包含Minecraft格式代码的文本
     */
    public static void ColorPrintlnPlus(String Message) {
        if (Message == null) {
            System.out.println();
            return;
        }

        StringBuilder result = new StringBuilder(Message.length() + 32);
        // 当前叠加的格式序列（加粗/下划线等），颜色码切换后需要重新应用
        StringBuilder activeFormat = new StringBuilder();
        boolean obfuscated = false;
        int i = 0;

        while (i < Message.length()) {
            // 检测格式代码：& 后跟一个字符
            if (Message.charAt(i) == '&' && i + 1 < Message.length()) {
                char code = Character.toLowerCase(Message.charAt(i + 1));
                String ansi = getAnsiCode(code);
                if (ansi != null) {
                    if (isColorCode(code)) {
                        // 颜色码：重置后应用新颜色，再恢复叠加的格式
                        result.append("\033[0m").append(ansi).append(activeFormat);
                        obfuscated = false;
                    } else if (code == 'r') {
                        // 重置：清空颜色与全部格式
                        result.append("\033[0m");
                        activeFormat.setLength(0);
                        obfuscated = false;
                    } else if (code == 'k') {
                        // 乱码模式开启
                        activeFormat.append(ansi);
                        result.append(ansi);
                        obfuscated = true;
                    } else {
                        // 加粗/下划线/斜体/删除线：格式叠加生效
                        activeFormat.append(ansi);
                        result.append(ansi);
                        obfuscated = false;
                    }
                    i += 2;
                    continue;
                }
            }

            // 普通字符
            char ch = Message.charAt(i);
            if (obfuscated && ch != '\n' && ch != '\r') {
                // 乱码模式：将可见字符替换为随机字符
                result.append(randomChar());
            } else {
                result.append(ch);
            }
            i++;
        }

        // 结尾重置格式
        result.append("\033[0m");
        System.out.println(result.toString());
    }

    /**
     * 是否为 Minecraft 颜色码（0-9、a-f）
     */
    private static boolean isColorCode(char code) {
        return (code >= '0' && code <= '9') || (code >= 'a' && code <= 'f');
    }

    /**
     * 根据 Minecraft 格式代码字符返回对应的 ANSI 转义序列
     *
     * @param code & 后的小写字符
     * @return ANSI 转义序列，若字符不是合法格式代码则返回 null
     */
    private static String getAnsiCode(char code) {
        switch (code) {
            // ===== 颜色码 =====
            case '0': return "\033[30m";    // 黑色
            case '1': return "\033[34m";    // 深蓝色
            case '2': return "\033[32m";    // 深绿色
            case '3': return "\033[36m";    // 深青色
            case '4': return "\033[31m";    // 深红色
            case '5': return "\033[35m";    // 深紫色
            case '6': return "\033[38;5;214m"; // 橙色（Gold）
            case '7': return "\033[37m";    // 灰色
            case '8': return "\033[90m";    // 深灰色
            case '9': return "\033[94m";    // 浅蓝色
            case 'a': return "\033[92m";    // 浅绿色
            case 'b': return "\033[96m";    // 浅青色
            case 'c': return "\033[91m";    // 浅红色
            case 'd': return "\033[95m";    // 粉色
            case 'e': return "\033[93m";    // 黄色
            case 'f': return "\033[97m";    // 白色
            // ===== 格式码 =====
            case 'l': return "\033[1m";    // 加粗
            case 'm': return "\033[9m";    // 删除线
            case 'n': return "\033[4m";    // 下划线
            case 'o': return "\033[3m";    // 斜体
            case 'k': return "\033[8m";    // 乱码（隐藏文字，配合随机字符替换实现）
            case 'r': return "\033[0m";    // 重置
            default:  return null;          // 非合法格式码，原样保留
        }
    }

    /**
     * 生成一个随机可见字符，用于乱码模式
     *
     * @return 随机可见字符
     */
    private static char randomChar() {
        // 从可打印 ASCII 字符范围（33~126）中随机选取
        return (char) ThreadLocalRandom.current().nextInt(33, 127);
    }
}
