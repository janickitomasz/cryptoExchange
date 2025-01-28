package tja.software.crypto.data.live.dia.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record DiaResponse(
    @JsonProperty("Symbol")
    String symbol,

    @JsonProperty("Name")
    String name,

    @JsonProperty("Price")
    BigDecimal price
){}