package tja.software.crypto.model;

import java.math.BigDecimal;

public record CalculatedRate(
    String symbol,
    String baseCurrencySymbol,
    BigDecimal ratio){}
