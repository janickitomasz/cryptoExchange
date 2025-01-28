package tja.software.crypto.data.live;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tja.software.crypto.data.live.dia.model.Asset;
import tja.software.crypto.data.live.dia.model.DiaResponse;
import tja.software.crypto.data.live.dia.model.Quotation;
import tja.software.crypto.data.live.dia.remote.RemoteDiaService;
import tja.software.crypto.model.Currency;
import tja.software.crypto.model.Rate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@TestPropertySource(properties = "configuration.allowedCurrencies=BTC,ETH,SOL,XRP")
class DataServiceTest {

    @MockitoBean
    RemoteDiaService remoteDiaService;

    @Autowired
    DataService dataService;

    @Test
    void getRate() {
        DiaResponse diaResponse = new DiaResponse("BTC", "BitCoin", new BigDecimal(20));
        Mockito.when(remoteDiaService.quotation(eq("BTC"), any())).thenReturn(diaResponse);

        Rate expectedResponse = new Rate("BTC",  new BigDecimal(20));
        Rate actualResponse = dataService.getRate("BTC", UUID.randomUUID());

        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    void getCurrencies() {
        Asset assetBtc = new Asset("BTC", "BitCoin");
        Asset assetEth = new Asset("ETH", "Ethereum");

        Quotation quotationBtc = new Quotation(assetBtc);
        Quotation quotationEth = new Quotation(assetEth);

        Mockito.when(remoteDiaService.quotedAssets(any())).thenReturn(List.of(quotationBtc, quotationEth));


        Currency currencyBtc = new Currency("BitCoin", "BTC");
        Currency currencyEth = new Currency("Ethereum", "ETH");
        List<Currency> expectedResponse = List.of(currencyBtc, currencyEth);

        List<Currency> actualResponse = dataService.getCurrencies(UUID.randomUUID());

        assertEquals(expectedResponse, actualResponse);
    }
}