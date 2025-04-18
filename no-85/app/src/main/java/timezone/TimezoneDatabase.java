package timezone;

import java.io.*;
import java.util.*;

public class TimezoneDatabase {

    private static TimezoneDatabase instance = null;
    private final List<TimezoneAbbr> timezones;
    private final Map<String, List<TimezoneAbbr>> timezonesByAbbr;
    private final Map<String, List<TimezoneAbbr>> timezonesById;

    private TimezoneDatabase() {
        timezones = new ArrayList<>();
        timezonesByAbbr = new HashMap<>();
        timezonesById = new HashMap<>();
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
                        // Add timezone to the map using the tzIdentifier as key
                        var alias = tz.backwardAliasName();
                        if (!tz.isBackward()) {
                            timezonesById.computeIfAbsent(tz.tzIdentifier(), k -> new ArrayList<>()).add(tz);
                            // Add timezone to the map using both standard and daylight saving time abbreviations
                            if (tz.timezoneSdt() != null && !tz.timezoneSdt().isBlank()) {
                                timezonesByAbbr.computeIfAbsent(tz.timezoneSdt(),k -> new ArrayList<>()).add(tz);
                            }
                            if (tz.timezoneDst() != null && !tz.timezoneDst().isBlank()) {
                                if (!tz.timezoneDst().equals(tz.timezoneSdt())) {
                                    timezonesByAbbr.computeIfAbsent(tz.timezoneDst(), k -> new ArrayList<>()).add(tz);
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
        return result.stream().filter(x -> !x.isAlias()).toList();
    }

    public List<String> getTimezoneByNames(String[] names) {
        Set<String> result = new HashSet<>();
        for (var name : names) {
            var zone = timezonesByAbbr.get(name.toUpperCase());
            if (zone != null) {
                result.addAll(zone.stream().flatMap(TimezoneAbbr::timezoneStream).toList());
            }
        }
        return new ArrayList<>(result);
    }

}
