package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.enums.Role;
import bsu.rpact.medionefrontend.pojo.other.Country;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.utils.VersionUtils;
import bsu.rpact.medionefrontend.vaadin.i18n.I18nProvider;
import bsu.rpact.medionefrontend.vaadin.view.*;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainLayout extends AppLayout implements LocaleChangeObserver {

    private final AuthService authService;
    private final SessionManager sessionManager;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;
    private final VersionUtils versionUtils;

    private Label powered;
    private Button logout;
    private ToggleButton toggle;
    private RouterLink homeLink;
    private RouterLink visitLink;
    private RouterLink profileLink;
    private RouterLink medcardLink;
    private RouterLink documentLink;
    private RouterLink notesLink;

    public MainLayout(AuthService authService, SessionManager sessionManager, DoctorService doctorService, ImageUtils imageUtils, VersionUtils versionUtils) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        this.versionUtils = versionUtils;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        VaadinSession.getCurrent().setLocale(Locale.getDefault());
        Image logo = new Image();
        logo.setMaxWidth("90px");
        logo.setMaxHeight("90px");
        logo.setSrc("images/logo.png");
        Label version = new Label();
        try {
            version = new Label(versionUtils.getCurrentVersion());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        powered = new Label(getTranslation("root.powered_by"));
        powered.getElement().getStyle().set("fontWeight", "200");
        logout = new Button(getTranslation("root.logout"), e ->{
            session.invalidate();
            authService.logout();
        });
        logout.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logout.setIcon(VaadinIcon.SIGN_OUT.create());
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(version);
        verticalLayout.add(powered);
        verticalLayout.setSpacing(false);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, verticalLayout);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        Label credsLabel = new Label(session.getAttribute("firstName")+ " " +
                session.getAttribute("patronymic") + " " +
                session.getAttribute("lastName"));
        credsLabel.getElement().getStyle().set("fontWeight", "bold");

        List<Country> countryList = new ArrayList<>();
        Country eng = new Country("English", "EN", "Europe", "images/flags/gb.png");
        countryList.add(eng);
        countryList.add(new Country("Belarusian", "BY", "Europe", "images/flags/by.png"));
        countryList.add(new Country("Russian", "RU", "Asia", "images/flags/ru.png"));
        ComboBox<Country> locales = new ComboBox<>();
        locales.setItems(countryList);
        locales.setRenderer(createRenderer());
        locales.setItemLabelGenerator(country -> country.getCode());
        locales.setAllowCustomValue(false);
        locales.setMaxWidth("100px");
        locales.setValue(eng);
        locales.addValueChangeListener(e -> {
           switch (e.getValue().getCode()){
               case "EN":{
                   getUI().get().setLocale(I18nProvider.ENGLISH);
                   break;
               }
               case "BY":{
                   getUI().get().setLocale(I18nProvider.BELARUSIAN);
                   break;
               }
               case "RU":{
                   getUI().get().setLocale(I18nProvider.RUSSIAN);
                   break;
               }
           }
        });

        Avatar avatar = createAvatar(Role.valueOf(String.valueOf(session.getAttribute("role"))));
        HorizontalLayout credentials = new HorizontalLayout(credsLabel,avatar);
        credentials.expand();
        credentials.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        credentials.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        HorizontalLayout logoutLayout = new HorizontalLayout();
        logoutLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        logoutLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        toggle = new ToggleButton();
        toggle.setLabel("Light theme");
        toggle.addValueChangeListener(event -> {
            event.getSource().setLabel(event.getValue() ? getTranslation("root.dark_theme") : getTranslation("root.light_theme"));
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)){
                themeList.remove(Lumo.DARK);
            }
            else {
                themeList.add(Lumo.DARK);
            }
        });
        logoutLayout.add(locales, toggle, logout);

        HorizontalLayout summaryLayout = new HorizontalLayout(header, credentials,logoutLayout);
        summaryLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        summaryLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        summaryLayout.setWidth("100%");
        summaryLayout.addClassNames("py-0", "px-m");

        addToNavbar(summaryLayout);

    }

    private void createDrawer() {
        homeLink = new RouterLink(getTranslation("root.navbar.home"), HomeView.class);
        visitLink = new RouterLink(getTranslation("root.navbar.visits"),sessionManager.getRoleAttribute().equals(Role.PATIENT.name())
                ? VisitViewPatient.class : sessionManager.getRoleAttribute().equals(Role.DOCTOR.name()) ? VisitViewDoctor.class : HomeView.class);
        profileLink = new RouterLink(getTranslation("root.navbar.profile"), ProfileView.class);
        medcardLink = new RouterLink(getTranslation("root.navbar.medcard"), MedcardView.class);
        documentLink = new RouterLink(getTranslation("root.navbar.documents"), DocumentView.class);
        notesLink = new RouterLink(getTranslation("root.navbar.notes"), NoteView.class);
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

    private Renderer<Country> createRenderer() {
        StringBuilder tpl = new StringBuilder();
        tpl.append("<div style=\"display: flex;\">");
        tpl.append("  <img style=\"height: var(--lumo-size-m);\" src=\"${item.flag}\"/>");
        tpl.append("</div>");

        return LitRenderer.<Country>of(tpl.toString()).withProperty("flag", Country::getFlag);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        powered.setText(getTranslation("root.powered_by"));
        logout.setText(getTranslation("root.logout"));
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            toggle.setLabel(getTranslation("root.dark_theme"));
        }
        else {
            toggle.setLabel(getTranslation("root.light_theme"));
        }
        homeLink.setText(getTranslation("root.navbar.home"));
        visitLink.setText(getTranslation("root.navbar.visits"));
        profileLink.setText(getTranslation("root.navbar.profile"));
        medcardLink.setText(getTranslation("root.navbar.medcard"));
        documentLink.setText(getTranslation("root.navbar.documents"));
        notesLink.setText(getTranslation("root.navbar.notes"));
    }
}
