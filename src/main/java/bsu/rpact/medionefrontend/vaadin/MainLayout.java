package bsu.rpact.medionefrontend.vaadin;

import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.view.HomeView;
import bsu.rpact.medionefrontend.vaadin.view.RestrictedView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

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
        H3 logo = new H3("Medione");

        Button logout = new Button("Log out", e ->{
            authService.logout();
        });
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink("Home", HomeView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                listLink
        ));
    }
}
