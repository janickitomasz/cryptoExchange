package tja.software.crypto.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tja.software.crypto.model.CalculatedRate;
import tja.software.crypto.model.Forecast;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = "exchange.margin=0.01")
class ExchangeServiceTest {

    @Autowired
    ExchangeService exchangeService = new ExchangeService();

    @Test
    void exchange() {
        CalculatedRate calculatedRate = new CalculatedRate("BTC", "XRP", new BigDecimal(2) );
        Forecast expectedResponse = new Forecast(new BigDecimal(2), new BigDecimal(100), new BigDecimal(198), new BigDecimal(1), "BTC");

        Forecast actualResponse = exchangeService.exchange(new BigDecimal(100), calculatedRate).join();

        assertEquals(expectedResponse.symbol(), actualResponse.symbol());
        assert(expectedResponse.amount().compareTo(actualResponse.amount()) == 0);
    }
}