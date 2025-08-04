import dhcp.message.Converters;
import dhcp.message.DhcpMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DhcpMessageTest {

    @BeforeAll static void setup() {
        // This method can be used to set up any necessary configurations before running the tests
        YourTestClient.getInstance();
    }


    @Test void new_discovermsg_ok() {
        // Arrange
    var hex = """
0000   01 01 06 00 40 a4 72 2c 00 00 80 00 00 00 00 00
0010   00 00 00 00 00 00 00 00 00 00 00 00 82 c1 45 d3
0020   81 94 00 00 00 00 00 00 00 00 00 00 00 00 00 00
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
0100   39 02 05 dc 3d 07 01 82 c1 45 d3 81 94 33 04 00
0110   76 a7 00 ff 00 00 00 00 00 00 00 00 00 00 00 00
0120   00 00 00 00 00 00 00 00 00 00 00 00""";

        // Act
        var transactionId = YourTestClient.getInstance().transactionId();
        var msg = new dhcp.message.DhcpMessage(
            dhcp.message.DhcpMessage.DHCPDISCOVER,
            transactionId);
        var hexMsg = Converters.convertByteArraryToHexDump(msg.getBytes());

        // Assert
        assertEquals(hex, hexMsg);
    }

    @Test void new_offermsg_ok() throws IOException {
        // Arrange
        var hex = """
0000   02 01 06 00 40 a4 72 2c 00 00 80 00 00 00 00 00
0010   0a 00 00 82 0a 00 00 01 00 00 00 00 82 c1 45 d3
0020   81 94 00 00 00 00 00 00 00 00 00 00 00 00 00 00
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
00f0   35 01 02 36 04 0a 00 00 01 33 04 00 02 a3 00 3a
0100   04 00 01 51 80 3b 04 00 02 4e a0 01 04 ff ff ff
0110   00 1c 04 0a 00 00 ff 03 04 0a 00 00 01 0f 19 70
0120   68 75 62 2e 6e 65 74 2e 63 61 62 6c 65 2e 72 6f
0130   67 65 72 73 2e 63 6f 6d 06 08 40 47 ff cc 40 47
0140   ff c6 ff""";

        var bytes = Converters.convertHexDumpIntoByteArray(hex);

        // Act
        var transactionId = YourTestClient.getInstance().transactionId();
        var msg = DhcpMessage.parse(bytes);

        // Assert
        assertArrayEquals(transactionId, msg.getTransactionId());
        assertEquals(dhcp.message.DhcpMessage.DHCPOFFER, msg.getMessageType());
        assertArrayEquals(YourTestClient.getInstance().serverIp(), msg.getServerIp());
        assertArrayEquals(YourTestClient.getInstance().offerIp(), msg.getOfferedIp().getAddress());
        assertArrayEquals(YourTestClient.getInstance().offerIp(), msg.getOfferIp());
        assertArrayEquals(YourTestClient.getInstance().clientIdentifier(), msg.getClientIdentifier());
        assertArrayEquals(YourTestClient.getInstance().transactionId(), msg.getTransactionId());
        assertEquals(90 * 24 * 60 * 60, msg.getLeaseTime()); // Default lease time: 90 days in seconds
        assertArrayEquals(Converters.convertHexToByteArray("00 00 00 00"), msg.getGatewayIp());
        assertArrayEquals(Converters.convertHexToByteArray("00 00 00 00"), msg.getClientIp());
        assertEquals(Converters.convertHexToInt("80 00 00 00") > 0, msg.isBroadcastFlag());
    }

    @Test void new_requestmsg_ok() {
        // Arrange
        var hex = """
0000   01 01 06 00 40 a4 72 2c 00 00 80 00 00 00 00 00
0010   00 00 00 00 00 00 00 00 00 00 00 00 82 c1 45 d3
0020   81 94 00 00 00 00 00 00 00 00 00 00 00 00 00 00
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
00f0   35 01 03 37 0b 01 79 03 06 0f 72 77 fc 5f 2c 2e
0100   39 02 05 dc 3d 07 01 82 c1 45 d3 81 94 32 04 0a
0110   00 00 82 36 04 0a 00 00 01 0c 0a 4d 61 63 42 6f
0120   6f 6b 50 72 6f ff 00 00 00 00 00 00""";

        // Act
        var transactionId = YourTestClient.getInstance().transactionId();
        var msg = new dhcp.message.DhcpMessage(
            dhcp.message.DhcpMessage.DHCPREQUEST,
            transactionId);
        msg.setOfferIp(YourTestClient.getInstance().offerIp()); // for request msg we still use the client IP empty
        msg.setServerIp(YourTestClient.getInstance().serverIp());
        msg.setClientIp(YourTestClient.getInstance().clientIp());
        msg.updateBuffer();
        var hexMsg = Converters.convertByteArraryToHexDump(msg.getBytes());

        // Assert
        assertEquals(hex, hexMsg);
    }

    @Test void new_ackmsg_ok() throws IOException {
        // Arrange
        var hex = """
0000   02 01 06 00 40 a4 72 2c 00 01 00 00 00 00 00 00
0010   0a 00 00 82 0a 00 00 01 00 00 00 00 82 c1 45 d3
0020   81 94 00 00 00 00 00 00 00 00 00 00 00 00 00 00
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
00f0   35 01 05 36 04 0a 00 00 01 33 04 00 02 a3 00 3a
0100   04 00 01 51 80 3b 04 00 02 4e a0 01 04 ff ff ff
0110   00 1c 04 0a 00 00 ff 03 04 0a 00 00 01 0f 19 70
0120   68 75 62 2e 6e 65 74 2e 63 61 62 6c 65 2e 72 6f
0130   67 65 72 73 2e 63 6f 6d 06 08 40 47 ff cc 40 47
0140   ff c6 ff""";

        var bytes = Converters.convertHexDumpIntoByteArray(hex);

        // Act
        var msg = new dhcp.message.DhcpMessage(bytes);
        msg.setOfferIp(msg.getOfferIp());
        msg.setServerIp(msg.getServerIp());
        msg.setClientIp(msg.getClientIp());

        // Assert
        assertEquals(dhcp.message.DhcpMessage.DHCPACK, msg.getMessageType());
        assertArrayEquals(YourTestClient.getInstance().serverIp(), msg.getServerIp());
        assertArrayEquals(YourTestClient.getInstance().offerIp(), msg.getOfferedIp().getAddress());
        assertArrayEquals(YourTestClient.getInstance().offerIp(), msg.getOfferIp());
        assertArrayEquals(YourTestClient.getInstance().clientIdentifier(), msg.getClientIdentifier());
        assertArrayEquals(YourTestClient.getInstance().transactionId(), msg.getTransactionId());
        assertEquals(90 * 24 * 60 * 60, msg.getLeaseTime()); // Default lease time: 90 days in seconds
        assertArrayEquals(Converters.convertHexToByteArray("00 00 00 00"), msg.getGatewayIp());
        assertArrayEquals(Converters.convertHexToByteArray("00 00 00 00"), msg.getClientIp());
        assertEquals(Converters.convertHexToInt("80 00 00 00") > 0, msg.isBroadcastFlag());
    }
}
