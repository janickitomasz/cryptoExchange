package tja.software.crypto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tja.software.crypto.log.LogForMethod;
import tja.software.crypto.model.CalculatedRate;
import tja.software.crypto.model.Forecast;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class ExchangeService {

    @Value("${exchange.margin}")
    private BigDecimal margin;

    @LogForMethod
    @Async
    public CompletableFuture<Forecast> exchange(BigDecimal amount, CalculatedRate calculatedRate){
        BigDecimal marginValue =  amount.multiply(margin);
        BigDecimal exchangeValue = amount.subtract(marginValue);
        if(exchangeValue.compareTo(BigDecimal.ZERO) ==-1){
            return null;
        }

        BigDecimal rate = calculatedRate.ratio();
        BigDecimal secondCurrencyAmount = exchangeValue.multiply(rate);

        Forecast forecast = new Forecast(rate, amount, secondCurrencyAmount, marginValue, calculatedRate.symbol());

        return CompletableFuture.completedFuture(forecast);
    }
}
