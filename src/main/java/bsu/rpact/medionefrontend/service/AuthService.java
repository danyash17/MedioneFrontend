package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.JwtTokenizationAdapter;
import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.pojo.authentication.RegisterRequest;
import bsu.rpact.medionefrontend.utils.UiUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Service
public class AuthService {

    @Value("${mappings.preambule}")
    private String restPreambule;
    @Value(("${frontend.mappings.preambule}"))
    private String frontendPreambule;
    @Value("${mappings.auth}")
    private String loginMapping;
    @Value("${mappings.register}")
    private String registerMapping;

    @Autowired
    private JwtTokenizationAdapter jwtTokenizationAdapter;
    @Autowired
    private CookieHelper cookieHelper;
    @Autowired
    private UiUtils uiUtils;

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.builder().baseUrl(restPreambule).build();
        jwtTokenizationAdapter.setWebClient(webClient);
    }


    public JwtResponce authenticate(String login, String password) {
        return jwtTokenizationAdapter.authenticate(loginMapping, login, password);
    }

    public MessageResponse register(RegisterRequest registerRequest){
        return jwtTokenizationAdapter.register(registerMapping, registerRequest);
    }

    public void logout() {
        cookieHelper.generateTerminatorCookie();
        UI.getCurrent().getSession().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        UI.getCurrent().getPage().setLocation(frontendPreambule);
        uiUtils.generateSuccessNotification("Logged out successfully").open();
    }

}
