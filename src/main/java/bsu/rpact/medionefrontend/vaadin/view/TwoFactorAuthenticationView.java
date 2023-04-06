package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.adapter.demographic.DemographicBaseAdapter;
import bsu.rpact.medionefrontend.session.cookie.CookieHelper;
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
import com.vaadin.flow.component.messages.MessageInputI18n;
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
    private final String thisIsA2FaPage = getTranslation("two.fact.this_is_a_2fa_page");
    private final String instruction = getTranslation("two.fact.instruction");
    private final String sendCode = getTranslation("two.fact.send_code");
    private final String enterYourPhoneCodeHere = getTranslation("two.fact.enter_your_phone_code_here");
    private final String codeFromAuthenticator = getTranslation("two.fact.code_from_authenticator");
    private final String codeFromSms = getTranslation("two.fact.code_from_sms");
    private final String send = getTranslation("two.fact.send");

    public TwoFactorAuthenticationView(AuthService authService, TwoFactorAuthenticationProvider provider, CookieHelper cookieHelper, SessionManager sessionManager, ApplicationContext context) {
        this.authService = authService;
        this.provider = provider;
        this.cookieHelper = cookieHelper;
        this.sessionManager = sessionManager;
        this.context = context;
        setSpacing(false);

        Image img = new Image(provider.getTotpResponce().getQrUri(), "restricted");
        add(img);

        add(new H2(thisIsA2FaPage));
        add(new Paragraph(instruction));
        MessageInput input = new MessageInput();
        MessageInputI18n messageInputI18n = new MessageInputI18n();
        messageInputI18n.setMessage(codeFromAuthenticator);
        messageInputI18n.setSend(send);
        input.setI18n(messageInputI18n);
        input.addSubmitListener(submitEvent -> {
            JwtResponce jwtResponce = authService.twoFactorQRAuthenticate(submitEvent.getValue());
            if(jwtResponce!=null){
                this.cookieHelper.addTokenCookie(jwtResponce.getToken(), 90000);
                this.sessionManager.generateAuthUserAttributes(jwtResponce);
                context.getBeansOfType(DemographicBaseAdapter.class).entrySet().stream().forEach((adapter)->adapter.getValue().initWebClient());
                input.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            }
        });
        add(input);
        Button codeButton = new Button(sendCode);
        codeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        codeButton.addClickListener(buttonClickEvent -> {
            authService.sendSms();
        });
        codeButton.setDisableOnClick(true);
        add(codeButton);
        MessageInput sms = new MessageInput();
        MessageInputI18n messageInputI18nSms = new MessageInputI18n();
        messageInputI18nSms.setMessage(codeFromSms);
        messageInputI18nSms.setSend(send);
        sms.setI18n(messageInputI18nSms);
        sms.addSubmitListener(submitEvent -> {
            JwtResponce jwtResponce = authService.twoFactorSMSAuthenticate(submitEvent.getValue());
            if(jwtResponce!=null){
                this.cookieHelper.addTokenCookie(jwtResponce.getToken(), 90000);
                this.sessionManager.generateAuthUserAttributes(jwtResponce);
                input.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            }
        });
        add(new Paragraph(enterYourPhoneCodeHere), sms);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}
