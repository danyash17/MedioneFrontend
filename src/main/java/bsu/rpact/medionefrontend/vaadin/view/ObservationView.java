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
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.vaadin.addons.badge.Badge;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Route(value = "observation", layout = MainLayout.class)
@PageTitle("Observation")
public class ObservationView extends VerticalLayout {

    private String PATTERN = "yyyy-MM-dd";
    private SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);

    private FhirCashingContainer container;

    public ObservationView(FhirCashingContainer container) {
        this.container = container;
        Observation observation = container.getObservation();
        doInit(observation);
    }

    protected void doInit(Observation observation) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);
        Optional<Identifier> frontendId = observation.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        H3 header = new H3("Observation " + (frontendId.isPresent() ? "[" + frontendId.get().getValue() + "]" : "UNDEFINED"));
        layout.add(header);
        Label status = new Label("Status: ");
        Label statusDisplay = new Label(observation.getStatus().getDisplay());
        Grid<Coding> codingGrid = new Grid<>();
        codingGrid.setItems(observation.getCode().getCoding());
        codingGrid.addColumn(Coding::getDisplay).setHeader("Measurement");
        codingGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, coding) -> {
            anchor.setText(coding.getCode());
            anchor.setHref(coding.getSystem() + "/" + coding.getCode());
        })).setHeader("Code");
        codingGrid.setAllRowsVisible(true);
        Label value = new Label("Value: ");
        Label quantity = new Label(observation.getValueQuantity().getValue().toPlainString());
        quantity.getElement().getStyle().set("fontWeight", "bold");
        Anchor anchor = new Anchor();
        anchor.setText(observation.getValueQuantity().getUnit());
        anchor.setHref(observation.getValueQuantity().getSystem() + "/" + observation.getValueQuantity().getCode());
        add(layout);
        add(new HorizontalLayout(status, statusDisplay));
        add(codingGrid);
        add(new HorizontalLayout(value, quantity, anchor));
        //TODO: RESOLVE MULTIPLE ENTRIES NOT FIRST REP
        if (observation.getInterpretationFirstRep() != null) {
            Label interpretation = new Label("Interpretation: ");
            Badge badge = FhirBadgeInterpretator.interpret(observation.getInterpretationFirstRep().getCodingFirstRep().getCode(),
                    observation.getInterpretationFirstRep().getCodingFirstRep().getDisplay());
            Grid<Observation.ObservationReferenceRangeComponent> referenceGrid = new Grid<>();
            referenceGrid.setItems(observation.getReferenceRange());
            referenceGrid.addColumn(component -> component.getLow().getValue()).setHeader("Low");
            referenceGrid.addColumn(component -> component.getHigh().getValue()).setHeader("High");
            referenceGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anch, referenceRange) -> {
                anch.setText(referenceRange.getHigh().getUnit());
                anch.setHref(referenceRange.getHigh().getSystem() + "/" + referenceRange.getHigh().getCode());
            })).setHeader("Unit");
            referenceGrid.setAllRowsVisible(true);
            add(new HorizontalLayout(interpretation, badge));
            add(referenceGrid);
        }
        if (observation.getIssued() != null) {
            Label issued = new Label("Issued: ");
            Label issuedValue = new Label(SIMPLE_DATE_FORMAT.format(observation.getIssued()));
            add(new HorizontalLayout(issued, issuedValue));
        }
        if (observation.getEffectivePeriod().getStart() != null && observation.getEffectivePeriod().getEnd() != null) {
            Label effective = new Label("Effective Period: ");
            Label effectiveFrom = new Label(SIMPLE_DATE_FORMAT.format(observation.getEffectivePeriod().getStart()));
            Label effectiveTo = new Label(SIMPLE_DATE_FORMAT.format(observation.getEffectivePeriod().getEnd()));
            add(new HorizontalLayout(effective, effectiveFrom, new Label("—"), effectiveTo));
        }
    }
}
