package bsu.rpact.medionefrontend.vaadin.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;

@PageTitle("Home")
@PreserveOnRefresh
@Route(value = "restricted")
public class RestrictedView extends VerticalLayout {

    public RestrictedView() {
        setSpacing(false);

        Image img = new Image("images/restricted.png", "restricted");
        img.setWidth("200px");
        add(img);

        add(new H2("Oops, it seems like you trying to access secured page without authorization"));
        add(new Paragraph("Look around, little hacker :)"));
        String route = RouteConfiguration.forSessionScope()
                .getUrl(LoginView.class);
        Anchor link = new Anchor(route, "Return to login");
        add(link);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
