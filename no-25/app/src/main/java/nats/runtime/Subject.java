package nats.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subject {
    static final String REGEXP = "^((([a-zA-Z0-9_]+)|[*])[.])*([a-zA-Z0-9_]+|[>]?)$";
    static final Pattern PATTERN = Pattern.compile(REGEXP);

    public final String[] tokens;
    public final String subject;
    private final Pattern wildcardPattern;

    public Subject(String name) {
        this.subject = name;
        this.tokens = splitValidTokens(name);
        this.wildcardPattern = this.subject.contains("*") || this.subject.contains(">")
                ? Pattern.compile(String.format("^%s$",
                        this.subject.replace(".", "[.]").replace(">", "[^>]+").replace("*", "[^>*.]+")))
                : null;
    }

    public String[] splitValidTokens(String subject) {
        Matcher matcher = PATTERN.matcher(subject);
        if (matcher.find()) {
            return subject.split("\\.");
        } else {
            return new String[0];
        }
    }

    public boolean isValid() {
        return this.tokens != null && this.tokens.length > 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Subject other = (Subject) obj;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        return true;
    }

    public boolean wildcardTo(Subject anotherSubject) {
        return this.wildcardPattern != null && this.wildcardPattern.matcher(anotherSubject.subject).find();
    }

    private boolean hasWildcards() {
        return this.wildcardPattern != null;
    }

    private boolean hasNoWildcards() {
        return this.wildcardPattern == null;
    }

    public boolean subscribedTo(Subject anotherSubject) {
        return this.hasNoWildcards() ? this.equals(anotherSubject)
                : this.wildcardTo(anotherSubject);
    }
}
