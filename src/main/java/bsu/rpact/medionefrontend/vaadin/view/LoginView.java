package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.pojo.authentication.LoginRequest;
import bsu.rpact.medionefrontend.pojo.authentication.TotpResponce;
import bsu.rpact.medionefrontend.security.TwoFactorAuthenticationProvider;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.UiUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Route("auth")
@RouteAlias(value = "")
@PreserveOnRefresh
public class LoginView extends Composite<VerticalLayout> {

    private final AuthService authService;
    private final TwoFactorAuthenticationProvider provider;
    private final CookieHelper cookieHelper;
    private final SessionManager sessionManager;
    private final UiUtils uiUtils;


    public LoginView(AuthService authService, TwoFactorAuthenticationProvider provider, CookieHelper cookieHelper, SessionManager sessionManager, UiUtils uiUtils) {
        this.authService = authService;
        this.provider = provider;
        this.sessionManager = sessionManager;
        this.cookieHelper = cookieHelper;
        this.uiUtils = uiUtils;
        String route = RouteConfiguration.forSessionScope()
                .getUrl(RegistrationView.class);
        Anchor link = new Anchor(route, "New here? Click and register");
        VerticalLayout layout = getContent();
        LoginForm loginForm = new LoginForm();
        H1 header = new H1("Medione");
        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.addLoginListener(loginEvent -> {
            try {
                TotpResponce totpResponce = this.authService.authenticate(loginEvent.getUsername(), loginEvent.getPassword());
                if (totpResponce != null && totpResponce.isEnabled2Fa()) {
                    provider.setTemporaryRequest(new LoginRequest(loginEvent.getUsername(), loginEvent.getPassword()));
                    provider.setTotpResponce(totpResponce);
                    loginForm.getUI().ifPresent(ui -> ui.navigate(TwoFactorAuthenticationView.class));
                } else if (totpResponce != null) {
                    this.cookieHelper.addTokenCookie(totpResponce.getToken(), 90000);
                    this.sessionManager.generateAuthUserAttributes(totpResponce);
                    loginForm.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
                }
            } catch (WebClientResponseException e) {
                this.uiUtils.generateErrorNotification("Failed to login").open();
            } finally {
                loginForm.setEnabled(true);
            }
        });
        layout.add(
                header,
                loginForm,
                link
        );
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

}
