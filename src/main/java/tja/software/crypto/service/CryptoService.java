package tja.software.crypto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tja.software.crypto.controller.model.ForecastResponse;
import tja.software.crypto.controller.model.RatesResponse;
import tja.software.crypto.log.LogForMethod;
import tja.software.crypto.model.CalculatedRate;
import tja.software.crypto.model.Forecast;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CryptoService {
    ExchangeService exchangeService;
    RateService rateService;
    CurrencyService currencyService;

    @Autowired
    CryptoService(ExchangeService exchangeService, RateService rateService, CurrencyService currencyService) {
        this.exchangeService = exchangeService;
        this.rateService = rateService;
        this.currencyService = currencyService;
    }

    @LogForMethod
    public ForecastResponse getExchanges(String baseCurrency, BigDecimal amount, List<String> currencies, UUID uuid){
        Set<String> filtered = currencyService.getCurrencies(currencies, baseCurrency, uuid);
        List<CompletableFuture<Forecast>> forecasts = getForecastFutures(baseCurrency, amount, filtered, uuid);
        Map<String, Forecast> forecastMap = putFuturesToForecastMap(forecasts);
        return new ForecastResponse(baseCurrency, forecastMap);
    }

    @LogForMethod
    public RatesResponse getRates(String baseCurrency, List<String> filter, UUID uuid){
        Set<String> currencies = currencyService.getCurrencies(filter, baseCurrency, uuid);
        List<CalculatedRate> calculatedRates = getCalculatedRates(baseCurrency, currencies, uuid);
        HashMap<String, BigDecimal> rates = getRatesMap(calculatedRates, uuid);

        return new RatesResponse(baseCurrency, rates);
    }

    @LogForMethod
    private static Map<String, Forecast> putFuturesToForecastMap(List<CompletableFuture<Forecast>> forecasts) {
        CompletableFuture.allOf(forecasts.toArray(new CompletableFuture[forecasts.size()])).join();

        Map<String, Forecast> forecastMap = forecasts.stream()
                .map(forecastCompletableFuture -> forecastCompletableFuture.getNow(null))
                .collect(Collectors.toMap(
                        Forecast::symbol,
                        forecast -> forecast));
        return forecastMap;
    }

    @LogForMethod
    private List<CompletableFuture<Forecast>>  getForecastFutures(String baseCurrency, BigDecimal amount, Set<String> currencies, UUID uuid) {
        List<CompletableFuture<Forecast>> forecasts = new ArrayList<>();
        for(String currency : currencies){
            forecasts.add(rateService.getRate(baseCurrency, currency, uuid)
                    .thenCompose(customRate -> exchangeService.exchange(amount, customRate)));
        }
        return forecasts;
    }

    @LogForMethod
    private static HashMap<String, BigDecimal> getRatesMap(List<CalculatedRate> calculatedRates, UUID uuid) {
        HashMap<String, BigDecimal> rates = new HashMap<>();
        for(CalculatedRate calculatedRate : calculatedRates){
            if(calculatedRate!=null){
                rates.put(calculatedRate.symbol(), calculatedRate.ratio());
            }
        }
        return rates;
    }

    @LogForMethod
    private List<CalculatedRate> getCalculatedRates(String baseCurrency, Set<String> currencies, UUID uuid){
        List<CompletableFuture<CalculatedRate>> futures = getCalculatedRatesFutures(baseCurrency, currencies, uuid);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        return futures.stream().map(cf -> cf.getNow(null)).toList();
    }

    @LogForMethod
    private List<CompletableFuture<CalculatedRate>> getCalculatedRatesFutures(String baseCurrency, Set<String> currencies, UUID uuid) {
        List<CompletableFuture<CalculatedRate>> futures = new ArrayList<>();
        for(String currency : currencies){
            CompletableFuture<CalculatedRate> rate = rateService.getRate(baseCurrency, currency, uuid);
            futures.add(rate);
        }
        return futures;
    }
}
