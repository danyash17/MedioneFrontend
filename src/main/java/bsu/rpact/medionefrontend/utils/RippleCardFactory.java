package bsu.rpact.medionefrontend.utils;

import bsu.rpact.medionefrontend.pojo.medical.DiagnosticReportContainer;
import bsu.rpact.medionefrontend.enums.FhirId;
import bsu.rpact.medionefrontend.session.FhirCashingContainer;
import bsu.rpact.medionefrontend.vaadin.view.DiagnosticReportView;
import bsu.rpact.medionefrontend.vaadin.view.MedicationRequestView;
import bsu.rpact.medionefrontend.vaadin.view.ObservationView;
import bsu.rpact.medionefrontend.vaadin.view.ProcedureView;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.IconItem;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RippleCardFactory {

    @Autowired
    private FhirCashingContainer fhirCashingContainer;
    @Autowired
    private ImageUtils imageUtils;

    public RippleClickableCard getObservationCard(Observation observation){
        Image img = imageUtils.getImageByDocumentType(observation.getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        String display = getDisplayString(observation.getIdentifier(), observation.getCode());
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(observation),
                new IconItem(img, display, observation.getIssued() != null ? observation.getIssued().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
    }

    public RippleClickableCard getDiagnosticReportCard(DiagnosticReportContainer container){
        Image img = imageUtils.getImageByDocumentType(container.getReport().getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        String display = getDisplayString(container.getReport().getIdentifier(), container.getReport().getCode());
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(container),
                new IconItem(img, display, container.getReport().getIssued() != null ? container.getReport().getIssued().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
    }

    public RippleClickableCard getProcedureCard(Procedure procedure){
        Image img = imageUtils.getImageByDocumentType(procedure.getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        String display = getDisplayString(procedure.getIdentifier(), procedure.getCode());
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(procedure),
                new IconItem(img, display, procedure.getPerformedDateTimeType().getValue().toString() != null ? procedure.getPerformedDateTimeType().getValue().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
    }

    public RippleClickableCard getMedicationRequestCard(MedicationRequest medicationRequest) {
        Image img = imageUtils.getImageByDocumentType(medicationRequest.getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        String display = medicationRequest.getIdentifier().stream().filter(id -> id.getPeriod()!=null).findFirst().get().getValue();
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(medicationRequest),
                new IconItem(img, display, medicationRequest.getAuthoredOn() != null ? medicationRequest.getAuthoredOn().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
    }

    public String getDisplayString(List<Identifier> id, CodeableConcept code) {
        Optional<Identifier> frontendId = id.stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        String coding = code.getCoding().get(0).getDisplay();
        String display = frontendId.isPresent() ? coding + " [" + frontendId.get().getValue() + "]" : coding + " UNDEFINED";
        return display;
    }

    private ComponentEventListener getComponentEventListener(DomainResource domainResource) {
        return componentEvent -> {
            switch (domainResource.getClass().getSimpleName()){
                case "Observation":{
                    Observation observation = (Observation) domainResource;
                    fhirCashingContainer.setObservation(observation);
                    UI.getCurrent().navigate(ObservationView.class);
                    break;
                }
                case "DiagnosticReportContainer":{
                    DiagnosticReportContainer report = (DiagnosticReportContainer) domainResource;
                    fhirCashingContainer.setReportContainer(report);
                    UI.getCurrent().navigate(DiagnosticReportView.class);
                    break;
                }
                case "Procedure":{
                    Procedure procedure = (Procedure) domainResource;
                    fhirCashingContainer.setProcedure(procedure);
                    UI.getCurrent().navigate(ProcedureView.class);
                    break;
                }
                case "MedicationRequest":{
                    MedicationRequest medicationRequest = (MedicationRequest) domainResource;
                    fhirCashingContainer.setMedicationRequest(medicationRequest);
                    UI.getCurrent().navigate(MedicationRequestView.class);
                    break;
                }
            }
        };
    }
}
