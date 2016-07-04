/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.Expression;

import lombok.Data;

/**
 * @author Nikolai Bock
 *
 */
@Data
@ConfigurationProperties(prefix="weatherunderground")
public class WeatherundergroundProcessorProperties {
    /**
     * Mode of data request (LIVE, HISTORY, FORECAST, ...)
     */
    private String mode = "LIVE";
    /**
     * API-Key for the weatherunderground service
     */
    private String apikey;
    /**
     * Used service query (SpEL)
     */
    private Expression query;
    /**
     * Position info (SpEL)
     */
    private String position;
}
