package memcached.commands;

public enum ValidationCode {
    OK("OK"),
    STORED("STORED"),
    DELETED("DELETED"),
    ERROR("ERROR"),
    EXISTS("EXISTS"),
    NOT_FOUND("NOT_FOUND"),
    NOT_STORED("NOT_STORED"),
    CLIENT_ERROR_CANNOT_INCREMENT_OR_DECREMENT_NON_NUMERIC_VALUE(
            "CLIENT_ERROR cannot increment or decrement non-numeric value");

    private String value;

    private ValidationCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}