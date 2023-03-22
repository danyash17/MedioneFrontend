package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route(value = "docOrigination", layout = MainLayout.class)
@PageTitle("Documents")
public class DoctorDocumentView extends VerticalLayout{

    public DoctorDocumentView() {
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setAlignItems(Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout();
        H2 h2 = new H2("Start document origination process");
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button prescription = new Button("Medical prescription");
        Button observation = new Button("Observation");
        observation.setEnabled(false);
        Button procedure = new Button("Procedure");
        procedure.setEnabled(false);
        Button diagReport = new Button("Diagnostic report");
        diagReport.setEnabled(false);
        horizontalLayout.add(prescription,observation,procedure,diagReport);
        verticalLayout.add(h2);
        verticalLayout.add(horizontalLayout);
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(verticalLayout);
    }
}
