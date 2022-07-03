package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.adapter.GeneralAdapter;
import bsu.rpact.medionefrontend.cookie.CookieHelper;
import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import bsu.rpact.medionefrontend.security.TwoFactorAuthenticationProvider;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.session.SessionManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import org.springframework.context.ApplicationContext;

@PageTitle("2FA")
@PreserveOnRefresh
@Route(value = "2FA")
public class TwoFactorAuthenticationView extends VerticalLayout {
    private final AuthService authService;
    private final TwoFactorAuthenticationProvider provider;
    private final CookieHelper cookieHelper;
    private final SessionManager sessionManager;
    private final ApplicationContext context;

    public TwoFactorAuthenticationView(AuthService authService, TwoFactorAuthenticationProvider provider, CookieHelper cookieHelper, SessionManager sessionManager, ApplicationContext context) {
        this.authService = authService;
        this.provider = provider;
        this.cookieHelper = cookieHelper;
        this.sessionManager = sessionManager;
        this.context = context;
        setSpacing(false);

        Image img = new Image(provider.getTotpResponce().getQrUri(), "restricted");
        add(img);

        add(new H2("This is an 2FA page"));
        add(new Paragraph("You could authenticate via Google Authenticator mobile app " +
                "or by sending a verification code to your phone"));
        MessageInput input = new MessageInput();
        input.addSubmitListener(submitEvent -> {
            JwtResponce jwtResponce = authService.twoFactorQRAuthenticate(submitEvent.getValue());
            if(jwtResponce!=null){
                this.cookieHelper.addTokenCookie(jwtResponce.getToken(), 90000);
                this.sessionManager.generateAuthUserAttributes(jwtResponce);
                context.getBeansOfType(GeneralAdapter.class).entrySet().stream().forEach((adapter)->adapter.getValue().initWebClient());
                input.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            }
        });
        add(input);
        Paragraph paragraph = new Paragraph("Or type a code from SMS");
        Button codeButton = new Button("Send code");
        codeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        codeButton.addClickListener(buttonClickEvent -> {
            authService.sendSms();
        });
        codeButton.setDisableOnClick(true);
        add(paragraph, codeButton);
        MessageInput sms = new MessageInput();
        sms.addSubmitListener(submitEvent -> {
            JwtResponce jwtResponce = authService.twoFactorSMSAuthenticate(submitEvent.getValue());
            if(jwtResponce!=null){
                this.cookieHelper.addTokenCookie(jwtResponce.getToken(), 90000);
                this.sessionManager.generateAuthUserAttributes(jwtResponce);
                input.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            }
        });
        add(new Paragraph("Enter your phone code here "), sms);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}
