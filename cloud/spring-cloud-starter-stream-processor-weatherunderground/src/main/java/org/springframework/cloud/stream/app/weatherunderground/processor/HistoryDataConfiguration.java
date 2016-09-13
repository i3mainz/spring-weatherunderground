/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor;

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
import org.springframework.weatherunderground.integration.filter.TimeRangeFilter;
import org.springframework.weatherunderground.integration.transformer.FindBestTimeFitTransformer;
import org.springframework.weatherunderground.util.DateParser;

/**
 * @author Nikolai Bock
 *
 */
@Configuration
@EnableConfigurationProperties({ WeatherundergroundProcessorProperties.class,
        WeatherundergroundProcessorResultProperties.class })
@ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "HISTORY")
public class HistoryDataConfiguration {

    @Autowired
    private WeatherundergroundProcessorProperties properties;

    @Value("http://api.wunderground.com/api/${weatherunderground.apikey}/history_{date}/q/{query}.json")
    private String uri;

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
        return MessageChannels.direct().get();
    }

    @Bean
    public DateParser dateConverter() {
        return new DateParser();
    }

    @Bean
    public IntegrationFlow setup() {
        return IntegrationFlows.from(start)
                .enrichHeaders(h -> h.headerExpression("mappingTimestamp",
                        "@dateConverter.createDate(" + properties.getDate()
                                + "," + properties.getDateFormat() + ")"))
                .channel(processChannel).get();
    }

    @Bean
    public IntegrationFlow process() {
        return IntegrationFlows.from(queryChannel)
                .handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET)
                        .expectedResponseType(Map.class)
                        .uriVariable("query", properties.getQuery())
                        .uriVariable("date",
                                "@dateConverter.parse(headers['mappingTimestamp'],'yyyMMdd')"))
                .channel(mergingChannel()).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "NEAREST")
    public FindBestTimeFitTransformer bestFitTransformer() {
        return new FindBestTimeFitTransformer();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "NEAREST")
    public IntegrationFlow fitNearest(FindBestTimeFitTransformer transformer) {
        return IntegrationFlows.from(mergingChannel()).transform(transformer)
                .channel(preOut).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "RANGE")
    public IntegrationFlow fitRange(
            @Value("${weatherunderground.result.fitTimeRange}") int timeRange) {
        TimeRangeFilter filter = new TimeRangeFilter();
        filter.setTimeRange(timeRange);
        return IntegrationFlows.from(mergingChannel())
                .split("payload.history.observations").filter(filter)
                .channel(preOut).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.result.fit", havingValue = "SUMMARY")
    public IntegrationFlow fitSummary() {
        return IntegrationFlows.from(mergingChannel())
                .transform("payload.history.dailysummary").channel(preOut)
                .get();
    }

}
