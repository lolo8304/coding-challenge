package timezone;

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public record TimezoneAbbr(String countryCodes, String tzIdentifier, String comments, String type, String utcOffsetSdt,
                           String utcOffsetDst, String timezoneSdt, String timezoneDst, String source, String notes,
                           boolean isAlias) implements Comparable<TimezoneAbbr> {

    public TimezoneAbbr(String countryCodes, String tzIdentifier, String comments, String type, String utcOffsetSdt,
                        String utcOffsetDst, String timezoneSdt, String timezoneDst, String source, String notes) {
        this(countryCodes, tzIdentifier, comments, type, utcOffsetSdt, utcOffsetDst, timezoneSdt, timezoneDst, source, notes, false);
    }

    @Override
    public int compareTo(TimezoneAbbr other) {
        int result = this.tzIdentifier.compareTo(other.tzIdentifier);
        if (result == 0) {
            result = this.countryCodes.compareTo(other.countryCodes);
        }
        return result;
    }

    public static Comparator<TimezoneAbbr> getComparator() {
        return Comparator.comparing(TimezoneAbbr::timezoneOffsetSdt).thenComparing(TimezoneAbbr::timezoneOffsetDst).thenComparing(TimezoneAbbr::tzIdentifier);
    }

    public static Optional<TimezoneAbbr> fromTzLine(String tzLine) {
        if (tzLine == null || tzLine.isBlank()) {
            return Optional.empty();
        }
        if (tzLine.endsWith(";")) {
            tzLine += " ";
        }
        String[] parts = tzLine.split(";");
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid timezone line: " + tzLine);
        }
        var tz = new TimezoneAbbr(
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
        if (tz.type.equals("Link†")) {
            return Optional.empty();
        }
        return Optional.of(tz);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s/%s - SDT: %s (%s), DST: %s (%s) - comment: %s",
                tzIdentifier, countryCodes, timezoneSdt, timezoneDst, utcOffsetSdt, type, utcOffsetDst, type, comments
        );
    }

    public String[] countries() {
        return Arrays.stream(this.countryCodes.split(",")).map(String::trim).toArray(String[]::new);
    }

    public boolean isBackward() {
        return "backward".equals(source);
    }

    // extracts: "Link to" from "Link to Africa/Abidjan"
    public Optional<String> backwardAliasName() {
        var text = "Link to ";
        var linkTextPos = notes.indexOf(text);
        if (linkTextPos >= 0) {
            return Optional.of(notes.substring(linkTextPos + text.length()));
        }
        return Optional.empty();
    }

    public TimezoneAbbr makeAlias() {
        return new TimezoneAbbr(countryCodes, tzIdentifier, comments, type, utcOffsetSdt, utcOffsetDst, timezoneSdt, timezoneDst, source, notes, true);
    }

    public Stream<String> timezoneStream() {
        if (timezoneSdt != null && timezoneDst != null && !timezoneSdt.isBlank() && !timezoneDst.isBlank()) {
            return Stream.of(timezoneSdt, timezoneDst);
        } else if (timezoneSdt != null && !timezoneSdt.isBlank()) {
            return Stream.of(timezoneSdt);
        } else if (timezoneDst != null && !timezoneDst.isBlank()) {
            return Stream.of(timezoneDst);
        } else {
            return Stream.of();
        }
    }

    public TimezoneOffset timezoneOffset(Instant currentTime) {
        ZoneId zoneId = ZoneId.of(this.tzIdentifier());
        ZoneRules rules = zoneId.getRules();

        var actualOffset = rules.getOffset(currentTime);
        var standardOffset = rules.getStandardOffset(currentTime);

        // Compare actual offset with standard to determine DST
        if (!actualOffset.equals(standardOffset)) {
            return new TimezoneOffset(this.utcOffsetDst); // Daylight Saving Time
        } else {
            return new TimezoneOffset(this.utcOffsetSdt); // Standard Time
        }
    }

    public TimezoneAbbr getAliasTimezone() {
        if (!isAlias) {
            return this;
        }
        var alias = backwardAliasName();
        if (alias.isPresent()) {
            var names = new String[] { alias.get() };
            return TimezoneDatabase.instance().getTimezonesByNames(names).stream().filter(x -> !x.isAlias()).findFirst().orElse(this);
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof TimezoneAbbr other && this.tzIdentifier.equals(other.tzIdentifier));
    }

    @Override
    public int hashCode() {
        return tzIdentifier.hashCode();
    }

    public TimezoneOffset timezoneOffsetSdt() {
        return new TimezoneOffset(this.utcOffsetSdt);
    }
    public TimezoneOffset timezoneOffsetDst() {
        return this.utcOffsetDst != null && !this.utcOffsetDst.isBlank() ? new TimezoneOffset(this.utcOffsetDst) : this.timezoneOffsetSdt();
    }
}
