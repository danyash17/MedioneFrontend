package bsu.rpact.medionefrontend.adapter;

import org.springframework.beans.factory.annotation.Value;
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
        webClient = WebClient.builder()
                .baseUrl(restPreambule)
                .build();
    }


    public void authenticateWebClient(String token){
        webClient = webClient.mutate().defaultHeader("Authorization","Bearer " + token).build();
    }

}
