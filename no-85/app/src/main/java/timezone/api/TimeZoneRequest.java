package timezone.api;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import timezone.TimezoneConverter;

@Getter
@Setter
@Builder(toBuilder = true)
public class TimeZoneRequest {
    private String source;
    private String[] countries;
    private String[] cities;
    private String[] timezones;

    public boolean hasAnyParameters() {
        return this.getCountries() != null && this.getCountries().length > 0 ||
                this.getCities() != null && this.getCities().length > 0 ||
                this.getTimezones() != null && this.getTimezones().length > 0;
    }

    public TimezoneConverter toTimezoneConverter() {
        if (this.getTimezones() != null && this.getTimezones().length > 0) {
            return TimezoneConverter.fromTimezones(this.getSource(), this.getTimezones());
        }
        if (this.getCities() != null && this.getCities().length > 0) {
            return TimezoneConverter.fromCities(this.getSource(), this.getCities());
        }
        if (this.getCountries() != null && this.getCountries().length > 0) {
            return TimezoneConverter.fromCountries(this.getSource(), this.getCountries());
        }
        return TimezoneConverter.fromTimezones(this.source, this.source);
    }
}
