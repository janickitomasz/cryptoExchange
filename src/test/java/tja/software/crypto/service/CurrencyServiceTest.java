package tja.software.crypto.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tja.software.crypto.data.live.DataService;
import tja.software.crypto.model.Currency;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@TestPropertySource(properties = "configuration.allowedCurrencies=BTC,ETH,SOL,XRP")
@SpringBootTest
class CurrencyServiceTest {

    @MockitoBean
    DataService dataService;

    @Autowired
    CurrencyService currencyService;

    @Test
    void getCurrencies(){
        Currency currencyBTC = new Currency("BitCoin", "BTC");
        Currency currencyETH = new Currency("Ethereum", "ETH");

        Mockito.when(dataService.getCurrencies(any())).thenReturn(new ArrayList<>(List.of(currencyBTC, currencyETH)));

        Set<String> expectedResponse = new HashSet<>(List.of("BTC", "ETH"));

        Set<String> actualResponse = currencyService.getCurrencies(null,null, UUID.randomUUID());

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getCurrencies_filter(){
        Currency currencyBTC = new Currency("BitCoin", "BTC");
        Currency currencyETH = new Currency("Ethereum", "ETH");

        Mockito.when(dataService.getCurrencies(any())).thenReturn(new ArrayList<>(List.of(currencyBTC, currencyETH)));

        Set<String> expectedResponse = new HashSet<>(List.of("BTC", "ETH"));

        Set<String> actualResponse = currencyService.getCurrencies(new ArrayList<>(List.of("BTC", "ETH", "SOL")), null, UUID.randomUUID());

        assertEquals(expectedResponse, actualResponse);
    }
}