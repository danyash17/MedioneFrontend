package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.adapter.GeneralAdapter;
import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.LoginRequest;
import bsu.rpact.medionefrontend.pojo.authentication.PrimaryLoginResponce;
import bsu.rpact.medionefrontend.security.TwoFactorAuthenticationProvider;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.components.LocalePicker;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Locale;

@Route("auth")
@RouteAlias(value = "")
@PreserveOnRefresh
public class LoginView extends Composite<VerticalLayout> implements LocaleChangeObserver {

    private final AuthService authService;
    private final TwoFactorAuthenticationProvider provider;
    private final CookieHelper cookieHelper;
    private final SessionManager sessionManager;
    private final ApplicationContext context;
    private final Anchor link;

    private final LoginI18n i18n = LoginI18n.createDefault();;
    private final LoginForm loginForm;
    private String failedMessage;


    public LoginView(AuthService authService, TwoFactorAuthenticationProvider provider, CookieHelper cookieHelper, SessionManager sessionManager, ApplicationContext ctx) {
        this.authService = authService;
        this.provider = provider;
        this.sessionManager = sessionManager;
        this.cookieHelper = cookieHelper;
        this.context = ctx;
        VaadinSession.getCurrent().setLocale(Locale.getDefault());
        String route = RouteConfiguration.forSessionScope()
                .getUrl(RegistrationView.class);
        link = new Anchor(route, getTranslation("login.register"));
        VerticalLayout layout = getContent();
        loginForm = new LoginForm();
        Image logo = new Image();
        logo.setMaxWidth("270px");
        logo.setMaxHeight("270px");
        logo.setSrc("images/logo.png");
        loginForm.setForgotPasswordButtonVisible(false);

        setupLoginI18n(i18n);

        LocalePicker localePicker = new LocalePicker();
        loginForm.addLoginListener(loginEvent -> {
            try {
                PrimaryLoginResponce primaryLoginResponce = this.authService.authenticate(loginEvent.getUsername(), loginEvent.getPassword());
                sessionManager.set2FaAttribute(primaryLoginResponce.isEnabled2Fa());
                if (primaryLoginResponce != null && primaryLoginResponce.isEnabled2Fa()) {
                    provider.setTemporaryRequest(new LoginRequest(loginEvent.getUsername(), loginEvent.getPassword()));
                    provider.setTotpResponce(primaryLoginResponce);
                    UI.getCurrent().navigate(TwoFactorAuthenticationView.class);
                } else if (primaryLoginResponce != null) {
                    this.cookieHelper.addTokenCookie(primaryLoginResponce.getToken(), 90000);
                    this.sessionManager.generateAuthUserAttributes(primaryLoginResponce);
                    this.sessionManager.setTokenAttribute(primaryLoginResponce.getToken());
                    context.getBeansOfType(GeneralAdapter.class).entrySet().stream()
                            .forEach((adapter)->adapter.getValue().authenticateWebClient(primaryLoginResponce.getToken()));
                    UI.getCurrent().navigate(HomeView.class);
                }
            } catch (WebClientResponseException e) {
                failedMessage = getTranslation("login.error");
                UiUtils.generateErrorNotification(failedMessage).open();
            } finally {
                loginForm.setEnabled(true);
            }
        });
        layout.add(
                logo,
                localePicker,
                loginForm,
                link
        );
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    private LoginI18n setupLoginI18n(LoginI18n i18n) {
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle(getTranslation("login.form.title"));
        i18nForm.setUsername(getTranslation("login.form.username"));
        i18nForm.setPassword(getTranslation("login.form.password"));
        i18nForm.setSubmit(getTranslation("login.form.submit"));
        i18n.setForm(i18nForm);
        loginForm.setI18n(i18n);
        return i18n;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        setupLoginI18n(i18n);
        link.setText(getTranslation("login.register"));
    }
}
