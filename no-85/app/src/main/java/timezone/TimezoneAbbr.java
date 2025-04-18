package timezone;

import java.util.Optional;
import java.util.stream.Stream;

public record TimezoneAbbr(String countryCodes, String tzIdentifier, String comments, String type, String utcOffsetSdt,
                           String utcOffsetDst, String timezoneSdt, String timezoneDst, String source, String notes, boolean isAlias) {

    public TimezoneAbbr(String countryCodes, String tzIdentifier, String comments, String type, String utcOffsetSdt,
                        String utcOffsetDst, String timezoneSdt, String timezoneDst, String source, String notes) {
        this(countryCodes, tzIdentifier, comments, type, utcOffsetSdt, utcOffsetDst, timezoneSdt, timezoneDst, source, notes, false);
    }
    // Getters

    @Override
    public String toString() {
        return String.format("%s [%s] %s/%s - SDT: %s (%s), DST: %s (%s)",
                tzIdentifier, countryCodes, timezoneSdt, timezoneDst, utcOffsetSdt, type, utcOffsetDst, type
        );
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
        } else if (timezoneDst != null && !timezoneDst.isBlank()){
            return Stream.of(timezoneDst);
        } else {
            return Stream.of();
        }
    }
}
