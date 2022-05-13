package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.JwtTokenizationAdapter;
import bsu.rpact.medionefrontend.entity.User;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Service
public class LoginService {

    @Value("${mappings.preambule}")
    private String webPreambule;
    @Value("${mappings.auth}")
    private String authMapping;

    @Autowired
    private JwtTokenizationAdapter jwtTokenizationAdapter;

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.builder().baseUrl(webPreambule).build();
        jwtTokenizationAdapter.setWebClient(webClient);
    }


    public JwtResponce authenticate(String login, String password) {
        return jwtTokenizationAdapter.authenticate(authMapping, login, password);
    }

}
