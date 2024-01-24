package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.vaadin.components.RegistrationForm;
import bsu.rpact.medionefrontend.validation.RegistrationFormBinder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@Route("register")
@PreserveOnRefresh
public class RegistrationView extends VerticalLayout {

    private final AuthService authService;

    public RegistrationView(AuthService authService) {
        this.authService = authService;
        RegistrationForm registrationForm = new RegistrationForm();
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        add(registrationForm);

        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(this.authService, registrationForm);
        registrationFormBinder.addBindingAndValidation();
    }
}