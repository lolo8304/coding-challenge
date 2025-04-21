package timezone;

public record TimeResponse(String id, String offset, String time) {
    @Override
    public String toString() {
        return String.format("TimeResponse{id='%s', offset='%s', time='%s'}", id, offset, time);
    }
}
