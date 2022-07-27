package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.adapter.GeneralAdapter;
import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.LoginRequest;
import bsu.rpact.medionefrontend.pojo.authentication.PrimaryLoginResponce;
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
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Route("auth")
@RouteAlias(value = "")
@PreserveOnRefresh
public class LoginView extends Composite<VerticalLayout> {

    private final AuthService authService;
    private final TwoFactorAuthenticationProvider provider;
    private final CookieHelper cookieHelper;
    private final SessionManager sessionManager;
    private final ApplicationContext context;


    public LoginView(AuthService authService, TwoFactorAuthenticationProvider provider, CookieHelper cookieHelper, SessionManager sessionManager, ApplicationContext ctx) {
        this.authService = authService;
        this.provider = provider;
        this.sessionManager = sessionManager;
        this.cookieHelper = cookieHelper;
        this.context = ctx;
        String route = RouteConfiguration.forSessionScope()
                .getUrl(RegistrationView.class);
        Anchor link = new Anchor(route, "New here? Click and register");
        VerticalLayout layout = getContent();
        LoginForm loginForm = new LoginForm();
        H1 header = new H1("Medione");
        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.addLoginListener(loginEvent -> {
            try {
                PrimaryLoginResponce primaryLoginResponce = this.authService.authenticate(loginEvent.getUsername(), loginEvent.getPassword());
                sessionManager.set2FaAttribute(primaryLoginResponce.isEnabled2Fa());
                if (primaryLoginResponce != null && primaryLoginResponce.isEnabled2Fa()) {
                    provider.setTemporaryRequest(new LoginRequest(loginEvent.getUsername(), loginEvent.getPassword()));
                    provider.setTotpResponce(primaryLoginResponce);
                    loginForm.getUI().ifPresent(ui -> ui.navigate(TwoFactorAuthenticationView.class));
                } else if (primaryLoginResponce != null) {
                    this.cookieHelper.addTokenCookie(primaryLoginResponce.getToken(), 90000);
                    this.sessionManager.generateAuthUserAttributes(primaryLoginResponce);
                    this.sessionManager.setTokenAttribute(primaryLoginResponce.getToken());
                    context.getBeansOfType(GeneralAdapter.class).entrySet().stream()
                            .forEach((adapter)->adapter.getValue().authenticateWebClient(primaryLoginResponce.getToken()));
                    loginForm.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
                }
            } catch (WebClientResponseException e) {
                UiUtils.generateErrorNotification("Failed to login").open();
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
