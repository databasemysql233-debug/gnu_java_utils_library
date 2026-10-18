package top.gtb520.java.gnu.java.utils.library.utils.processing.hwid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HwidTest {

    @Test
    void generateHwidHasExpectedFormat() {
        String hwid = getHWID.generateHWID();
        assertNotNull(hwid);
        // 成功时是 64 位十六进制；全部硬件信息获取失败时降级为 UNKNOWN_HWID
        assertTrue("UNKNOWN_HWID".equals(hwid) || hwid.matches("[0-9a-f]{64}"),
                "HWID 应为64位小写十六进制或 UNKNOWN_HWID，实际: " + hwid);
    }

    @Test
    void generateHwidIsStableAcrossCalls() {
        String first = getHWID.generateHWID();
        String second = getHWID.generateHWID();
        assertEquals(first, second, "同一台机器两次调用应得到相同的 HWID");
    }
}
