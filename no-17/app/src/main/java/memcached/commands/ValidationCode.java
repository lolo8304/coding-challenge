package memcached.commands;

public enum ValidationCode {
    OK,
    STORED,
    DELETED,
    ERROR,
    EXISTS,
    NOT_FOUND,
    NOT_STORED
}