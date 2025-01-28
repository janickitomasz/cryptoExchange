package tja.software.crypto.data.live.dia.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Asset(
    @JsonProperty("Symbol")
    String symbol,

    @JsonProperty("Name")
    String name
){}

