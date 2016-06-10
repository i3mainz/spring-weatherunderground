/**
 * 
 */
package de.i3mainz.springframework.xd.modules.weatherunderground.filter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.integration.annotation.Filter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @author Nikolai Bock
 *
 */
public class TimeRangeFilter {

    private int timeRange = 5;

    /**
     * @param timeRange the timeRange to set
     */
    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
    }



    @Filter
    public boolean inTimeRange(@Payload Map observation, @Header("mappingTimestamp") LocalDateTime searchedTime) {

        Map<String, String> date = (Map<String, String>) observation.get("date");
        LocalDateTime observationTime = LocalDateTime.of(new Integer(date.get("year").toString()),
                new Integer(date.get("mon")), new Integer(date.get("mday")), new Integer(date.get("hour")),
                new Integer(date.get("min")), 0, 0);

        Duration period = Duration.between(searchedTime, observationTime);
        return period.abs().toMinutes() <= timeRange;
    }

}
