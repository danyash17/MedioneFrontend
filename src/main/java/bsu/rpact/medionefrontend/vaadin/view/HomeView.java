package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;

@PageTitle("Home")
@PreserveOnRefresh
@Route(value = "home", layout = MainLayout.class)
public class HomeView extends VerticalLayout {


    public HomeView() {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();

        setSpacing(false);

        Image img = new Image("images/greeting.png", "greeting");
        img.setWidth("200px");
        add(img);

        add(new H2("Glad to see you, " + session.getAttribute("firstName")));
        add(new Paragraph("Medione Web app provide you an opportunity to manage your visits, documents and notes!"));
        add(new Paragraph("▪To observate and change your profile data, click Profile at the side menu"));
        add(new Paragraph("▪To rule your visits, click Visits at the side menu"));
        add(new Paragraph("▪To work out with your documents, click Documents at the side menu"));
        add(new Paragraph("▪To check your notes, click Notes at the side menu"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
