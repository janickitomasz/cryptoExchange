package tja.software.crypto.controller.model;

import java.math.BigDecimal;
import java.util.List;

public record ExchangeRequest (
    String from,
    List<String> to,
    BigDecimal amount
){}

