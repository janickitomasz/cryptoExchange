package tja.software.crypto.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import tja.software.crypto.controller.model.ExchangeRequest;
import tja.software.crypto.controller.model.ForecastResponse;
import tja.software.crypto.controller.model.RatesResponse;
import tja.software.crypto.service.CryptoService;

import java.util.List;
import java.util.UUID;

@RestController("/")
public class Controller {
    CryptoService cryptoService;

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    Controller(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Cacheable(value = "controllerCache")
    @GetMapping("/currencies/{currency}")
    public RatesResponse getRates(@PathVariable String currency,
                                  @RequestParam(value = "filter", required = false) List<String> filter){
        UUID uuid = UUID.randomUUID();
        logger.debug("Request to getRates for currency " + currency + ". UUID: " + uuid );
        return cryptoService.getRates(currency, filter, uuid);
    }

    @Cacheable(value = "controllerCache")
    @PostMapping("/currencies/exchange")
    public ForecastResponse getForecasts(@RequestBody ExchangeRequest exchangeRequest){
        UUID uuid = UUID.randomUUID();
        logger.debug("Request to getForecasts with request body: " + exchangeRequest.toString() + ". UUID: " + uuid );
        return cryptoService.getExchanges(exchangeRequest.from(), exchangeRequest.amount(), exchangeRequest.to(), uuid);
    }
}
