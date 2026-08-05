import top.gtb520.java.gnu.java.utils.library.main;

public class HwidTest {
    public static void main() {
        String Hwid_Str;
        Hwid_Str = main.hwid.getHWID();
        System.out.println("HWID: " + Hwid_Str);
        System.out.println("HWID length: " + Hwid_Str.length());
    }
}
