package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.JwtTokenizationAdapter;
import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.*;
import bsu.rpact.medionefrontend.security.TwoFactorAuthenticationProvider;
import bsu.rpact.medionefrontend.utils.UiUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Service
public class AuthService {

    @Value("${mappings.preambule}")
    private String restPreambule;
    @Value(("${frontend.mappings.preambule}"))
    private String frontendPreambule;
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

    @Autowired
    private JwtTokenizationAdapter jwtTokenizationAdapter;
    @Autowired
    private CookieHelper cookieHelper;
    @Autowired
    private UiUtils uiUtils;
    @Autowired
    private TwoFactorAuthenticationProvider provider;

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.builder().baseUrl(restPreambule).build();
        jwtTokenizationAdapter.setWebClient(webClient);
    }


    public TotpResponce authenticate(String login, String password) {
        return jwtTokenizationAdapter.totpAuthenticate(loginMapping, login, password);
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

    public JwtResponce twoFactorSMSAuthenticate(String code) {
        TwoFactorAuthenticationRequest twoFactorAuthenticationRequest = new TwoFactorAuthenticationRequest(
                provider.getTemporaryRequest().getLogin(),
                provider.getTemporaryRequest().getPassword(),
                null,
                code);
        return jwtTokenizationAdapter.twoFactorAuthenticate(twoFactorSmsVerifyMapping,twoFactorAuthenticationRequest);
    }

    public JwtResponce twoFactorQRAuthenticate(String code) {
        TwoFactorAuthenticationRequest twoFactorAuthenticationRequest = new TwoFactorAuthenticationRequest(
                provider.getTemporaryRequest().getLogin(),
                provider.getTemporaryRequest().getPassword(),
                code,
                null);
        return jwtTokenizationAdapter.twoFactorAuthenticate(twoFactorQrMapping,twoFactorAuthenticationRequest);
    }

    public void sendSms() {
        LoginRequest loginRequest = new LoginRequest(
                provider.getTemporaryRequest().getLogin(),
                provider.getTemporaryRequest().getPassword());
        jwtTokenizationAdapter.sendSms(twoFactorSmsMapping,loginRequest);
    }
}
