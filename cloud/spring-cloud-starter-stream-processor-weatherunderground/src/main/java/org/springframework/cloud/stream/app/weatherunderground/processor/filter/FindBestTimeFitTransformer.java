/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor.filter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @author Nikolai Bock
 *
 */
public class FindBestTimeFitTransformer {

    @Transformer
    public Map<String, Object> transform(@Payload(value = "history.observations") List<Map<String, Object>> payload,
            @Header("mappingTimestamp") LocalDateTime searchedTime) throws Exception {
        if (payload.size() == 1) {
            return payload.get(0);
        } else if (payload.size() > 1) {
            Duration lowest = Duration.ofDays(1);
            Map<String, Object> result = null;
            for (Map<String, Object> observation : payload) {
                Map<String, String> date = (Map<String, String>) observation.get("date");
                LocalDateTime observationTime = LocalDateTime.of(new Integer(date.get("year")),
                        new Integer(date.get("mon")), new Integer(date.get("mday")), new Integer(date.get("hour")),
                        new Integer(date.get("min")), 0, 0);
                Duration period = Duration.between(searchedTime, observationTime);
                if (period.compareTo(lowest) < 0) {
                    lowest = period;
                    result = observation;
                }
            }
            return result;
        }
        return null;
    }

}
