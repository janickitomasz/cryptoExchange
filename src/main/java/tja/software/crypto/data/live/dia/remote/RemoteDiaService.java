package tja.software.crypto.data.live.dia.remote;

import feign.FeignException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tja.software.crypto.data.live.dia.model.DiaResponse;
import tja.software.crypto.data.live.dia.model.Quotation;
import tja.software.crypto.log.LogForMethod;

import java.util.List;
import java.util.UUID;

@FeignClient(name= "RemoteDiaService", url = "${api.dia.url}")
public interface RemoteDiaService {

    @Cacheable(value = "ratesCache")
    @RequestMapping(method = RequestMethod.GET, value="/quotation/{currency}")
    DiaResponse quotation(@PathVariable String currency);

    @Cacheable(value = "currencyCache", key = "'quotedAssets'")
    @RequestMapping(method = RequestMethod.GET, value="/quotedAssets/")
    List<Quotation> quotedAssets();


    @LogForMethod
    @Retryable(
            value = { FeignException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2500)
    )
    default DiaResponse quotation(@PathVariable String currency, UUID uuid){
        return quotation(currency);
    }

    @LogForMethod
    @Retryable(
            value = { FeignException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2500)
    )
    default List<Quotation> quotedAssets(UUID uuid){
        return quotedAssets();
    }
}
