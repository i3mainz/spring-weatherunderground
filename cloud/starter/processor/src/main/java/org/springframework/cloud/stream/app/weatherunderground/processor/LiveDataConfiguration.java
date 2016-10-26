/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor;

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
@EnableConfigurationProperties({ WeatherundergroundProcessorProperties.class })
@ConditionalOnProperty(name = "weatherunderground.mode", havingValue = "LIVE")
public class LiveDataConfiguration {

    @Autowired
    private WeatherundergroundProcessorProperties properties;

    @Value("http://api.wunderground.com/api/${weatherunderground.apikey}/conditions/q/{query}.json")
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
    public IntegrationFlow setup() {
        return IntegrationFlows.from(start).channel(processChannel).get();
    }

    @Bean
    public IntegrationFlow dataProcess() {
        return IntegrationFlows.from(queryChannel)
                .handle(Http.outboundGateway(uri).httpMethod(HttpMethod.GET).expectedResponseType(Map.class)
                        .uriVariable("query", properties.getQuery()))
                .enrichHeaders(h -> h.headerExpression("location",
                        "'POINT('+payload.current_observation.observation_location.longitude+' '+payload.current_observation.observation_location.latitude+')'",
                        true))
                .channel(preOut).get();
    }
}
