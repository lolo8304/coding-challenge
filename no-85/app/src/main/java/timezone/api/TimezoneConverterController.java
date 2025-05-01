package timezone.api;
import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import timezone.TimezoneAbbrJsonAdapter;
import timezone.TimezoneDatabase;
import timezone.TimezoneAbbr;

import java.time.Instant;

public class TimezoneConverterController {

    private final Gson gson;
    public TimezoneConverterController() {
        // Initialize the controller
        this.gson = new GsonBuilder()
                .registerTypeAdapter(TimezoneAbbr.class, new TimezoneAbbrJsonAdapter())
                .create();
        setupInit();
        setupCors();
        setupRoutesStaticData();
        setupRoutesConverter();
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
    void setupRoutesConverter() {
        get("/hello", (req, res) -> "Hello World");
        post("/timezone-converter", (req, res) -> {
            String utcParam = req.queryParams("utc");
            if (utcParam == null) {
                utcParam = Instant.now().toString();
            }
            String hours = req.queryParams("hours");
            if (hours == null) {
                hours = "8";
            }
            int nofHours = Integer.parseInt(hours);
            var bodyString = req.body();
            if (bodyString == null || bodyString.isBlank()) {
                halt(400, "Invalid request body");
                return null;
            }

            var body = gson.fromJson(bodyString, TimeZoneRequest.class);

            var instant = Instant.parse(utcParam);
            var result = body.toTimezoneConverter().run(instant, nofHours);
            res.type("application/json");
            if (result == null) {
                halt(400, "Invalid request");
                return null;
            } else {
                return gson.toJson(result);
            }
        });
    }

    void setupRoutesStaticData() {
        get("/timezones", (req, res) -> {
            var timezones = TimezoneDatabase.instance().getTimezoneIds();
            res.type("application/json");
            return this.gson.toJson(timezones);
        });
        get("/countries", (req, res) -> {
            var timezones = TimezoneDatabase.instance().getTimezoneCountries();
            res.type("application/json");
            return this.gson.toJson(timezones);
        });
        // get /cities?name=<city>
        get("/cities", (req, res) -> {
            var name = req.queryParams("name");
            if (name == null || name.isBlank()) {
                return this.gson.toJson(new String[]{});
            }
            var timezones = TimezoneDatabase.instance().getTimezoneLikeCities(new String[]{name});
            res.type("application/json");
            return this.gson.toJson(timezones);
        });
    }
}
