package tja.software.crypto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tja.software.crypto.data.live.DataService;
import tja.software.crypto.log.LogForMethod;
import tja.software.crypto.model.Currency;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class CurrencyService {

    DataService dataService;

    @Value("#{'${configuration.allowedCurrencies:}'.isEmpty() ? null : '${configuration.allowedCurrencies:}'.split(',')}")
    private List<String> allowedCurrencies;

    @Autowired
    public CurrencyService(DataService dataService){
        this.dataService = dataService;
    }

    @LogForMethod
    private Set<String> getCurrencies(UUID uuid){
        Set<String> currencies = new HashSet<>(dataService.getCurrencies(uuid).stream().map(Currency::symbol).toList());
        return currencies;
    }

    @LogForMethod
    public Set<String> getCurrencies(List<String> filter, String baseCurrency, UUID uuid){
        Set<String> currencies = getCurrencies(uuid);
        if(allowedCurrencies!=null && !allowedCurrencies.isEmpty()){
            currencies.retainAll(allowedCurrencies);
        }
        if(filter != null && !filter.isEmpty()){
           currencies.retainAll(filter);
        }
        currencies.remove(baseCurrency);
        return currencies;
    }
}
