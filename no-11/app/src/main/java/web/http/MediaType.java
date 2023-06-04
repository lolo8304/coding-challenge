package web.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MediaType {

    private static final String APPLICATION = "application";
    private String type;
    private String subtype;
    private Map<String, String> parameters;

    private static final Map<String, String> emptyMap = Collections.emptyMap();

    public static final String MEDIA_TYPE_WILDCARD = "*";

    public static final String WILDCARD = "*/*";
    public static final MediaType WILDCARD_TYPE = new MediaType();

    public static final String APPLICATION_XML = APPLICATION + "/xml";
    public static final MediaType APPLICATION_XML_TYPE = new MediaType(APPLICATION, "xml");
    public static final String APPLICATION_ATOM_XML = APPLICATION + "/atom+xml";
    public static final MediaType APPLICATION_ATOM_XML_TYPE = new MediaType(APPLICATION, "atom+xml");
    public static final String APPLICATION_XHTML_XML = APPLICATION + "/xhtml+xml";
    public static final MediaType APPLICATION_XHTML_XML_TYPE = new MediaType(APPLICATION, "xhtml+xml");
    public static final String APPLICATION_SVG_XML = APPLICATION + "/svg+xml";
    public static final MediaType APPLICATION_SVG_XML_TYPE = new MediaType(APPLICATION, "svg+xml");
    public static final String APPLICATION_JSON = APPLICATION + "/json";
    public static final MediaType APPLICATION_JSON_TYPE = new MediaType(APPLICATION, "json");
    public static final String APPLICATION_FORM_URLENCODED = APPLICATION + "/x-www-form-urlencoded";
    public static final MediaType APPLICATION_FORM_URLENCODED_TYPE = new MediaType(APPLICATION,
            "x-www-form-urlencoded");
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final MediaType MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");
    public static final String APPLICATION_OCTET_STREAM = APPLICATION + "/octet-stream";
    public static final MediaType APPLICATION_OCTET_STREAM_TYPE = new MediaType(APPLICATION, "octet-stream");
    public static final String TEXT_PLAIN = "text/plain";
    public static final MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");
    public static final String TEXT_XML = "text/xml";
    public static final MediaType TEXT_XML_TYPE = new MediaType("text", "xml");
    public static final String TEXT_HTML = "text/html";
    public static final MediaType TEXT_HTML_TYPE = new MediaType("text", "html");

    /*
     * parse media types in form
     * application/json; charset=utf-8; boundary=abc123
     */
    public static MediaType valueOf(String typeString) throws IllegalArgumentException {
        if (typeString != null && !typeString.isEmpty() && !typeString.isBlank()) {
            var splitted = typeString.split(";");
            if (splitted.length > 0) {
                var splittedType = splitted[0].split("/");
                if (splittedType.length == 2) {
                    var type = splittedType[0].trim();
                    var subType = splittedType[1].trim();
                    if (splitted.length > 1) {
                        return valueOfWithParameters(splitted, type, subType);
                    } else {
                        return new MediaType(type, subType);
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Media type invalid - no type or subtype - '" + typeString + "'");
                }
            }
        }
        throw new IllegalArgumentException("Invalid media type - '" + typeString + "'");
    }

    private static MediaType valueOfWithParameters(String[] splitted, String type, String subType) {
        var map = new HashMap<String, String>();
        for (String parameter : splitted) {
            var splittedParameter = parameter.split("=");
            map.put(splittedParameter[0].trim(), splittedParameter[1].trim());
        }
        return new MediaType(type, subType, map);
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
        this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;
        if (parameters == null) {
            this.parameters = emptyMap;
        } else {
            Map<String, String> map = new TreeMap<>(String::compareToIgnoreCase);
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
            this.parameters = Collections.unmodifiableMap(map);
        }
    }

    /**
     * Creates a new instance of MediaType with the supplied type and subtype.
     * 
     * @param type    the primary type, null is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     * @param subtype the subtype, null is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     */
    public MediaType(String type, String subtype) {
        this(type, subtype, emptyMap);
    }

    /**
     * Creates a new instance of MediaType, both type and subtype are wildcards.
     * Consider using the constant {@link #WILDCARD_TYPE} instead.
     */
    public MediaType() {
        this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for primary type.
     * 
     * @return value of primary type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Checks if the primary type is a wildcard.
     * 
     * @return true if the primary type is a wildcard
     */
    public boolean isWildcardType() {
        return this.getType().equals(MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for subtype.
     * 
     * @return value of subtype.
     */
    public String getSubtype() {
        return this.subtype;
    }

    /**
     * Checks if the subtype is a wildcard
     * 
     * @return true if the subtype is a wildcard
     */
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for a read-only parameter map. Keys are case-insensitive.
     * 
     * @return an immutable map of parameters.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Check if this media type is compatible with another media type. E.g.
     * image/* is compatible with image/jpeg, image/png, etc. Media type
     * parameters are ignored. The function is commutative.
     * 
     * @return true if the types are compatible, false otherwise.
     * @param other the media type to compare with
     */
    public boolean isCompatible(MediaType other) {
        if (other == null)
            return false;
        if (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD))
            return true;
        else if (type.equalsIgnoreCase(other.type)
                && (subtype.equals(MEDIA_TYPE_WILDCARD) || other.subtype.equals(MEDIA_TYPE_WILDCARD)))
            return true;
        else
            return this.type.equalsIgnoreCase(other.type)
                    && this.subtype.equalsIgnoreCase(other.subtype);
    }

    /**
     * Compares obj to this media type to see if they are the same by comparing
     * type, subtype and parameters. Note that the case-sensitivity of parameter
     * values is dependent on the semantics of the parameter name, see
     * {@link <a href=
     * "http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1</a>}.
     * This method assumes that values are case-sensitive.
     * 
     * @param obj the object to compare to
     * @return true if the two media types are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof MediaType))
            return false;
        MediaType other = (MediaType) obj;
        return (this.type.equalsIgnoreCase(other.type)
                && this.subtype.equalsIgnoreCase(other.subtype)
                && this.parameters.equals(other.parameters));
    }

    /**
     * Generate a hashcode from the type, subtype and parameters.
     * 
     * @return a hashcode
     */
    @Override
    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
    }

    /**
     * Convert the media type to a string suitable for use as the value of a
     * corresponding HTTP header.
     * 
     * @return a stringified media type
     */
    @Override
    public String toString() {
        var buffer = new StringBuilder();
        buffer.append(type).append('/').append(subtype);
        if (parameters.size() > 0) {
            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                buffer.append("; ").append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        return buffer.toString();
    }
}