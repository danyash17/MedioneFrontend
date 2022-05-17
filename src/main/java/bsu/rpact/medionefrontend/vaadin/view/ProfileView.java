package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

import java.awt.*;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends VerticalLayout {

    public ProfileView() {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
    }
}