package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Credentials;
import bsu.rpact.medionefrontend.service.CredentialsService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends VerticalLayout {

    private final SessionManager sessionManager;
    private final CredentialsService credentialsService;

    public ProfileView(SessionManager sessionManager, CredentialsService credentialsService) {
        this.sessionManager = sessionManager;
        this.credentialsService = credentialsService;
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        setSpacing(false);

        add(new Paragraph("Profile settings"));
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
    }
}