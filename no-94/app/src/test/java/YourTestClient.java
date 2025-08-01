import dhcp.YourClient;
import dhcp.message.Converters;

public class YourTestClient extends YourClient {

    protected YourTestClient() {
        // Private constructor to prevent instantiation
    }

    public static YourClient getInstance() {
        if (instance == null) {
            instance = new YourTestClient();
        }
        return instance;
    }

    @Override
    public byte[] clientIdentifier() {
        // Override to provide a specific client identifier for testing
        return Converters.convertHexToByteArray("52 98 ee c8 86 c3");
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
        return Converters.convertHexToByteArray("40 a4 72 29");
    }
}
