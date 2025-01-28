package tja.software.crypto.data.live.dia.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Quotation (
    @JsonProperty("Asset")
    Asset asset){}
