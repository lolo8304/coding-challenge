package web.http.servlets;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class Routes {

    private final Set<Route> list;

    public Routes() {
        this.list = new TreeSet<>((x, y) -> {
            if (x.key.pathPattern.equals("*") && y.key.pathPattern.equals("*")) {
                return 0;
            }
            if (x.key.pathPattern.equals("*")) {
                return 1;
            }
            if (y.key.pathPattern.equals("*")) {
                return -1;
            }
            if (!x.key.pathPattern.equals(y.key.pathPattern)) {
                return y.key.pathPattern.length() - x.key.pathPattern.length();
            } else {
                return 0;
            }
        });
        this.add("*", "*", new NoServlet());
    }

    public Route add(String actionPattern, String pathPattern, WebServlet servlet) {
        var route = new Route(actionPattern, pathPattern, servlet);
        this.list.add(route);
        return route;
    }

    public Optional<Route> match(String action, String path) {
        Route[] filteredRoutes = this.list.stream().filter((x) -> x.match(action, path)).toArray(Route[]::new);
        return filteredRoutes.length > 0 ? Optional.of(filteredRoutes[0]) : Optional.empty();
    }

    public static class Route {
        public final RouteKey key;
        public final WebServlet servlet;

        public Route(RouteKey key, WebServlet servlet) {
            this.key = key;
            this.servlet = servlet;
        }

        public Route(String actionPattern, String pathPattern, WebServlet servlet) {
            this.key = new RouteKey(actionPattern, pathPattern);
            this.servlet = servlet;
        }

        public Route(String pathPattern, WebServlet servlet) {
            this(new RouteKey("*", pathPattern), servlet);
        }

        public Route(WebServlet servlet) {
            this("", servlet);
        }

        public boolean isMoreGeneric(Route route) {
            return this.key.isMoreGeneric(route.key);
        }

        public boolean match(String action, String path) {
            return this.key.match(action, path);
        }

    }

    public static class RouteKey {
        private final String actionPattern;
        private final String pathPattern;

        public RouteKey(String actionPattern, String pathPattern) {
            this.actionPattern = actionPattern;
            this.pathPattern = pathPattern;
        }

        /*
         * true, if current key is more generic rule than another key
         * false, if current is a part of anotherKey path
         * example
         * this: path /
         * other: path /test
         */
        public boolean isMoreGeneric(RouteKey anotherKey) {
            if (this.pathPattern.equals(anotherKey.pathPattern)) {
                throw new IllegalArgumentException("key is identical and not allowed");
            }
            if (this.pathPattern.equals("*")) {
                return true;
            }
            if (anotherKey.pathPattern.equals("*")) {
                return false;
            }
            if (anotherKey.pathPattern.startsWith(this.pathPattern)) {
                return true;
            }
            return false;
        }

        public boolean match(String action, String path) {
            var actionMatched = this.actionPattern.equals("*") || this.actionPattern.equalsIgnoreCase(action);
            var pathMatched = this.pathPattern.equals("*") || this.pathPattern.isEmpty()
                    || path.toLowerCase().startsWith(this.pathPattern.toLowerCase());
            return actionMatched && pathMatched;
        }
    }

}
