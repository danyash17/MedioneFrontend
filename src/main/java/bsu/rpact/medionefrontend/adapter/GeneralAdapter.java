package bsu.rpact.medionefrontend.adapter;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.net.http.HttpRequest;

public class GeneralAdapter {
    @Value("${mappings.preambule}")
    protected String restPreambule;
    @Value(("${frontend.mappings.preambule}"))
    protected String frontendPreambule;
    protected WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        VaadinRequest request = VaadinService.getCurrentRequest();
        webClient = WebClient.builder()
                .baseUrl(restPreambule)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("Authorization", request==null ? "" : request.getHeader("Authorization"));
                })
                .build();

    }
}
