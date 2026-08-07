import top.gtb520.java.gnu.java.utils.library.main;
import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;

public class echo {
    static void main(String[] args) {
        main.echo.Println("YELLOW", ColorNameEnum.YELLOW);
        main.echo.Println("BLUE", ColorNameEnum.BLUE);
        main.echo.Println("CYAN", ColorNameEnum.CYAN);
        main.echo.Println("PURPLE", ColorNameEnum.PURPLE);
        main.echo.Println("GREEN", ColorNameEnum.GREEN);
        main.echo.Println("RED", ColorNameEnum.RED);

        ColorHexPrintlnTest();

        System.out.println("111111111111111111");
        main.echo.Println("头上带点绿，生活过得去", ColorNameEnum.GREEN);
    }

    static void ColorHexPrintlnTest() {
        main.echo.ColorHexPrintln("ColorHexPrintlnTest_RED", "31");
        main.echo.ColorHexPrintln("自定义橙色", "38;5;208");
        main.echo.ColorHexPrintln("十六进制粉色", "#FFC0CB");
    }
}
