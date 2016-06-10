/**
 * 
 */
package de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor;

import java.util.concurrent.BlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.i3mainz.springframework.cloud.stream.app.weatherunderground.processor.WeatherUndergroundProcessorConfiguration;

/**
 * Some nice integration tests for WeatherUnderground processor
 * 
 * @author Nikolai Bock
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WeatherUndergroundProcessorIntegrationTests.WeatherUndergroundProcessorApplication.class)
@IntegrationTest("{server.port=-1}")
@DirtiesContext
public abstract class WeatherUndergroundProcessorIntegrationTests {

    @Autowired
    @Bindings(WeatherUndergroundProcessorConfiguration.class)
    protected Processor processor;

    @Autowired
    protected MessageCollector messageCollector;

    @WebIntegrationTest({ "weatherunderground.search.nearbypws=false",
            "weatherunderground.query=new String(\"de/Mainz\")", "weatherunderground.mode=LIVE" })
    public static class TestWeatherUndergroundLiveRequest extends WeatherUndergroundProcessorIntegrationTests {
        @Test
        public void testInsert() throws JsonProcessingException {
            System.out.println("Hallo");
            processor.input().send(new GenericMessage<String>("Hallo Welt"));
            BlockingQueue<Message<?>> messages = messageCollector.forChannel(processor.output());
            System.out.println(messages.size());
            messages.forEach(m -> System.out.println(m.getPayload()));
        }
    }

    @SpringBootApplication
    public static class WeatherUndergroundProcessorApplication {

    }
}
