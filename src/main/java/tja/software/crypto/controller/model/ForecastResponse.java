package tja.software.crypto.controller.model;

import tja.software.crypto.model.Forecast;

import java.util.Map;

public record ForecastResponse (
    String from,
    Map<String, Forecast> forecasts
){}
