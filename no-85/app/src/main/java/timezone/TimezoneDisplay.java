package timezone;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TimezoneDisplay {

    private final String source;
    private final String[] targets;
    private final String[] cities;

    public static TimezoneDisplay fromTimezones(String source, String targets) {
        var timezoneArray = Arrays.stream(targets.split(",")).map(String::trim).toArray(String[]::new);
        return fromTimezones(source, timezoneArray);
    }
    public static TimezoneDisplay fromTimezones(String source, String[] zones) {
        String[] cities = {};
        var validZones = TimezoneDatabase.instance().getTimezoneByNames(zones);
        return new TimezoneDisplay(source, validZones.toArray(String[]::new), cities);
    }
    public static TimezoneDisplay fromCities(String source, String[] cities) {
        String[] timezones = {};
        var timezoneIds = TimezoneDatabase.instance().getTimezoneLikeCities(cities).stream().map(TimezoneAbbr::tzIdentifier).toArray(String[]::new);
        return new TimezoneDisplay(source, timezones, timezoneIds);
    }
    public static TimezoneDisplay fromCities(String source, String cities) {
        var cityArray = Arrays.stream(cities.split(",")).map(String::trim).toArray(String[]::new);
        return fromCities(source, cityArray);
    }

    public TimezoneDisplay(String source, String[] targets, String[] cities) {
        this.source = source;
        this.targets = targets;
        this.cities = cities;
    }
    public TimezoneDisplay(String source, String targets, String cities) {
        this.source = source;
        this.targets = Arrays.stream(targets.split(",")).map(String::trim).toArray(String[]::new);
        this.cities = Arrays.stream(cities.split(",")).map(String::trim).toArray(String[]::new);
    }

    public String getSource() {
        return source;
    }
    public String[] getTargets() {
        return targets;
    }
    public String[] getCities() {
        return cities;
    }

    public void logCurrentTime() {
        var now = ZonedDateTime.now(ZoneOffset.UTC);
        System.out.println("Current time: " + now);
    }

    public void run() {
        logCurrentTime();
        System.out.println("Source timezone: " + source);
        System.out.println("Target timezones: " + String.join(", ", targets));
        System.out.println("Target cities: " + String.join(", ", cities));
    }

}
