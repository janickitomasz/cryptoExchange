# CryptoExchange

The application provides information about cryptocurrency rates and forecasts for exchange, taking into account the provided margin.
The application uses data from third-party providers to calculate cryptocurrency rates in a base currency or to make forecasts for exchanging selected currencies.

#  3rd party providers
Currently application uses data provided by diadata:

## Site
https://www.diadata.org/
## RestAPI endpoints
https://docs.diadata.org/use-nexus-product/readme/token-price-feeds/access-api-endpoints/api-endpoints

# Endpoints

## /currencies/{currency}
Delivers information on exchange rates, represented in the 'currency' unit.

example:

curl -X GET "http://localhost:8080/currencies/BTC" -H "Accept: application/json"

## /currencies/exchange
Delivers forecasts for currency exchange. 

example:

curl -X POST "http://localhost:8080/currencies/exchange" -H "Content-Type: application/json" -d "{\"from\":\"BTC\",\"to\":[\"ETH\",\"SOL\"],\"amount\":100}"


# Properties
Properties are described i .properties file. 
 BE AWARE, THAT REMOVING configuration.allowedCurrencies MAKES APPLICATIONS DO CALCULATIONS ON ALL CUURRENCIES PROVIDED BY 3rd PARTY API WHICH PROBABLY WILL FAIL. APPLICATION IS NOT OPTIMIZED TO THIS CONFIGURATION