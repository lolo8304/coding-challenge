package redis.resp.commands.library;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespSortedMap;

public abstract class RespLibraryFunction {
    private static final String[] EMPTY = new String[0];

    private final RespCommandLibrary library;
    private final String name;
    private final String[] subFunctions;

    protected RespLibraryFunction(String name, RespCommandLibrary library) {
        this.name = name;
        this.subFunctions = EMPTY;
        this.library = library;
    }

    protected RespLibraryFunction(String name, String[] subFunctions, RespCommandLibrary library) {
        this.name = name;
        this.subFunctions = subFunctions;
        this.library = library;
    }

    public abstract RespResponse execute(RespRequest request) throws RespCommandException;

    public RespResponse execute(RespRequest request, String subFunction) throws RespCommandException {
        throw new RespCommandException("Subfunction " + subFunction + " is not implemented");
    }

    public String getName() {
        return this.name;
    }

    public String[] getSubFunctions() {
        return this.subFunctions;
    }

    public boolean hasSubFunctions() {
        return this.subFunctions.length > 0;
    }

    public boolean hasSubFunction(String subFunction) {
        for (String string : subFunctions) {
            if (string.equalsIgnoreCase(subFunction)) {
                return true;
            }
        }
        return false;
    }

    public RespCommandLibrary getLibrary() {
        return this.library;
    }

    public abstract RespSortedMap getCommandDocs();
}
