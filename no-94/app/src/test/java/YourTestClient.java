import dhcp.YourClient;
import dhcp.message.Converters;

public class YourTestClient extends YourClient {

    protected YourTestClient() {
        // Private constructor to prevent instantiation
    }

    public static YourTestClient getInstance() {
        if (instance == null) {
            instance = new YourTestClient();
        }
        return (YourTestClient)instance;
    }

    @Override
    public byte[] clientIdentifier() {
        // Override to provide a specific client identifier for testing
        return Converters.convertHexToByteArray("82 c1 45 d3 81 94");
    }

    @Override
    public byte[] hostName() {
        // Override to provide a specific host name for testing
        return Converters.convertStringToByteArray("MacBookPro");
    }

    @Override
    public byte[] hardwareType() {
        // Override to provide a specific hardware type for testing
        return super.hardwareType();
    }

    @Override
    public byte[] transactionId() {
        // Override to provide a specific transaction ID for testing
        return Converters.convertHexToByteArray("40 a4 72 2c");
    }

    public byte[] offerIp() {
        // Override to provide a specific offer IP for testing
        return Converters.convertHexToByteArray("0a 00 00 82");
    }

    public byte[] clientIp() {
        // Override to provide a specific offer IP for testing
        return Converters.convertHexToByteArray("00 00 00 00");
    }

    public byte[] serverIp() {
        // Override to provide a specific server IP for testing
        return Converters.convertHexToByteArray("0a 00 00 01");
    }
}
