package tja.software.crypto.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tja.software.crypto.data.live.DataService;
import tja.software.crypto.model.CalculatedRate;
import tja.software.crypto.model.Rate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class RateServiceTest {

    @MockitoBean
    DataService dataService;

    @Autowired
    RateService rateService;

    @Test
    void getRate() {
        Rate rateBtc = new Rate("BTC", new BigDecimal(20));
        Rate rateEth = new Rate("ETH", new BigDecimal(40));

        Mockito.when(dataService.getRate(eq("BTC"), any())).thenReturn(rateBtc);
        Mockito.when(dataService.getRate(eq("ETH"), any())).thenReturn(rateEth);



        CalculatedRate expectedRate = new CalculatedRate("BTC", "ETH", new BigDecimal(2));
        CalculatedRate actualRate = rateService.getRate("ETH", "BTC", UUID.randomUUID()).join();

        assertEquals(expectedRate.symbol(), actualRate.symbol());
        assertEquals(expectedRate.baseCurrencySymbol(), actualRate.baseCurrencySymbol());
        assert(expectedRate.ratio().compareTo(actualRate.ratio())==0);
    }
}