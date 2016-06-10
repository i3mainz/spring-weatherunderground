/**
 * 
 */
package de.i3mainz.springframework.xd.modules.weatherunderground.util;

import org.geotools.referencing.GeodeticCalculator;

/**
 * @author Nikolai Bock
 *
 */
public class DistanceCalculator {

    public double calcDist(String startPoint, double longitude, double latitude) {

        GeodeticCalculator calc = new GeodeticCalculator();
        String[] latlon = startPoint.split(",", 2);
        double latitudeStart = new Double(latlon[0]);
        double longitudeStart = new Double(latlon[1]);
        calc.setStartingGeographicPoint(longitudeStart, latitudeStart);
        calc.setDestinationGeographicPoint(longitude, latitude);

        return calc.getOrthodromicDistance();

    }

}
