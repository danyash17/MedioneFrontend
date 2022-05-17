package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.pojo.authentication.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtTokenizationAdapter {

    private WebClient webClient;

    public TotpResponce totpAuthenticate(String mapping, String login, String password){
        LoginRequest loginRequest = new LoginRequest(login,password);
        return webClient.post()
                .uri(mapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .retrieve()
                .bodyToMono(TotpResponce.class)
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

    public JwtResponce twoFactorAuthenticate(String mapping, TwoFactorAuthenticationRequest twoFactorAuthenticationRequest) {
        return webClient.post()
                .uri(mapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(twoFactorAuthenticationRequest), TwoFactorAuthenticationRequest.class)
                .retrieve()
                .bodyToMono(JwtResponce.class)
                .block();
    }

    public void sendSms(String mapping, LoginRequest loginRequest) {
        webClient.post()
                .uri(mapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
