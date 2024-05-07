package ru.gb.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic(value = "Тестирование API https://mock.server//locations/v1/{locationkey}")
@Feature(value = "Домашнее задание")
public class GetLocationTest extends AbstractTest{

    private static final Logger logger = LoggerFactory.getLogger(GetLocationTest.class);

    @Test
    @DisplayName("GetLocationTest.StatusOK")
    @Description("GET Search by locationKey. Status OK")
    @Link("")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Тестирование метода Locations")
    void get_shloudReturn200() throws IOException, URISyntaxException {
        logger.info("Тест код 200 запущен.");
        //given
        ObjectMapper mapper = new ObjectMapper();
        Location bodyOk = new Location();
        bodyOk.setKey("OK");

        Location bodyError = new Location();
        bodyError.setKey("Error");

        logger.debug("Формирование мока для GET /locations/v1/293142");
        stubFor(get(urlPathEqualTo("/locations/v1/293142"))
                .willReturn(aResponse().withStatus(200).withBody(mapper.writeValueAsString(bodyOk))));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http client Создан");
        //when

        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/293142");
        URI uriOk = new URIBuilder(request.getURI()).build();
        request.setURI(uriOk);
        HttpResponse responseOk = httpClient.execute(request);
        //then

        verify(1, getRequestedFor(urlPathEqualTo("/locations/v1/293142")));
        assertEquals(200, responseOk.getStatusLine().getStatusCode());
        assertEquals("OK", mapper.readValue(responseOk.getEntity().getContent(), Location.class).getKey());
    }

    @Test
    @DisplayName("GetLocationTest.StatusUnauthorized")
    @Description("GET Search by locationKey. Status Unauthorized ")
    @Link("")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Тестирование метода Locations")
    void get_shouldReturn403() throws IOException, URISyntaxException {
        logger.info("Тест код ответа 403 запущен");
        //given
        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apiKey", notMatching("fffffvdfvdvdvdvffvdfvdfvdfv"))
                .willReturn(aResponse()
                        .withStatus(403).withBody("forbidden")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/cities/autocomplete");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "A_fffffvdfvdvdvdvffvdfvdfvdfv")
                .build();
        request.setURI(uri);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apiKey", equalTo("A_fffffvdfvdvdvdvffvdfvdfvdfv")));
        assertEquals(403, response.getStatusLine().getStatusCode());
        assertEquals("forbidden", convertResponseToString(response));

    }
}
