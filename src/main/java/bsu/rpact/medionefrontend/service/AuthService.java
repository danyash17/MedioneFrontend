package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.JwtTokenizationAdapter;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.pojo.authentication.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Service
public class AuthService {

    @Value("${mappings.preambule}")
    private String webPreambule;
    @Value("${mappings.auth}")
    private String loginMapping;
    @Value("${mappings.register}")
    private String registerMapping;

    @Autowired
    private JwtTokenizationAdapter jwtTokenizationAdapter;

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.builder().baseUrl(webPreambule).build();
        jwtTokenizationAdapter.setWebClient(webClient);
    }


    public JwtResponce authenticate(String login, String password) {
        return jwtTokenizationAdapter.authenticate(loginMapping, login, password);
    }

    public MessageResponse register(RegisterRequest registerRequest){
        return jwtTokenizationAdapter.register(registerMapping, registerRequest);
    }

}
