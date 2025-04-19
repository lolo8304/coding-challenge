package timezone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                    var tzOpt = TimezoneAbbr.fromTzLine(line);
                    tzOpt.ifPresent(tz -> {
                        var alias = tz.backwardAliasName();
                        if (!tz.isBackward()) {
                            this.timezonesById.computeIfAbsent(tz.tzIdentifier(), _ -> new ArrayList<>()).add(tz);
                            // Add timezone to the map using both standard and daylight saving time abbreviations
                            if (tz.timezoneSdt() != null && !tz.timezoneSdt().isBlank()) {
                                this.timezonesByAbbr.computeIfAbsent(tz.timezoneSdt(), _ -> new ArrayList<>()).add(tz);
                            }
                            if (tz.timezoneDst() != null && !tz.timezoneDst().isBlank()) {
                                if (!tz.timezoneDst().equals(tz.timezoneSdt())) {
                                    this.timezonesByAbbr.computeIfAbsent(tz.timezoneDst(), _ -> new ArrayList<>()).add(tz);
                                }
                            }
                            timezones.add(tz);
                        } else alias.ifPresent(s -> fill2ndPass.computeIfAbsent(s, _ -> new ArrayList<>()).add(tz));
                        var countries = Arrays.stream(tz.countryCodes().split(",")).map(String::trim).toList();
                        for (var country : countries) {
                            timezonesByCountry.computeIfAbsent(country, _ -> new ArrayList<>()).add(tz);
                        }
                    });
                }
            }
            for (var fill : fill2ndPass.entrySet()) {
                var aliasId = fill.getKey();
                for (var tz : fill.getValue()) {
                    var tzId = tz.tzIdentifier();
                    timezonesById.computeIfAbsent(tzId, k -> new ArrayList<>()).add(timezonesById.get(aliasId).getFirst());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Object> getTimezoneById(String id) {
        var tz = this.timezonesById.get(id);
        if (tz != null && !tz.isEmpty()) {
            return Optional.of(tz.getFirst());
        }
        return Optional.empty();
    }

    public List<TimezoneAbbr> getTimezoneLikeCities(String[] cities) {
        List<TimezoneAbbr> result = new ArrayList<>();
        for (var city : cities) {
            var zone = this.getTimezoneById(city);
            if (zone.isPresent()) {
                result.add((TimezoneAbbr) zone.get());
            } else {
                String cityLower = city.toLowerCase();
                for (Map.Entry<String, List<TimezoneAbbr>> entry : timezonesById.entrySet()) {
                    if (entry.getKey().toLowerCase().contains(cityLower)) {
                        result.addAll(entry.getValue().stream().map(TimezoneAbbr::getAliasTimezone).toList());
                    }
                }
            }
        }
        return result;
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
