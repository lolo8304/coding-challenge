package timezone;

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
}
