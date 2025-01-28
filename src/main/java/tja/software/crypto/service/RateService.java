package tja.software.crypto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tja.software.crypto.data.live.DataService;
import tja.software.crypto.log.LogForMethod;
import tja.software.crypto.model.CalculatedRate;
import tja.software.crypto.model.Rate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class RateService {
    DataService dataService;

    @Autowired
    RateService(DataService dataService) {
        this.dataService = dataService;
    }

    @Async
    @LogForMethod
    public CompletableFuture<CalculatedRate> getRate(String currency1, String currency2, UUID uuid) {
        Rate rate1 = dataService.getRate(currency1, uuid);
        Rate rate2 = dataService.getRate(currency2, uuid);

        if(rate1==null || rate2==null) {
            return null;
        }

        BigDecimal ratio = getRatio(rate1, rate2, uuid);

        return CompletableFuture.completedFuture(new CalculatedRate(currency2, currency1, ratio));
    }

    @LogForMethod
    private static BigDecimal getRatio(Rate rate1, Rate rate2, UUID uuid) {
        BigDecimal price1 = rate1.price_usd();
        BigDecimal price2 = rate2.price_usd();

        BigDecimal ratio = price1.divide(price2, 16, RoundingMode.HALF_UP);
        return ratio;
    }
}
