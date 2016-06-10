/**
 * 
 */
package de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author Nikolai Bock
 *
 */
@Data
@ConfigurationProperties(prefix = "weatherunderground.search")
public class WeatherUndergroundProcessorSearchProperties {
    private boolean nearbypws = false;
}
