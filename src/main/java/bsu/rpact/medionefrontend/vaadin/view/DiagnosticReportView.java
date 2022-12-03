package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "report", layout = MainLayout.class)
@PageTitle("Diagnostic Report")
public class DiagnosticReportView extends VerticalLayout {
}
