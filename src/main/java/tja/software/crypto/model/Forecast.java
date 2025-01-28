package tja.software.crypto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public record Forecast(
    BigDecimal rate,
    BigDecimal amount,
    BigDecimal result,
    BigDecimal fee,

    @JsonIgnore
    String symbol
){}