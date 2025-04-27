package timezone.api;
import static spark.Spark.*;

import com.google.gson.Gson;
import timezone.TimezoneConverter;

import java.time.Instant;

public class TimezoneConverterController {

    public TimezoneConverterController() {
        // Initialize the controller
        setupInit();
        setupCors();
        setupRoutes();
    }
    void setupInit() {
        var portEnv = System.getenv("PORT");
        var port = Integer.parseInt(portEnv != null && !portEnv.isBlank() ? portEnv : "4567");
        System.out.printf("Starting server on port %d%n", port);

        port(port);
    }
    void setupCors() {
        // CORS setup
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });
        // Handle pre-flight requests (important!)
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
    }
    void setupRoutes() {
        get("/hello", (req, res) -> "Hello World");
        post("/timezone-converter", (req, res) -> {
            String utcParam = req.queryParams("utc");
            if (utcParam == null) {
                utcParam = Instant.now().toString();
            }
            var bodyString = req.body();
            if (bodyString == null || bodyString.isBlank()) {
                halt(400, "Invalid request body");
                return null;
            }

            Gson gson = new Gson();
            var body = gson.fromJson(bodyString, TimeZoneRequest.class);

            var instant = Instant.parse(utcParam);
            var result = body.toTimezoneConverter().run(instant);
            res.type("application/json");
            if (result == null) {
                halt(400, "Invalid request");
                return null;
            } else {
                return gson.toJson(result);
            }
        });
    }
}
