package tja.software.crypto.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tja.software.crypto.controller.model.ForecastResponse;
import tja.software.crypto.controller.model.RatesResponse;
import tja.software.crypto.data.live.DataService;
import tja.software.crypto.model.CalculatedRate;
import tja.software.crypto.model.Forecast;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@TestPropertySource(properties = "configuration.allowedCurrencies=BTC,ETH,SOL,XRP")
@SpringBootTest
class CryptoServiceTest {

    @MockitoBean
    CurrencyService currencyService;

    @MockitoBean
    RateService rateService;

    @MockitoBean
    DataService dataService;

    @MockitoBean
    ExchangeService exchangeService;

    @Autowired
    CryptoService cryptoService;

    @Test
    void getExchanges() {
        CalculatedRate calculatedRateBTC = new CalculatedRate("BTC", "XRP", new BigDecimal(2));
        CalculatedRate calculatedRateETH = new CalculatedRate("ETH", "XRP", new BigDecimal(4));

        Mockito.when(rateService.getRate(eq("XRP"), eq("BTC"), any())).thenReturn(CompletableFuture.completedFuture(calculatedRateBTC));
        Mockito.when(rateService.getRate(eq("XRP"), eq("ETH"), any())).thenReturn(CompletableFuture.completedFuture(calculatedRateETH));

        Forecast forecastBtc = new Forecast( new BigDecimal(2), new BigDecimal(100), new BigDecimal(198), new BigDecimal(1) , "BTC");
        Forecast forecastEth = new Forecast(new BigDecimal(4), new BigDecimal(100), new BigDecimal(396), new BigDecimal(1) , "ETH");

        Mockito.when(exchangeService.exchange(new BigDecimal(100), calculatedRateBTC)).thenReturn(CompletableFuture.completedFuture(forecastBtc));
        Mockito.when(exchangeService.exchange(new BigDecimal(100), calculatedRateETH)).thenReturn(CompletableFuture.completedFuture(forecastEth));

        Mockito.when(currencyService.getCurrencies(any(), any(), any())).thenReturn(Set.of("BTC","ETH"));

        Map<String, Forecast> forecasts = new HashMap<>();
        forecasts.put("BTC", forecastBtc);
        forecasts.put("ETH", forecastEth);

        ForecastResponse expectedResponse = new ForecastResponse("XRP", forecasts);

        ForecastResponse actualResponse = cryptoService.getExchanges("XRP", new BigDecimal(100), new ArrayList(List.of("BTC", "ETH")), UUID.randomUUID());

        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    void getRates_happyPath() {
        CalculatedRate calculatedRateBTC = new CalculatedRate("BTC", "XRP", new BigDecimal(2));
        CalculatedRate calculatedRateETH = new CalculatedRate("ETH", "XRP", new BigDecimal(4));

        Mockito.when(currencyService.getCurrencies(any(), any(), any() )).thenReturn(Set.of("BTC", "ETH"));
        Mockito.when(rateService.getRate(eq("XRP"), eq("BTC"), any())).thenReturn(CompletableFuture.completedFuture(calculatedRateBTC));
        Mockito.when(rateService.getRate(eq("XRP"), eq("ETH"), any())).thenReturn(CompletableFuture.completedFuture(calculatedRateETH));


        HashMap<String, BigDecimal> rates = new HashMap<>();
        rates.put("BTC", new BigDecimal(2));
        rates.put("ETH", new BigDecimal(4));

        RatesResponse expectedResponse = new RatesResponse("XRP", rates);

        RatesResponse actualResponse = cryptoService.getRates("XRP", List.of("BTC", "ETH"), UUID.randomUUID() );

        assertEquals(expectedResponse, actualResponse);
    }
}