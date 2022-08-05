package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.vaadin.MainLayout;
import bsu.rpact.medionefrontend.vaadin.VisitDiv;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "visit", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitView extends VerticalLayout {

    public VisitView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        add(new H2("Visits"));
        H3 active = new H3("Active");
        H3 archive = new H3("Archive");
        HorizontalLayout layout = new HorizontalLayout(active,archive);
        layout.setJustifyContentMode(JustifyContentMode.EVENLY);
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidthFull();
        add(layout);
        VisitDiv visitDiv = new VisitDiv();
        add(visitDiv);
        Paragraph paragraph = new Paragraph("Want to do a visit reservation?");
        Button button = new Button("New visit");
        button.addClickListener(e -> {
            UI.getCurrent().navigate(VisitCreationView.class);
        });
        add(paragraph,button);
    }

}