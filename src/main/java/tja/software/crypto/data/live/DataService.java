package tja.software.crypto.data.live;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import tja.software.crypto.data.live.dia.model.DiaResponse;
import tja.software.crypto.data.live.dia.model.Quotation;
import tja.software.crypto.data.live.dia.remote.RemoteDiaService;
import tja.software.crypto.log.LogForMethod;
import tja.software.crypto.model.Currency;
import tja.software.crypto.model.Rate;

import java.util.List;
import java.util.UUID;

@Service
public class DataService {
    RemoteDiaService remoteDiaService;

    @Autowired
    public DataService(RemoteDiaService remoteDiaService) {
        this.remoteDiaService = remoteDiaService;
    }

    @LogForMethod
    public Rate getRate(String symbol, UUID uuid){
        DiaResponse diaResponse = remoteDiaService.quotation(symbol, uuid);
        if(diaResponse==null){
            return null;
        }
        Rate rate = new Rate(diaResponse.symbol(), diaResponse.price());
        return rate;
    }

    @LogForMethod
    public List<Currency> getCurrencies(UUID uuid){
        List<Quotation> quotations = remoteDiaService.quotedAssets(uuid);
        return quotations.stream().map(q->new Currency(q.asset().name(), q.asset().symbol())).toList();
    }
}
