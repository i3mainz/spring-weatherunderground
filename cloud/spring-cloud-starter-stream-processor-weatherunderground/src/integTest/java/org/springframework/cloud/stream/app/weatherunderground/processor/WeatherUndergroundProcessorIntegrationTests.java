/**
 * 
 */
package org.springframework.cloud.stream.app.weatherunderground.processor;

import java.util.concurrent.BlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.app.weatherunderground.processor.WeatherundergroundProcessorConfiguration;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

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
    @Bindings(WeatherundergroundProcessorConfiguration.class)
    protected Processor processor;

    @Autowired
    protected MessageCollector messageCollector;

    @WebIntegrationTest({ "weatherunderground.search.nearbypws=false",
            "weatherunderground.query=new String(\"de/Mainz\")",
            "weatherunderground.mode=LIVE" })
    public static class TestWeatherUndergroundLiveRequest
            extends WeatherUndergroundProcessorIntegrationTests {
        @Test
        public void request() throws JsonProcessingException {
            processor.input().send(new GenericMessage<String>(""));
            BlockingQueue<Message<?>> messages = messageCollector
                    .forChannel(processor.output());
            messages.stream().map(Message::getPayload)
                    .forEach(System.out::println);
        }
    }

    @WebIntegrationTest({ "weatherunderground.search.nearbypws=false",
            "weatherunderground.query=new String(\"de/Mainz\")",
            "weatherunderground.mode=HISTORY",
            "weatherunderground.date=new String(\"2016-09-12 11:30:00\")",
            "weatherunderground.dateFormat=new String(\"yyyy-MM-dd HH:mm:ss\")",
            "weatherunderground.result.fit=NEAREST"/*
                                                    * ,
                                                    * "logging.level.org.springframework.web: DEBUG"
                                                    */ })
    public static class TestWeatherUndergroundHistoryRequest
            extends WeatherUndergroundProcessorIntegrationTests {
        @Test
        public void request() throws JsonProcessingException {
            processor.input().send(new GenericMessage<String>(""));
            BlockingQueue<Message<?>> messages = messageCollector
                    .forChannel(processor.output());
            messages.stream().map(Message::getPayload)
                    .forEach(System.out::println);
        }
    }

    @WebIntegrationTest({ "weatherunderground.search.nearbypws=false",
            "weatherunderground.query=new String(\"de/Mainz\")",
            "weatherunderground.mode=FORECAST"
            /*
             * , "logging.level.org.springframework.web: DEBUG"
             */ })
    public static class TestWeatherUndergroundForecastRequest
            extends WeatherUndergroundProcessorIntegrationTests {
        @Test
        public void request() throws JsonProcessingException {
            processor.input().send(new GenericMessage<String>(""));
            BlockingQueue<Message<?>> messages = messageCollector
                    .forChannel(processor.output());
            messages.stream().map(Message::getPayload)
                    .forEach(System.out::println);
        }
    }

    @WebIntegrationTest({ "weatherunderground.search.nearbypws=false",
            "weatherunderground.query=new String(\"de/Mainz\")",
            "weatherunderground.mode=FORECAST"
            /*
             * , "logging.level.org.springframework.web: DEBUG"
             */ })
    public static class TestWeatherUndergroundHourlyRequest
            extends WeatherUndergroundProcessorIntegrationTests {
        @Test
        public void request() throws JsonProcessingException {
            processor.input().send(new GenericMessage<String>(""));
            BlockingQueue<Message<?>> messages = messageCollector
                    .forChannel(processor.output());
            messages.stream().map(Message::getPayload)
                    .forEach(System.out::println);
        }
    }

    @SpringBootApplication
    public static class WeatherUndergroundProcessorApplication {

    }
}
