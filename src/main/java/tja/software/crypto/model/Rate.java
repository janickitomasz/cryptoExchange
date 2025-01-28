package tja.software.crypto.model;

import java.math.BigDecimal;

public record Rate(
    String symbol,
    BigDecimal price_usd){}
