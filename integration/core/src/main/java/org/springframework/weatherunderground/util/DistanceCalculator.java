/**
 * 
 */
package org.springframework.weatherunderground.util;

import org.geotools.referencing.GeodeticCalculator;

/**
 * @author Nikolai Bock
 *
 */
public class DistanceCalculator {

    public double calcDist(String startPoint, double longitude, double latitude) {

        GeodeticCalculator calc = new GeodeticCalculator();
        String[] latlon = startPoint.split(",", 2);
        double latitudeStart = Double.parseDouble(latlon[0]);
        double longitudeStart = Double.parseDouble(latlon[1]);
        calc.setStartingGeographicPoint(longitudeStart, latitudeStart);
        calc.setDestinationGeographicPoint(longitude, latitude);

        return calc.getOrthodromicDistance();

    }

}
