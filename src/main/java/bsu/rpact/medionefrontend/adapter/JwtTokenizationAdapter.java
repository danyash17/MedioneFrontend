package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.pojo.authentication.LoginRequest;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.pojo.authentication.RegisterRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtTokenizationAdapter {

    private WebClient webClient;

    public JwtResponce authenticate(String mapping,String login, String password){
        LoginRequest loginRequest = new LoginRequest(login,password);
        return webClient.post()
                .uri(mapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .retrieve()
                .bodyToMono(JwtResponce.class)
                .block();
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public MessageResponse register(String mapping, RegisterRequest registerRequest) {
        return webClient.post()
                .uri(mapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registerRequest), RegisterRequest.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
