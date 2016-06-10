/**
 * 
 */
package de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.Expression;

import lombok.Data;

/**
 * @author Nikolai Bock
 *
 */
@Data
@ConfigurationProperties(prefix="weatherunderground")
public class WeatherUndergroundProcessorProperties {
    private String mode = "LIVE";
    private String apikey;
    private Expression query;
    private String position;
}
