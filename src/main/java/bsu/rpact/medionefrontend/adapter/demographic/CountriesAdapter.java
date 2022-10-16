package bsu.rpact.medionefrontend.adapter.demographic;

import bsu.rpact.medionefrontend.pojo.other.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
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
        webClient = WebClient.builder()
                .baseUrl(mapping)
                .build();
    }

    public List<Country> getCountries(){
        return webClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Country>>() {})
                .block();
    }

}
