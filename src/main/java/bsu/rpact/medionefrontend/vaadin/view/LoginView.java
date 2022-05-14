package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.utils.UiUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Route("auth")
@PreserveOnRefresh
public class LoginView extends Composite<VerticalLayout> {

    private final AuthService authService;
    private final CookieHelper cookieHelper;
    private final UiUtils uiUtils;


    public LoginView(AuthService authService, CookieHelper cookieHelper, UiUtils uiUtils) {
        String route = RouteConfiguration.forSessionScope()
                .getUrl(RegistrationView.class);
        Anchor link = new Anchor(route, "New here? Click and register");
        this.authService = authService;
        this.cookieHelper = cookieHelper;
        this.uiUtils = uiUtils;
        VerticalLayout layout = getContent();
        LoginForm loginForm = new LoginForm();
        H1 header = new H1("Medione");
        loginForm.setForgotPasswordButtonVisible(false);

        loginForm.addLoginListener(loginEvent -> {
            try {
                JwtResponce jwtResponce = this.authService.authenticate(loginEvent.getUsername(), loginEvent.getPassword());
                this.cookieHelper.initTokenCookie(jwtResponce.getToken());
                loginForm.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            }
            catch (WebClientResponseException e){
                this.uiUtils.generateErrorNotification("Failed to login").open();
            }
            finally {
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
