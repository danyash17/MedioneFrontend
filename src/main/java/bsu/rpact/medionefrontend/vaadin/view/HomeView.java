package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

@PageTitle("Home")
@PreserveOnRefresh
@Route(value = "home", layout = MainLayout.class)
public class HomeView extends VerticalLayout implements LocaleChangeObserver {

    WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();

    private String welcome = getTranslation("home.welcome");
    private String provide = getTranslation("home.provide");
    private String profile = getTranslation("home.profile");
    private String visits = getTranslation("home.visits");
    private String documents = getTranslation("home.documents");
    private String notes = getTranslation("home.notes");
    private final H2 h2 = new H2(welcome + session.getAttribute("firstName"));
    private final Paragraph provideParagraph = new Paragraph(provide);
    private final Paragraph profileParagraph = new Paragraph(profile);
    private final Paragraph visitsParagraph = new Paragraph(visits);
    private final Paragraph documentsParagraph = new Paragraph(documents);
    private final Paragraph notesParagraph = new Paragraph(notes);

    public HomeView() {

        setSpacing(false);

        Image img = new Image("images/greeting.png", "greeting");
        img.setWidth("200px");
        add(img);
        add(h2);
        add(provideParagraph);
        add(profileParagraph);
        add(visitsParagraph);
        add(documentsParagraph);
        add(notesParagraph);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        welcome = getTranslation("home.welcome");
        provide = getTranslation("home.provide");
        profile = getTranslation("home.profile");
        visits = getTranslation("home.visits");
        documents = getTranslation("home.documents");
        notes = getTranslation("home.notes");
        h2.setText(welcome + session.getAttribute("firstName"));
        provideParagraph.setText(provide);
        profileParagraph.setText(profile);
        visitsParagraph.setText(visits);
        documentsParagraph.setText(documents);
        notesParagraph.setText(notes);
    }
}
