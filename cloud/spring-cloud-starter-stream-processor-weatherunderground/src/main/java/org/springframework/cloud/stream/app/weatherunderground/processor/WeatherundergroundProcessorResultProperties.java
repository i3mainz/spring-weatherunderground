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
@ConfigurationProperties(prefix = "weatherunderground.result")
public class WeatherundergroundProcessorResultProperties {
    private String fit;
}
