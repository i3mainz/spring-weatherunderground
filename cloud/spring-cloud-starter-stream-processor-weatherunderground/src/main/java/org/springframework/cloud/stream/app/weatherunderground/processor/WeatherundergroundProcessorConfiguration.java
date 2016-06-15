/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.weatherunderground.processor.util.DistanceCalculator;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.http.Http;
import org.springframework.messaging.MessageChannel;

/**
 * @author Nikolai Bock
 *
 */
@Configuration
@EnableConfigurationProperties({ WeatherundergroundProcessorProperties.class,
        WeatherundergroundProcessorSearchProperties.class })
@EnableBinding(Processor.class)
public class WeatherundergroundProcessorConfiguration {
    

    @Autowired
    private WeatherundergroundProcessorProperties properties;

    @Bean
    public MessageChannel start() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel processChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel queryChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel preOut() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow startFlow() {
        return IntegrationFlows.from(Processor.INPUT).channel(start()).get();
    }

    @Bean
    public IntegrationFlow endFlow() {
        return IntegrationFlows.from(preOut())
                .enrichHeaders(h -> h.defaultOverwrite(true).header("SensorService", "WeatherUnderground"))
                .channel(Processor.OUTPUT).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.search.nearbypws", havingValue = "true")
    public DistanceCalculator distanceCalc() {
        return new DistanceCalculator();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.search.nearbypws", havingValue = "true")
    public IntegrationFlow nearbyFlow(
            @Value("http://api.wunderground.com/api/${weatherunderground.apikey}/geolookup/q/{query}.json") URI uri) {
        return IntegrationFlows.from(processChannel())
                .enrichHeaders(h -> h.headerExpression("POSQUERY", properties.getPosition()))
                .handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET).expectedResponseType(Map.class)
                        .uriVariable("query", "headers['POSQUERY']"))
                .enrichHeaders(h -> h
                        .headerExpression("PWSQuery",
                                "new String('pws:').concat(payload.location.nearby_weather_stations.pws.station[0].id)")
                        .headerExpression("location",
                                "'POINT('+payload.location.nearby_weather_stations.pws.station[0].lon+' '+payload.location.nearby_weather_stations.pws.station[0].lat+')'")
                        .headerExpression("distance",
                                "@distanceCalc.calcDist(headers['POSQUERY'],payload.location.nearby_weather_stations.pws.station[0].lon,payload.location.nearby_weather_stations.pws.station[0].lat)"))
                .channel(queryChannel()).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.search.nearbypws", havingValue = "false")
    public IntegrationFlow queryFlow() {
        return IntegrationFlows.from(processChannel()).channel(queryChannel()).get();
    }
}
