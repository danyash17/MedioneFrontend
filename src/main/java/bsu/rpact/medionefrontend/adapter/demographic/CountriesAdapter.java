package bsu.rpact.medionefrontend.adapter.demographic;

import bsu.rpact.medionefrontend.pojo.other.Country;
import bsu.rpact.medionefrontend.pojo.other.RestCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class CountriesAdapter{

    @Value("${api.countries}")
    private String mapping;
    private WebClient webClient;

    @PostConstruct
    private void initWebClient(){
        final int size = 32 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        webClient = WebClient.builder()
                .baseUrl(mapping)
                .exchangeStrategies(strategies)
                .build();
    }

    public List<RestCountry> getCountries(){
        return webClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RestCountry>>() {})
                .block();
    }

}
