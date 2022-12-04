package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.enums.FhirId;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.DoctorSpecialityService;
import bsu.rpact.medionefrontend.session.FhirCashingContainer;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.vaadin.components.DoctorDetails;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.components.ObservationViewComp;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hl7.fhir.r4.model.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Route(value = "report", layout = MainLayout.class)
@PageTitle("Diagnostic Report")
public class DiagnosticReportView extends VerticalLayout {

    private String PATTERN = "yyyy-MM-dd";
    private SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);

    private final DoctorSpecialityService doctorSpecialityService;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;
    private final FhirCashingContainer container;

    public DiagnosticReportView(DoctorSpecialityService doctorSpecialityService, DoctorService doctorService, ImageUtils imageUtils, FhirCashingContainer container) {
        this.doctorSpecialityService = doctorSpecialityService;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        this.container = container;
        DiagnosticReport report = container.getReportContainer().getReport();
        List<Observation> observationList = container.getReportContainer().getObservationList();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);
        Optional<Identifier> frontendId = report.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        H3 header = new H3("Diagnostic report " + (frontendId.isPresent() ? "[" + frontendId.get().getValue() + "]" : "UNDEFINED"));
        layout.add(header);
        Label status = new Label("Status: ");
        Label statusDisplay = new Label(report.getStatus().getDisplay());
        Label issued = new Label("Issued: ");
        Label issuedValue = new Label(SIMPLE_DATE_FORMAT.format(report.getIssued()));
        Optional<Identifier> doctorId = report.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Doctor.name())).findFirst();
        Doctor doctor = doctorService.getDoctorById(Integer.valueOf(doctorId.get().getValue()));
        VerticalLayout doctorLayout = new DoctorDetails(this.doctorSpecialityService, this.imageUtils, doctor);
        Details details = new Details(doctor.getCredentials().getFirstName() + " " +
                doctor.getCredentials().getPatronymic() + " " +
                doctor.getCredentials().getLastName(), doctorLayout);
        Grid<Coding> codingGrid = new Grid<>();
        codingGrid.setItems(report.getCode().getCoding());
        codingGrid.addColumn(Coding::getDisplay).setHeader("Report name");
        codingGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, coding) -> {
            anchor.setText(coding.getCode());
            anchor.setHref(coding.getSystem() + "/" + coding.getCode());
        })).setHeader("Code");
        codingGrid.setAllRowsVisible(true);
        Grid<Observation> refGrid = new Grid<>();
        refGrid.setItems(container.getReportContainer().getObservationList());
        refGrid.addComponentColumn(observation -> {
            Details dtls = new Details();
            dtls = new Details(observation.getCode().getCodingFirstRep().getDisplay(), new ObservationViewComp(observation));
            return dtls;
        }).setHeader("Observation");
        refGrid.setAllRowsVisible(true);
        H4 conclusion = new H4("Conclusion");
        Grid<CodeableConcept> conclusionGrid = new Grid<>();
        conclusionGrid.setItems(report.getConclusionCode());
        conclusionGrid.addColumn(CodeableConcept::getText).setHeader("Classificator");
        conclusionGrid.addComponentColumn(codeableConcept -> {
            Label label = new Label(codeableConcept.getCodingFirstRep().getDisplay());
            label.getElement().getStyle().set("fontWeight", "bold");
            return label;
        }).setHeader("Conclusion");
        conclusionGrid.addColumn(new ComponentRenderer<>(Anchor::new, (anchor, codeableConcept) -> {
            anchor.setText(codeableConcept.getCodingFirstRep().getCode());
            anchor.setHref(codeableConcept.getCodingFirstRep().getSystem() + "/" + codeableConcept.getCodingFirstRep().getCode());
        })).setHeader("Code");
        conclusionGrid.setAllRowsVisible(true);
        add(layout);
        add(new HorizontalLayout(status, statusDisplay));
        add(new HorizontalLayout(issued, issuedValue));
        add(new HorizontalLayout(new Label("Issuer: "), details));
        add(codingGrid);
        add(refGrid);
        add(conclusion);
        add(conclusionGrid);
    }
}
