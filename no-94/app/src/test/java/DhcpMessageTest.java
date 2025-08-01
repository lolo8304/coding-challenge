import dhcp.message.Converters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DhcpMessageTest {

    @BeforeAll static void setup() {
        // This method can be used to set up any necessary configurations before running the tests
        YourTestClient.getInstance();
    }


    @Test void new_discovermsg_ok() {
        // Arrange
    var hex = """
0000   01 01 06 00 40 a4 72 29 00 00 80 00 00 00 00 00
0010   00 00 00 00 00 00 00 00 00 00 00 00 52 98 ee c8
0020   86 c3 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0030   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0040   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0050   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0060   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0070   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0080   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
0090   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00a0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00b0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00c0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00d0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00e0   00 00 00 00 00 00 00 00 00 00 00 00 63 82 53 63
00f0   35 01 01 37 0b 01 79 03 06 0f 72 77 fc 5f 2c 2e
0100   39 02 05 dc 3d 07 01 52 98 ee c8 86 c3 33 04 00
0110   76 a7 00 ff""";
        var bytes = Converters.convertHexDumpIntoByteArray(hex);

        // Act
        var transactionId = YourTestClient.getInstance().transactionId();
        var msg = new dhcp.message.DhcpMessage(
            dhcp.message.DhcpMessage.DHCPDISCOVER,
            transactionId);
        var hexMsg = Converters.convertByteArraryToHexDump(msg.getBytes());

        // Assert
        assertEquals(hex, hexMsg);
    }
}
