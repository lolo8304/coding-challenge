package timezone;

import java.util.List;

public record TimeConverterResponse(TimeResponse source, List<TimeResponse> targets) {
    @Override
    public String toString() {
        return String.format("TimeConverterResponse{source=%s, targets=%s}", source, targets);
    }
}