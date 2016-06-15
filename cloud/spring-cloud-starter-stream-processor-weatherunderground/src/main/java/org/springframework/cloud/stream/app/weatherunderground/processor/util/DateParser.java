/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Nikolai Bock
 *
 */
public class DateParser {

    public String parse(String oldDate, String oldPattern, String newPattern) {
        return parse(createDate(oldDate, oldPattern), newPattern);
    }

    public String parse(LocalDateTime createDate, String newPattern) {
        return createDate.format(DateTimeFormatter.ofPattern(newPattern));
    }

    public LocalDateTime createDate(Object oldDate, String oldPattern) {
        if (oldDate instanceof String) {
            return LocalDateTime.parse(oldDate.toString(), DateTimeFormatter.ofPattern(oldPattern));
        } else if (oldDate instanceof Date) {
            return createDate((Date) oldDate);
        } else {
            return LocalDateTime.now();
        }
    }

    private LocalDateTime createDate(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }

}
