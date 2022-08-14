package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Credentials;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.service.CredentialsService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends VerticalLayout {

    private final SessionManager sessionManager;
    private final CredentialsService credentialsService;
    private final AuthService authService;

    public ProfileView(SessionManager sessionManager, CredentialsService credentialsService, AuthService authService) {
        this.sessionManager = sessionManager;
        this.credentialsService = credentialsService;
        this.authService = authService;
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        add(new H3("Profile data"));
        add(new Label("First name: " + sessionManager.getFirstNameAttribute()));
        add(new Label("Patronymic: " + sessionManager.getPatronymicAttribute()));
        add(new Label("Last name: " + sessionManager.getLastNameAttribute()));
        add(new Label("Phone: " + sessionManager.getPhoneAttribute()));
        add(new Label("Role: " + sessionManager.getRoleAttribute()));
        add(new Label("Two-factor authorization enabled: " + sessionManager.get2FaAttribute().booleanValue()));
        add(new H3("Profile settings"));
        Checkbox checkbox = new Checkbox();
        checkbox.setLabel("Use Two-Factor Authentication for this account");
        checkbox.setValue(session.getAttribute("2FA").equals("true"));
        add(checkbox);
        HorizontalLayout layout = new HorizontalLayout();
        Button apply = new Button("Apply", e -> {
            Credentials credentials = credentialsService.getSelf();
            credentials.setEnabled2Fa(checkbox.getValue());
            credentialsService.update(credentials, true);
            sessionManager.set2FaAttribute(checkbox.getValue());
            UiUtils.generateSuccessNotification("Profile settings are updated. \n Two-factor authentication:" + checkbox.getValue()).open();
        });
        layout.add(apply);
        add(layout);
        add(new H3("Change password"));
        PasswordField password = new PasswordField();
        password.setLabel("New password");
        PasswordField passwordConfirmation = new PasswordField();
        passwordConfirmation.setLabel("Confirm new password");
        Button applyPassword = new Button("Apply");
        applyPassword.addClickListener(e -> {
            if(ValidatorUtils.isValidPassword(password.getValue())){
                if(password.getValue().equals(passwordConfirmation.getValue())) {
                    Credentials credentials = credentialsService.getSelf();
                    credentials.setPassword(password.getValue());
                    credentialsService.update(credentials, true);
                    UiUtils.generateSuccessNotification("You successfully changed your password").open();
                    password.clear();
                    passwordConfirmation.clear();
                    password.setInvalid(false);
                    passwordConfirmation.setInvalid(false);
                }
                else {
                    passwordConfirmation.clear();
                    passwordConfirmation.setErrorMessage("Passwords does not match");
                    passwordConfirmation.setInvalid(true);
                }
            }
            else {
                password.clear();
                passwordConfirmation.clear();
                password.setErrorMessage("Password must be" +
                        "8-20 length\n" +
                        ",contain at least one letter\n" +
                        ",not contain any white space.");
                password.setInvalid(true);
            }
        });
        add(password,passwordConfirmation,applyPassword);
    }
}