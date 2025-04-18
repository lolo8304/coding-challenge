package timezone;

import java.io.*;
import java.util.*;

public class TimezoneDatabase {

    private static TimezoneDatabase instance = null;
    private final List<TimezoneAbbr> timezones;
    private final Map<String, List<TimezoneAbbr>> timezonesByAbbr;
    private final Map<String, List<TimezoneAbbr>> timezonesById;
    private final Map<String, List<TimezoneAbbr>> timezonesByCountry;

    private TimezoneDatabase() {
        timezones = new ArrayList<>();
        timezonesByAbbr = new HashMap<>();
        timezonesById = new HashMap<>();
        timezonesByCountry = new HashMap<>();
        loadTimezones();
    }

    public static TimezoneDatabase instance() {
        if (instance == null) {
            instance = new TimezoneDatabase();
        }
        return instance;
    }

    private void loadTimezones() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("tz/timezones.csv")) {
            assert is != null;
            var fill2ndPass = new HashMap<String, List<TimezoneAbbr>>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] parts = line.split(";", -1); // -1 keeps trailing empty fields
                    if (parts.length >= 10) {
                        TimezoneAbbr tz = new TimezoneAbbr(
                                parts[0].trim(),
                                parts[1].trim(),
                                parts[2].trim(),
                                parts[3].trim(),
                                parts[4].trim(),
                                parts[5].trim(),
                                parts[6].trim(),
                                parts[7].trim(),
                                parts[8].trim(),
                                parts[9].trim()
                        );
                        if (tz.type().equals("Link†")) continue;
                        // Add timezone to the map using the tzIdentifier as key
                        var alias = tz.backwardAliasName();
                        if (!tz.isBackward()) {
                            this.timezonesById.computeIfAbsent(tz.tzIdentifier(), k -> new ArrayList<>()).add(tz);
                            // Add timezone to the map using both standard and daylight saving time abbreviations
                            if (tz.timezoneSdt() != null && !tz.timezoneSdt().isBlank()) {
                                this.timezonesByAbbr.computeIfAbsent(tz.timezoneSdt(),k -> new ArrayList<>()).add(tz);
                            }
                            if (tz.timezoneDst() != null && !tz.timezoneDst().isBlank()) {
                                if (!tz.timezoneDst().equals(tz.timezoneSdt())) {
                                    this.timezonesByAbbr.computeIfAbsent(tz.timezoneDst(), k -> new ArrayList<>()).add(tz);
                                }
                            }
                            timezones.add(tz);
                        } else if (alias.isPresent()) {
                            var existingBackLink = timezonesById.get(alias.get());
                            if (existingBackLink != null) {
                                timezonesById.computeIfAbsent(tz.tzIdentifier(), k -> new ArrayList<>()).add(existingBackLink.getFirst().makeAlias());
                            } else {
                                fill2ndPass.computeIfAbsent(alias.get(), k -> new ArrayList<>()).add(tz);
                            }
                        }
                        var countries = Arrays.stream(tz.countryCodes().split(",")).map(String::trim).toList();
                        for (var country : countries) {
                            timezonesByCountry.computeIfAbsent(country, k -> new ArrayList<>()).add(tz);
                        }
                    }
                }
            }
            for (var fill : fill2ndPass.entrySet()) {
                for (var tz : fill.getValue()) {
                    timezonesById.computeIfAbsent(fill.getKey(), k -> new ArrayList<>()).add(tz.makeAlias());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TimezoneAbbr> getTimezoneLikeCities(String[] cities) {
        List<TimezoneAbbr> result = new ArrayList<>();
        for (var city : cities) {
            String cityLower = city.toLowerCase();
            for (Map.Entry<String, List<TimezoneAbbr>> entry : timezonesById.entrySet()) {
                if (entry.getKey().toLowerCase().contains(cityLower)) {
                    result.addAll(entry.getValue());
                }
            }
        }
        return result.stream().filter(x -> true).toList();
    }
    public List<TimezoneAbbr> getTimezoneByCountries(String[] countries) {
        List<TimezoneAbbr> result = new ArrayList<>();
        for (var country : countries) {
            String cityUpper = country.toUpperCase();
            var tzCountries = timezonesByCountry.get(cityUpper);
            if (tzCountries != null) {
                result.addAll(tzCountries);
            }
        }
        return result;
    }

    public List<String> getTimezoneNamesByNames(String[] names) {
        Set<String> result = new HashSet<>();
        for (var name : names) {
            var zones = this.timezonesByAbbr.get(name.toUpperCase());
            if (zones != null) {
                result.addAll(zones.stream().flatMap(TimezoneAbbr::timezoneStream).toList());
            }
        }
        return new ArrayList<>(result);
    }

    public List<TimezoneAbbr> getTimezonesByNames(String[] names) {
        List<TimezoneAbbr> result = new ArrayList<>();
        for (var name : names) {
            var zones = this.timezonesByAbbr.get(name.toUpperCase());
            if (zones != null) {
                result.addAll(zones);
            }
        }
        return result;
    }

}
