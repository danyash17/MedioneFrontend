package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.RegistrationForm;
import bsu.rpact.medionefrontend.validation.RegistrationFormBinder;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@Route("register")
@PreserveOnRefresh
public class RegistrationView extends VerticalLayout {

    private final UiUtils uiUtils;
    private final ValidatorUtils validatorUtils;
    private final AuthService authService;

    public RegistrationView(UiUtils uiUtils, ValidatorUtils validatorUtils, AuthService authService) {
        this.uiUtils = uiUtils;
        this.validatorUtils = validatorUtils;
        this.authService = authService;
        RegistrationForm registrationForm = new RegistrationForm();
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        add(registrationForm);

        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(this.uiUtils, this.validatorUtils, this.authService, registrationForm);
        registrationFormBinder.addBindingAndValidation();
    }
}