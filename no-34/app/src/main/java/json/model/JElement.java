package json.model;

public abstract class JElement implements  Serializer {
    @Override
    public String toString() {
        return this.serialize(new JsonBuilder()).toString();
    }

}
