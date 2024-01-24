package bsu.rpact.medionefrontend.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

public class GeneralAdapter {
    @Value("${mappings.preambule}")
    protected String restPreambule;
    @Value(("${frontend.mappings.preambule}"))
    protected String frontendPreambule;
    protected WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        webClient = WebClient.builder()
                .baseUrl(restPreambule)
                .exchangeStrategies(strategies)
                .build();
    }


    public void authenticateWebClient(String token){
        webClient = webClient.mutate().defaultHeader("Authorization","Bearer " + token).build();
    }

}
