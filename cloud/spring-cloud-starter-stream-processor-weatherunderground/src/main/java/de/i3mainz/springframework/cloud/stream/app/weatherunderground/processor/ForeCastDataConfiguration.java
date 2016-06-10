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
import org.springframework.cloud.stream.config.SpelExpressionConverterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.http.Http;
import org.springframework.messaging.MessageChannel;

/**
 * @author Nikolai Bock
 *
 */
@Configuration
@Import(SpelExpressionConverterConfiguration.class)
@EnableConfigurationProperties(value = { WeatherUndergroundProcessorProperties.class })
public class ForeCastDataConfiguration {

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
    @ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "FORECAST")
    public IntegrationFlow setupForecast() {
        return IntegrationFlows.from(start).channel(processChannel).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "HOURLY")
    public IntegrationFlow setupHourly() {
        return IntegrationFlows.from(start).channel(processChannel).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "FORECAST")
    public IntegrationFlow createForecastRequest(
            @Value("http://api.wunderground.com/api/${weatherunderground.apikey}/forecast/q/{query}.json") URI uri) {
        return IntegrationFlows
                .from(queryChannel).handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET)
                        .expectedResponseType(Map.class).uriVariable("query", properties.getQuery()))
                .channel(preOut).get();
    }

    @Bean
    @ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "HOURLY")
    public IntegrationFlow createHourlyRequest(
            @Value("http://api.wunderground.com/api/${weatherunderground.apikey}/hourly/q/{query}.json") URI uri) {
        return IntegrationFlows.from("queryChannel").handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET)
                .expectedResponseType(Map.class).uriVariable("query", properties.getQuery())).channel("preOut").get();
    }
}
