package ru.gb.accu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Epic(value = "Тестирование API https://mock.server/forecast/v1/daily/10day/{locationkey}")
@Feature(value = "Домашнее задание")
public class GetWeatherTenDaysTest extends AbstractTest{

    private static final Logger logger = LoggerFactory.getLogger(GetWeatherTenDaysTest.class);

    @Test
    @DisplayName("GetLocationTest.StatusOK")
    @Description("GET 10 Days of Daily Forecasts")
    @Link("")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Тестирование метода Forecast")
    void get_shouldReturn200() throws IOException {
        logger.info("Тест код ответ 200 запущен");
        //given
        ObjectMapper mapper = new ObjectMapper();
        Weather weather = new Weather();
        Headline headline = new Headline();
        headline.setCategory("mild");
        weather.setHeadline(headline);
        DailyForecast dailyForecast = new DailyForecast();
        List<DailyForecast> dailyForecasts = new ArrayList<>();
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        dailyForecasts.add(dailyForecast);
        weather.setDailyForecasts(dailyForecasts);

        logger.debug("Формирование мока для GET /forecast/v1/daily/10day/293142");
        stubFor(get(urlPathEqualTo("/forecast/v1/daily/10day/293142")).willReturn(aResponse()
                .withStatus(200).withBody(mapper.writeValueAsString(weather))));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + "/forecast/v1/daily/10day/293142");
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo("/forecast/v1/daily/10day/293142")));
        assertEquals(200, response.getStatusLine().getStatusCode());

        Weather responseBody = mapper.readValue(response.getEntity().getContent(), Weather.class);
        assertEquals("mild", responseBody.getHeadline().getCategory());
        assertEquals(10, responseBody.getDailyForecasts().size());
    }
}
