package timezone;

public record TimezoneOffset(String offsetMMHH, boolean plus) implements Comparable<TimezoneOffset> {
    public TimezoneOffset(String offset) {
        this(offset.substring(1), offset.charAt(0) == '+');
    }

    @Override
    public int compareTo(TimezoneOffset o) {
        if (o instanceof TimezoneOffset(String otherMMYYY, boolean otherPlus)) {
            if (this.plus == otherPlus) return (this.plus ? 1 : -1) * this.offsetMMHH.compareToIgnoreCase(otherMMYYY);
            if (!this.plus) return -1;
            return 1;
        }
        throw new RuntimeException("Cannot compare this object " + o);
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.plus ? "+" : "-", offsetMMHH);
    }
}
