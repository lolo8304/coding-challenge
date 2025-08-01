package dhcp;

import dhcp.message.Converters;

import java.net.NetworkInterface;
import java.util.Arrays;

public class YourClient {

    protected static YourClient instance;
    protected YourClient() {
        // Private constructor to prevent instantiation
    }
    public static YourClient getInstance() {
        if (instance == null) {
            instance = new YourClient();
        }
        return instance;
    }

    public byte[] clientIdentifier() {
        try {
            var nif = NetworkInterface.getNetworkInterfaces().nextElement();
            var mac = nif.getHardwareAddress();
            return Arrays.copyOf(mac, 6);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get MAC address", e);
        }
    }

    public byte[] hostName() {
        try {
            var hostName = java.net.InetAddress.getLocalHost().getHostName();
            return hostName.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Unable to get host name", e);
        }
    }

    public byte[] hardwareType() {
        // The hardware type is typically a 2-byte value.
        // Assuming Ethernet hardware type
        return Converters.convertUIntToByteArray(1); // 1 for Ethernet
    }

    public byte[] transactionId() {
        // Generate a random transaction ID
        byte[] transactionId = new byte[4];
        new java.util.Random().nextBytes(transactionId);
        return transactionId;
    }

}
