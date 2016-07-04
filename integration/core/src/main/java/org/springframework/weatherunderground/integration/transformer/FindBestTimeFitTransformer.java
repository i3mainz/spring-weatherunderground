/**
 * 
 */
package org.springframework.weatherunderground.integration.transformer;

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
        // if (payload.size() == 1) {
        // return payload.get(0);
        // } else if (payload.size() > 1) {
        // Duration lowest = Duration.ofDays(1);
        // Map<String, Object> result = null;
        // for (Map<String, Object> observation : payload) {
        // Duration period = calcDuration(observation,searchedTime);
        // if (period.compareTo(lowest) < 0) {
        // lowest = period;
        // result = observation;
        // }
        // }
        // return result;
        //
        // }
        return payload.stream()
                .sorted((o1, o2) -> calcDuration(o1, searchedTime).compareTo(calcDuration(o2, searchedTime)))
                .findFirst().get();
        // return null;
    }

    private Duration calcDuration(Map<String, Object> observation, LocalDateTime searchedTime) {
        @SuppressWarnings("unchecked")
        Map<String, String> date = (Map<String, String>) observation.get("date");
        LocalDateTime observationTime = LocalDateTime.of(new Integer(date.get("year")), new Integer(date.get("mon")),
                new Integer(date.get("mday")), new Integer(date.get("hour")), new Integer(date.get("min")), 0, 0);
        return Duration.between(searchedTime, observationTime);
    }

}
