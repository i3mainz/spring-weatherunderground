/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author Nikolai Bock
 *
 */
@Data
@ConfigurationProperties(prefix = "weatherunderground.search")
public class WeatherundergroundProcessorSearchProperties {
    /**
     * Whether private weatherstations nearby should be used.
     */
    private boolean nearbypws = false;
}
