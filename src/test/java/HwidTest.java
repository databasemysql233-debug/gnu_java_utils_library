import top.gtb520.java.gnu.java.utils.library.main;
import top.gtb520.java.gnu.java.utils.library.utils.processing.hwid.getHWID;

public class HwidTest {
    public static void main() {
        String Hwid_Str;
        Hwid_Str = main.hwid.getHWID();
        System.out.println("HWID: " + Hwid_Str);
        System.out.println("HWID length: " + Hwid_Str.length());

        String Hwid_Str2;
        Hwid_Str2 = getHWID.generateHWID();
        System.out.println("HWID: " + Hwid_Str2);
        System.out.println("HWID length: " + Hwid_Str2.length());
    }
}
