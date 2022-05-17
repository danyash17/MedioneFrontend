package bsu.rpact.medionefrontend.vaadin;

import bsu.rpact.medionefrontend.enums.Role;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.view.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

public class MainLayout extends AppLayout {

    private final AuthService authService;
    private final UiUtils uiUtils;

    public MainLayout(AuthService authService, UiUtils uiUtils) {
        this.authService = authService;
        this.uiUtils = uiUtils;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        H3 logo = new H3("Medione");

        Button logout = new Button("Log out", e ->{
            authService.logout();
        });
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        Label credsLabel = new Label(session.getAttribute("firstName")+ " " +
                session.getAttribute("patronymic") + " " +
                session.getAttribute("lastName"));
        credsLabel.addClassNames("bold");


        Avatar avatar = createAvatar(Role.valueOf(String.valueOf(session.getAttribute("role"))));
        HorizontalLayout credentials = new HorizontalLayout(credsLabel,avatar);
        credentials.expand();
        credentials.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        credentials.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        HorizontalLayout logoutLayout = new HorizontalLayout(logout);
        logoutLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        logoutLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);


        HorizontalLayout summaryLayout = new HorizontalLayout(header, credentials,logoutLayout);
        summaryLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        summaryLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        summaryLayout.setWidth("100%");
        summaryLayout.addClassNames("py-0", "px-m");

        addToNavbar(summaryLayout);

    }

    private void createDrawer() {
        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink visitLink = new RouterLink("Visits", VisitView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink profileLink = new RouterLink("Profile", ProfileView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink documentLink = new RouterLink("Documents", DocumentView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink notesLink = new RouterLink("Notes", NoteView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());
        VerticalLayout layout = new VerticalLayout(
                homeLink,
                profileLink,
                visitLink,
                documentLink,
                notesLink
        );
        addToDrawer(layout);
    }

    public Avatar createAvatar(Role role){
        Avatar avatar = new Avatar();
        switch (role){
            case PATIENT: {
                avatar.setImage("images/patientAvatar.png");
                break;
            }
            case DOCTOR: {
                avatar.setImage("images/doctorAvatar.png");
                break;
            }
            case ADMIN: {
                avatar.setImage("images/adminAvatar.png");
                break;
            }
        }
        return avatar;
    }
}
