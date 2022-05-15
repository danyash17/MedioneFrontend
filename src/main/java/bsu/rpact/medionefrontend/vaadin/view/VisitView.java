package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "visit", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitView extends VerticalLayout {

    public VisitView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }
}