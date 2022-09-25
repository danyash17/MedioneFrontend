package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.enums.Role;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.vaadin.view.*;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.theme.lumo.Lumo;

public class MainLayout extends AppLayout {

    private final AuthService authService;
    private final SessionManager sessionManager;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;

    public MainLayout(AuthService authService, SessionManager sessionManager, DoctorService doctorService, ImageUtils imageUtils) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        Image logo = new Image();
        logo.setMaxWidth("90px");
        logo.setMaxHeight("90px");
        logo.setSrc("images/logo.png");

        Button logout = new Button("Log out", e ->{
            session.invalidate();
            authService.logout();
        });
        logout.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logout.setIcon(VaadinIcon.SIGN_OUT.create());
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
        HorizontalLayout logoutLayout = new HorizontalLayout();
        logoutLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        logoutLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        ToggleButton toggle = new ToggleButton();
        toggle.setLabel("Light theme");
        toggle.addValueChangeListener(event -> {
            event.getSource().setLabel(event.getValue() ? "Dark theme" : "Light theme");
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)){
                themeList.remove(Lumo.DARK);
            }
            else {
                themeList.add(Lumo.DARK);
            }
        });
        logoutLayout.add(toggle,logout);

        HorizontalLayout summaryLayout = new HorizontalLayout(header, credentials,logoutLayout);
        summaryLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        summaryLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        summaryLayout.setWidth("100%");
        summaryLayout.addClassNames("py-0", "px-m");

        addToNavbar(summaryLayout);

    }

    private void createDrawer() {
        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        RouterLink visitLink = new RouterLink("Visits",sessionManager.getRoleAttribute().equals(Role.PATIENT.name())
                ? VisitViewPatient.class : sessionManager.getRoleAttribute().equals(Role.DOCTOR.name()) ? VisitViewDoctor.class : HomeView.class);
        RouterLink profileLink = new RouterLink("Profile", ProfileView.class);
        RouterLink medcardLink = new RouterLink("Medcard", MedcardView.class);
        RouterLink documentLink = new RouterLink("Documents", DocumentView.class);
        RouterLink notesLink = new RouterLink("Notes", NoteView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());
        VerticalLayout layout;
        switch (sessionManager.getRoleAttribute()){
            case "PATIENT":{
                layout = new VerticalLayout(
                        homeLink,
                        profileLink,
                        medcardLink,
                        visitLink,
                        documentLink,
                        notesLink
                );
                break;
            }
            case "DOCTOR":{
                 layout = new VerticalLayout(
                        homeLink,
                        profileLink,
                        visitLink,
                        documentLink,
                        notesLink
                );
                 break;
            }
            case "ADMIN":{
                 layout = new VerticalLayout(
                        homeLink,
                        profileLink,
                        notesLink
                );
                 break;
            }
            default:{
                layout = new VerticalLayout(
                        homeLink
                );
            }
        }
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
                Doctor doctor = doctorService.getDoctorSelf();
                avatar.setImage(imageUtils.chacheByteArrToImageDoctor(doctor.getDoctorPhoto(),doctor.getCredentials().getFirstName() +
                        doctor.getCredentials().getPatronymic() +
                        doctor.getCredentials().getLastName()));
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
