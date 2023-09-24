package nats.protocol.commands;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nats.runtime.NatsContext;

public class Info implements ICmd {
    private static Random random;
    private static final String SECURESTRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String serverIdUnique;

    @JsonProperty("server_id")
    public String serverId = serverIdUnique;
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
        serverIdUnique = generateServerId();
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
    public Optional<String> print() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of("INFO " + objectMapper.writeValueAsString(this) + CRLF);
        } catch (JsonProcessingException e) {
            return Optional.of("ERROR " + e.getMessage() + CRLF);
        }
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        return this.print();
    }

}
