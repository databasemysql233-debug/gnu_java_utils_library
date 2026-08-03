import top.gtb520.java.gnu.java.utils.library.main;
import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;

public class echoTest {
    public static void main(String[] args) {
        System.out.println("========== echo 测试开始 ==========");

        // 测试 #RRGGBB 格式
        main.echo.ColorHexPrintln("十六进制粉色 #FFC0CB", "#FFC0CB");
        main.echo.ColorHexPrintln("十六进制红色 #FF0000", "#FF0000");
        main.echo.ColorHexPrintln("十六进制绿色 #00FF00", "#00FF00");
        main.echo.ColorHexPrintln("十六进制蓝色 #0000FF", "#0000FF");

        // 测试 #RGB 简写格式
        main.echo.ColorHexPrintln("RGB简写粉色 #F0F", "#F0F");
        main.echo.ColorHexPrintln("RGB简写白色 #FFF", "#FFF");

        // 测试原有 ANSI 颜色代码（兼容性）
        main.echo.ColorHexPrintln("ANSI红色 31", "31");
        main.echo.ColorHexPrintln("ANSI扩展色 38;5;208", "38;5;208");

        // 测试预定义颜色枚举
        main.echo.Println("枚举红色", ColorNameEnum.RED);
        main.echo.Println("枚举蓝色", ColorNameEnum.BLUE);
        main.echo.Println("枚举绿色", ColorNameEnum.GREEN);
        main.echo.Println("枚举黄色", ColorNameEnum.YELLOW);
        main.echo.Println("枚举青色", ColorNameEnum.CYAN);
        main.echo.Println("枚举紫色", ColorNameEnum.PURPLE);

        System.out.println("========== echo 测试完成 ==========");
    }
}
