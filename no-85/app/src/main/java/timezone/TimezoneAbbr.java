package timezone;

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public record TimezoneAbbr(String countryCodes, String tzIdentifier, String comments, String type, String utcOffsetSdt,
                           String utcOffsetDst, String timezoneSdt, String timezoneDst, String source, String notes,
                           boolean isAlias) {

    public TimezoneAbbr(String countryCodes, String tzIdentifier, String comments, String type, String utcOffsetSdt,
                        String utcOffsetDst, String timezoneSdt, String timezoneDst, String source, String notes) {
        this(countryCodes, tzIdentifier, comments, type, utcOffsetSdt, utcOffsetDst, timezoneSdt, timezoneDst, source, notes, false);
    }
    // Getters

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

    public String timezoneOffset(Instant currentTime) {
        ZoneId zoneId = ZoneId.of(this.tzIdentifier());
        ZoneRules rules = zoneId.getRules();

        for (ZoneOffsetTransitionRule transitionRule : rules.getTransitionRules()) {
            ZoneOffsetTransition transition = transitionRule.createTransition(2025); // Übergang für 2025
            Instant transitionInstant = transition.getInstant();

            if (currentTime.isBefore(transitionInstant)) {
                // SDT
                return this.utcOffsetSdt;
            } else if (currentTime.equals(transitionInstant) || currentTime.isAfter(transitionInstant)) {
                // DST
                return this.utcOffsetDst;
            }
        }
        return this.utcOffsetSdt;
    }
}
