/**
 * 
 */
package de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.http.Http;
import org.springframework.messaging.MessageChannel;

import de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor.filter.FindBestTimeFitTransformer;
import de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor.filter.TimeRangeFilter;
import de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor.util.DateParser;

/**
 * @author Nikolai Bock
 *
 */
@Configuration
@EnableConfigurationProperties({ WeatherUndergroundProcessorProperties.class,
        WeatherUndergroundProcessorResultProperties.class })
@ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "HISTORY")
public class HistoryDataConfiguration {

    @Autowired
    private WeatherUndergroundProcessorProperties properties;
    
    @Autowired
    private MessageChannel start;

    @Autowired
    private MessageChannel preOut;

    @Autowired
    private MessageChannel processChannel;

    @Autowired
    private MessageChannel queryChannel;

    @Bean
    public MessageChannel mergingChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    public DateParser dateConverter() {
        return new DateParser();
    }

    @Bean
    public IntegrationFlow setup() {
        return IntegrationFlows.from(start)
                .enrichHeaders(h -> h.headerExpression("mappingTimestamp",
                        "@dateConverter.createDate(${weatherunderground.date},${weatherunderground.dateFormat})"))
                .channel(processChannel).get();
    }

    @Bean
    public IntegrationFlow process(
            @Value("http://api.wunderground.com/api/${weatherunderground.apikey}/history_{date}/q/{query}.json") URI uri) {
        return IntegrationFlows.from(queryChannel)
                .handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET).expectedResponseType(Map.class)
                        .uriVariable("query", properties.getQuery())
                        .uriVariable("date", "@dateConverter.parse(headers['mappingTimestamp'],'yyyMMdd')"))
                .channel(mergingChannel()).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "NEAREST")
    public IntegrationFlow fitNearest() {
        return IntegrationFlows.from(mergingChannel()).transform(new FindBestTimeFitTransformer()).channel(preOut)
                .get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "RANGE")
    public IntegrationFlow fitRange(@Value("${weatherunderground.result.fitTimeRange}") int timeRange) {
        TimeRangeFilter filter = new TimeRangeFilter();
        filter.setTimeRange(timeRange);
        return IntegrationFlows.from(mergingChannel()).split("payload.history.observations").filter(filter)
                .channel(preOut).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "SUMMARY")
    public IntegrationFlow fitSummary() {
        return IntegrationFlows.from(mergingChannel()).transform("payload.history.dailysummary").channel(preOut)
                .get();
    }

}
