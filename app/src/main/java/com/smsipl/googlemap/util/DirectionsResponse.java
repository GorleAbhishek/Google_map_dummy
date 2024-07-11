package com.smsipl.googlemap.util;

import java.util.List;

public class DirectionsResponse {
    public List<Route> routes;

    public static class Route {
        public double distance;
        public double duration;
        public String geometry;
    }
}


