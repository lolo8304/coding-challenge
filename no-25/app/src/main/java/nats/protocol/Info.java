package nats.protocol;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// {"server_id\":\"NCXMJZYQEWUDJFLYLSTTE745I2WUNCVG3LJJ3NRKSFJXEG6RGK7753DJ\",\"version\":"2.0.0","proto":1,"go":"go1.11.10","host":"0.0.0.0","port":4222,"max_payload":1048576,"client_id":5089}
public class Info implements ICmd {
    private static Random random;
    private static final String SECURESTRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String SERVER_ID;

    @JsonProperty("server_id")
    public String serverId = SERVER_ID;
    @JsonProperty("version")
    public String version = "1.0.0";
    @JsonProperty("proto")
    public int protocol = 1;
    @JsonProperty("go")
    public String goVersion = "java:" + System.getProperty("java.version");
    @JsonProperty("host")
    public String host = "0.0.0.0";
    @JsonProperty("port")
    public int port;
    @JsonProperty("max_payload")
    public int maxPayload = 1048576;
    @JsonProperty("client_id")
    public int clientId;

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            random = new Random();
        }
        SERVER_ID = generateServerId();
    }

    private static String generateServerId() {
        var builder = new StringBuilder();
        for (int i = 0; i < 58; i++) {
            builder.append(random.nextInt(SECURESTRING.length()));
        }
        return builder.toString();
    }

    public Info(int clientId, int port) {
        this.clientId = clientId;
        this.port = port;
    }

    @Override
    public Optional<String> send() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of("INFO " + objectMapper.writeValueAsString(this) + EOL);
        } catch (JsonProcessingException e) {
            return Optional.of("ERROR " + e.getMessage() + EOL);
        }
    }

    @Override
    public Optional<String> execute() throws IOException {
        return this.send();
    }

}
