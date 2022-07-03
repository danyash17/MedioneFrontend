package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.pojo.authentication.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Component
public class JwtTokenizationAdapter extends GeneralAdapter{

    @Value("${mappings.auth}")
    private String loginMapping;
    @Value("${mappings.twoFactorAuth.qr}")
    private String twoFactorQrMapping;
    @Value("${mappings.twoFactorAuth.sms}")
    private String twoFactorSmsMapping;
    @Value("${mappings.twoFactorAuth.sms.verify}")
    private String twoFactorSmsVerifyMapping;
    @Value("${mappings.register}")
    private String registerMapping;

    public TotpResponce totpAuthenticate(String login, String password){
        LoginRequest loginRequest = new LoginRequest(login,password);
        return webClient.post()
                .uri(loginMapping)
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

    public MessageResponse register(RegisterRequest registerRequest) {
        return webClient.post()
                .uri(registerMapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registerRequest), RegisterRequest.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }

    public JwtResponce twoFactorAuthenticate(TwoFactorAuthenticationRequest twoFactorAuthenticationRequest) {
        return webClient.post()
                .uri(twoFactorQrMapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(twoFactorAuthenticationRequest), TwoFactorAuthenticationRequest.class)
                .retrieve()
                .bodyToMono(JwtResponce.class)
                .block();
    }

    public void sendSms(LoginRequest loginRequest) {
        webClient.post()
                .uri(twoFactorSmsVerifyMapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
