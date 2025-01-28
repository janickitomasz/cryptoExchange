package tja.software.crypto.controller.model;

import java.math.BigDecimal;
import java.util.Map;

public record RatesResponse(
    String source,
    Map<String, BigDecimal> rates) {}
