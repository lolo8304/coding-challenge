package timezone;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TimezoneConverter {

    private final String source;
    private final String[] targets;
    private final String[] countries;
    private final String[] cities;
    private final List<TimezoneAbbr> targetTimezones;

    public TimezoneConverter(String source, String[] targets, String[] countries, String[] cities, List<TimezoneAbbr> targetTimezones) {
        this.source = source;
        this.targets = targets;
        this.countries = countries;
        this.cities = cities;
        this.targetTimezones = targetTimezones.stream().anyMatch(x -> !x.isAlias()) ? targetTimezones.stream().filter(x -> !x.isAlias()).toList() : targetTimezones;
    }

    public static TimezoneConverter fromTimezones(String source, String targets) {
        var timezoneArray = Arrays.stream(targets.split(",")).map(String::trim).toArray(String[]::new);
        return fromTimezones(source, timezoneArray);
    }

    public static TimezoneConverter fromTimezones(String source, String[] names) {
        String[] cities = {};
        var validZones = TimezoneDatabase.instance().getTimezoneNamesByNames(names);
        var zones = TimezoneDatabase.instance().getTimezonesByNames(validZones.toArray(String[]::new));
        return new TimezoneConverter(source, validZones.toArray(String[]::new), cities, cities, zones);
    }

    public static TimezoneConverter fromCities(String source, String[] cities) {
        String[] timezones = {};
        var zones = TimezoneDatabase.instance().getTimezoneLikeCities(cities);
        var timezoneIds = zones.stream().map(TimezoneAbbr::tzIdentifier).toArray(String[]::new);
        return new TimezoneConverter(source, timezones, timezones, timezoneIds, zones);
    }

    public static TimezoneConverter fromCities(String source, String cities) {
        var cityArray = Arrays.stream(cities.split(",")).map(String::trim).toArray(String[]::new);
        return fromCities(source, cityArray);
    }

    public static TimezoneConverter fromCountries(String source, String[] countries) {
        String[] timezones = {};
        var zones = TimezoneDatabase.instance().getTimezoneByCountries(countries);
        var countryIds = zones.stream().flatMap(x -> Arrays.stream(x.countries())).toArray(String[]::new);
        countryIds = Arrays.stream(countryIds).filter(x -> Arrays.asList(countries).contains(x)).toArray(String[]::new);
        countryIds = new HashSet<>(List.of(countryIds)).toArray(String[]::new);
        return new TimezoneConverter(source, timezones, countryIds, timezones, zones);
    }

    public static TimezoneConverter fromCountries(String source, String countries) {
        var countryArray = Arrays.stream(countries.split(",")).map(String::trim).toArray(String[]::new);
        return fromCountries(source, countryArray);
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

    public List<TimezoneAbbr> getTargetTimezones() {
        return targetTimezones;
    }

    public void logCurrentTime(Instant utcTime) {
        System.out.println("Current time: " + utcTime);
    }

    public void run(Instant utcTime) {
        logCurrentTime(utcTime);
        System.out.println("Source timezone: " + source);
        System.out.println("Target timezones: " + String.join(", ", targets));
        System.out.println("Target countries: " + String.join(", ", countries));
        System.out.println("Target cities: " + String.join(", ", cities));
        System.out.println("Zones: \n" + String.join("\n", targetTimezones.stream().map(TimezoneAbbr::toString).toList()));
        var sortedList = new HashSet<TimezoneOffset>();
        for (var zone : targetTimezones) {
            var currentZone = zone.timezoneOffset(utcTime);
            sortedList.add(currentZone);
        }
        var list = new ArrayList<>(sortedList.stream().toList());
        list.sort(TimezoneOffset::compareTo);
        for (var zone : list) {
            System.out.println(zone);
        }
    }

}
