package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.enums.FhirId;
import bsu.rpact.medionefrontend.session.FhirCashingContainer;
import bsu.rpact.medionefrontend.utils.FhirBadgeInterpretator;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Procedure;
import org.vaadin.addons.badge.Badge;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Route(value = "procedure", layout = MainLayout.class)
@PageTitle("Procedure")
public class ProcedureView extends VerticalLayout {

    private String PATTERN = "yyyy-MM-dd";
    private SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);

    private final FhirCashingContainer container;

    public ProcedureView(FhirCashingContainer container) {
        this.container = container;
        Procedure procedure = container.getProcedure();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);
        Optional<Identifier> frontendId = procedure.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        H3 header = new H3("Procedure " + (frontendId.isPresent() ? "[" + frontendId.get().getValue() + "]" : "UNDEFINED"));
        layout.add(header);
        Label status = new Label("Status: ");
        Label statusDisplay = new Label(procedure.getStatus().getDisplay());
        Label issued = new Label("Issued: ");
        Label issuedValue = new Label(SIMPLE_DATE_FORMAT.format(procedure.getPerformedDateTimeType().getValue()));
        add(new HorizontalLayout(issued, issuedValue));
        Grid<Coding> procedureGrid = new Grid<>();
        procedureGrid.setItems(procedure.getCode().getCoding());
        procedureGrid.addColumn(Coding::getDisplay).setHeader("Name");
        procedureGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, coding) -> {
            anchor.setText(coding.getCode());
            anchor.setHref(coding.getSystem() + "/" + coding.getCode());
        })).setHeader("Code");
        procedureGrid.setAllRowsVisible(true);
        Label label = new Label("Reason: ");
        Label reason = new Label(procedure.getReasonReference().get(0).getDisplay());
        reason.getElement().getStyle().set("fontWeight", "bold");
        Grid<CodeableConcept> bodyGrid = new Grid<>();
        bodyGrid.setItems(procedure.getBodySite());
        bodyGrid.addColumn(item -> item.getCodingFirstRep().getDisplay()).setHeader("Body part");
        bodyGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, concept) -> {
            anchor.setText(concept.getCodingFirstRep().getCode());
            anchor.setHref(concept.getCodingFirstRep().getSystem() + "/" + concept.getCodingFirstRep().getCode());
        })).setHeader("Code");
        bodyGrid.setAllRowsVisible(true);
        Grid<CodeableConcept> complicationGrid = new Grid<>();
        complicationGrid.setItems(procedure.getComplication());
        complicationGrid.addColumn(item -> item.getCodingFirstRep().getDisplay()).setHeader("Complication");
        complicationGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, concept) -> {
            anchor.setText(concept.getCodingFirstRep().getCode());
            anchor.setHref(concept.getCodingFirstRep().getSystem() + "/" + concept.getCodingFirstRep().getCode());
        })).setHeader("Code");
        complicationGrid.setAllRowsVisible(true);
        Grid<CodeableConcept> usedGrid = new Grid<>();
        usedGrid.setItems(procedure.getUsedCode());
        usedGrid.addColumn(item -> item.getCodingFirstRep().getDisplay()).setHeader("Used");
        usedGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, concept) -> {
            anchor.setText(concept.getCodingFirstRep().getCode());
            anchor.setHref(concept.getCodingFirstRep().getSystem() + "/" + concept.getCodingFirstRep().getCode());
        })).setHeader("Code");
        usedGrid.setAllRowsVisible(true);
        Label followUpLabel = new Label("Follow-up: ");
        Label followUp = new Label(procedure.getFollowUpFirstRep().getText());
        Label note = new Label("Note: ");
        Label annotation = new Label(procedure.getNoteFirstRep().getText());
        add(layout);
        add(new HorizontalLayout(status, statusDisplay));
        add(new HorizontalLayout(issued, issuedValue));
        add(procedureGrid);
        add(new HorizontalLayout(label, reason));
        add(bodyGrid);
        add(complicationGrid);
        add(usedGrid);
        add(new HorizontalLayout(followUpLabel, followUp));
        add(new HorizontalLayout(note, annotation));
    }

}
