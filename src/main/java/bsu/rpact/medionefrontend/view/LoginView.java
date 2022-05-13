package bsu.rpact.medionefrontend.view;

import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.service.LoginService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;


@Route("auth")
public class LoginView extends Composite<LoginOverlay> {

    @Autowired
    private LoginService loginService;
    @Autowired
    private CookieHelper cookieHelper;



    public LoginView() {
        LoginOverlay loginOverlay = getContent();
        loginOverlay.setTitle("Medione✚");
        loginOverlay.setDescription("The one to trust");
        loginOverlay.setOpened(true);

        loginOverlay.addLoginListener(loginEvent -> {
            JwtResponce jwtResponce = loginService.authenticate(loginEvent.getUsername(), loginEvent.getPassword());
            cookieHelper.initTokenCookie(jwtResponce.getToken());
            loginOverlay.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
        });

    }

}
