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
    /**
     * Result variant (NEAREST, RANGE, SUMMARY)
     */
    private String fit;

    /**
     * Timerange in minutes when using result variant RANGE
     */
    private int fitTimeRange;
}
