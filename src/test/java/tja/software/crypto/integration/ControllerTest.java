package tja.software.crypto.integration;

import com.github.tomakehurst.wiremock.client.WireMock;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureWireMock(port=8081)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"api.dia.url=http://localhost:8081","configuration.allowedCurrencies=BTC,ETH,SOL,XRP" })
@AutoConfigureMockMvc
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DirtiesContext
    @Test
    void getRates() throws Exception {
        String body = """
  [
  {
    "Asset": {
      "Symbol": "BTC",
      "Name": "Bitcoin",
      "Address": "0x0000000000000000000000000000000000000000",
      "Decimals": 8,
      "Blockchain": "Bitcoin"
    },
    "Volume": 5162522693.891084,
    "VolumeUSD": 0,
    "Index": 0
  },
  {
    "Asset": {
      "Symbol": "ETH",
      "Name": "Ether",
      "Address": "0x0000000000000000000000000000000000000000",
      "Decimals": 18,
      "Blockchain": "Ethereum"
    },
    "Volume": 4092927158.399444,
    "VolumeUSD": 0,
    "Index": 0
  }
  ]
  """;

String bodyBtc = """
        {
          "Symbol": "BTC",
          "Name": "Bitcoin",
          "Address": "0x0000000000000000000000000000000000000000",
          "Blockchain": "Bitcoin",
          "Price": 40,
          "PriceYesterday": 40,
          "VolumeYesterdayUSD": 4585562989.646538,
          "Time": "2025-01-26T18:07:59Z",
          "Source": "diadata.org"
        }
        """;

String bodyEth = """
        {
          "Symbol": "ETH",
          "Name": "Ether",
          "Address": "0x0000000000000000000000000000000000000000",
          "Blockchain": "Ethereum",
          "Price": 20,
          "PriceYesterday": 20,
          "VolumeYesterdayUSD": 3740721794.9799957,
          "Time": "2025-01-26T18:07:58Z",
          "Source": "diadata.org"
        }
        """;

    WireMock.stubFor(WireMock.get("/quotation/BTC")
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(bodyBtc)));
    WireMock.stubFor(WireMock.get("/quotation/ETH")
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(bodyEth)));

    WireMock.stubFor(WireMock.get("/quotedAssets/")
            .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body)));

    mockMvc.perform(get("/currencies/BTC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rates.ETH").value(2));

    }

    @DirtiesContext
    @Test
    void getRates_retry() throws Exception {
        String body = """
  [
  {
    "Asset": {
      "Symbol": "BTC",
      "Name": "Bitcoin",
      "Address": "0x0000000000000000000000000000000000000000",
      "Decimals": 8,
      "Blockchain": "Bitcoin"
    },
    "Volume": 5162522693.891084,
    "VolumeUSD": 0,
    "Index": 0
  },
  {
    "Asset": {
      "Symbol": "ETH",
      "Name": "Ether",
      "Address": "0x0000000000000000000000000000000000000000",
      "Decimals": 18,
      "Blockchain": "Ethereum"
    },
    "Volume": 4092927158.399444,
    "VolumeUSD": 0,
    "Index": 0
  }
  ]
  """;

    String bodyBtc = """
    {
      "Symbol": "BTC",
      "Name": "Bitcoin",
      "Address": "0x0000000000000000000000000000000000000000",
      "Blockchain": "Bitcoin",
      "Price": 40,
      "PriceYesterday": 40,
      "VolumeYesterdayUSD": 4585562989.646538,
      "Time": "2025-01-26T18:07:59Z",
      "Source": "diadata.org"
    }
    """;

    String bodyEth = """
    {
      "Symbol": "ETH",
      "Name": "Ether",
      "Address": "0x0000000000000000000000000000000000000000",
      "Blockchain": "Ethereum",
      "Price": 20,
      "PriceYesterday": 20,
      "VolumeYesterdayUSD": 3740721794.9799957,
      "Time": "2025-01-26T18:07:58Z",
      "Source": "diadata.org"
    }
    """;

        WireMock.stubFor(WireMock.get("/quotation/BTC")
                .inScenario("quotationBTC")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(WireMock.aResponse()
                        .withStatus(500))
                        .willSetStateTo("second try"));

        WireMock.stubFor(WireMock.get("/quotation/BTC")
                .inScenario("quotationBTC")
                .whenScenarioStateIs("second try")
                .willReturn(WireMock.aResponse()
                        .withStatus(500))
                .willSetStateTo("third try"));

        WireMock.stubFor(WireMock.get("/quotation/BTC")
                .inScenario("quotationBTC")
                .whenScenarioStateIs("third try")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                                .withBody(bodyBtc)));


        WireMock.stubFor(WireMock.get("/quotation/ETH")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyEth)));

        WireMock.stubFor(WireMock.get("/quotedAssets/")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));


        mockMvc.perform(get("/currencies/BTC"));

        WireMock.verify(3, WireMock.getRequestedFor(WireMock.urlEqualTo("/quotation/BTC")));
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/quotation/ETH")));
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/quotedAssets/")));
    }


    @DirtiesContext
    @Test
    public void getForecasts() throws Exception {

        String body = """
  [
  {
    "Asset": {
      "Symbol": "BTC",
      "Name": "Bitcoin",
      "Address": "0x0000000000000000000000000000000000000000",
      "Decimals": 8,
      "Blockchain": "Bitcoin"
    },
    "Volume": 5162522693.891084,
    "VolumeUSD": 0,
    "Index": 0
  },
  {
    "Asset": {
      "Symbol": "ETH",
      "Name": "Ether",
      "Address": "0x0000000000000000000000000000000000000000",
      "Decimals": 18,
      "Blockchain": "Ethereum"
    },
    "Volume": 4092927158.399444,
    "VolumeUSD": 0,
    "Index": 0
  }
  ]
  """;

        String bodyBtc = """
        {
          "Symbol": "BTC",
          "Name": "Bitcoin",
          "Address": "0x0000000000000000000000000000000000000000",
          "Blockchain": "Bitcoin",
          "Price": 40,
          "PriceYesterday": 40,
          "VolumeYesterdayUSD": 4585562989.646538,
          "Time": "2025-01-26T18:07:59Z",
          "Source": "diadata.org"
        }
        """;

        String bodyEth = """
        {
          "Symbol": "ETH",
          "Name": "Ether",
          "Address": "0x0000000000000000000000000000000000000000",
          "Blockchain": "Ethereum",
          "Price": 20,
          "PriceYesterday": 20,
          "VolumeYesterdayUSD": 3740721794.9799957,
          "Time": "2025-01-26T18:07:58Z",
          "Source": "diadata.org"
        }
        """;

        String requestJSON = "{\"from\":\"BTC\",\"to\":[\"ETH\"],\"amount\":100}";

        WireMock.stubFor(WireMock.get("/quotation/BTC")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyBtc)));
        WireMock.stubFor(WireMock.get("/quotation/ETH")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyEth)));

        WireMock.stubFor(WireMock.get("/quotedAssets/")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));



        mockMvc.perform(post("/currencies/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(jsonPath("$.forecasts.ETH.fee").value(1));

    }


}
