package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.JwtTokenizationAdapter;
import bsu.rpact.medionefrontend.session.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.*;
import bsu.rpact.medionefrontend.security.TwoFactorAuthenticationProvider;
import bsu.rpact.medionefrontend.utils.UiUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Value(("${frontend.mappings.preambule}"))
    protected String frontendPreambule;

    @Autowired
    private JwtTokenizationAdapter adapter;
    @Autowired
    private CookieHelper cookieHelper;
    @Autowired
    private TwoFactorAuthenticationProvider provider;

    public PrimaryLoginResponce authenticate(String login, String password) {
        return adapter.primaryAuthenticate(login, password);
    }

    public MessageResponse register(RegisterRequest registerRequest){
        return adapter.register(registerRequest);
    }

    public void logout() {
        cookieHelper.generateTerminatorCookie();
        UI.getCurrent().getSession().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        UI.getCurrent().getPage().setLocation(frontendPreambule);
        UiUtils.generateSuccessNotification("Logged out successfully").open();
    }

    public JwtResponce twoFactorSMSAuthenticate(String code) {
        TwoFactorAuthenticationRequest twoFactorAuthenticationRequest = new TwoFactorAuthenticationRequest(
                provider.getTemporaryRequest().getLogin(),
                provider.getTemporaryRequest().getPassword(),
                null,
                code);
        return adapter.twoFactorAuthenticate(twoFactorAuthenticationRequest);
    }

    public JwtResponce twoFactorQRAuthenticate(String code) {
        TwoFactorAuthenticationRequest twoFactorAuthenticationRequest = new TwoFactorAuthenticationRequest(
                provider.getTemporaryRequest().getLogin(),
                provider.getTemporaryRequest().getPassword(),
                code,
                null);
        return adapter.twoFactorAuthenticate(twoFactorAuthenticationRequest);
    }

    public void sendSms() {
        LoginRequest loginRequest = new LoginRequest(
                provider.getTemporaryRequest().getLogin(),
                provider.getTemporaryRequest().getPassword());
        adapter.sendSms(loginRequest);
    }
}
