package timezone;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record TimezoneOffset(String offsetMMHH, boolean plus) implements Comparable<Object> {
    public TimezoneOffset(String offset) {
        this(offset.substring(1), offset.charAt(0) == '+');
    }

    public static TimezoneOffset fromMinutes(int totalMinutes) {
        boolean positive = totalMinutes >= 0;
        totalMinutes = Math.abs(totalMinutes);

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        return new TimezoneOffset(String.format("%02d:%02d", hours, minutes), positive);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof TimezoneOffset(String otherMMYYY, boolean otherPlus)) {
            if (this.plus == otherPlus) return (this.plus ? 1 : -1) * this.offsetMMHH.compareToIgnoreCase(otherMMYYY);
            if (!this.plus) return -1;
            return 1;
        } else if (o instanceof String otherMMYYY) {
            return this.compareTo(new TimezoneOffset(otherMMYYY));
        }
        throw new RuntimeException("Cannot compare this object " + o);
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.plus ? "+" : "-", offsetMMHH);
    }

    public TimezoneOffset add(TimezoneOffset offset) {
        return TimezoneOffset.fromMinutes(this.toMinutes() + offset.toMinutes());
    }

    public TimezoneOffset substract(TimezoneOffset offset) {
        return TimezoneOffset.fromMinutes(this.toMinutes() - offset.toMinutes());
    }

    public int toMinutes() {
        String[] parts = this.offsetMMHH.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        int totalMinutes = hours * 60 + minutes;
        return this.plus ? totalMinutes : -totalMinutes;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof TimezoneOffset other && this.compareTo(other) == 0);
    }

    @Override
    public int hashCode() {
        return this.offsetMMHH.hashCode() + (this.plus ? 1 : -1);
    }

    public List<Map<String, String>> mapToTimezones(
            Instant startInstant,
            List<TimezoneAbbr> timezones,
            int hoursIncrement
    ) {
        int sourceOffsetMin = this.toMinutes();
        var targetOffsets = timezones.stream()
                .map( x-> x.timezoneOffset(startInstant))
                .toList();
        var targetOffsetsMin = targetOffsets.stream()
                .map( x-> x.toMinutes())
                .toList();

        List<Map<String, String>> result = new ArrayList<>();

        for (int i = 0; i < hoursIncrement; i++) {
            var row = new LinkedHashMap<String, String>();

            var utc = startInstant.plus(Duration.ofHours(i));
            var sourceTime = utc.plus(Duration.ofMinutes(sourceOffsetMin))
                    .atOffset(ZoneOffset.UTC);

            row.put("source_time", sourceTime.toString());
            row.put("source_offset", this.fromMinutes(sourceOffsetMin).toString());

            for (int j = 0; j < targetOffsetsMin.size(); j++) {
                var targetOffsetMin = targetOffsetsMin.get(j);

                // Compute time in target
                var targetTime = utc.plus(Duration.ofMinutes(targetOffsetMin))
                        .atOffset(ZoneOffset.UTC);

                // Offsets
                var offsetToUtc = fromMinutes(targetOffsetMin);
                var offsetToSource = fromMinutes(targetOffsetMin - sourceOffsetMin);

                row.put("target_" + j + "_id", timezones.get(j).tzIdentifier());
                row.put("target_" + j + "_time", targetTime.toString());
                row.put("target_" + j + "_offset_to_utc", offsetToUtc.toString());
                row.put("target_" + j + "_offset_to_source", offsetToSource.toString());
            }

            result.add(row);
        }

        return result;
    }
}
