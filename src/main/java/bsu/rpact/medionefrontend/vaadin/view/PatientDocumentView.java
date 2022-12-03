package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.enums.FhirId;
import bsu.rpact.medionefrontend.service.PatientService;
import bsu.rpact.medionefrontend.service.medical.ObservationService;
import bsu.rpact.medionefrontend.service.medical.ProcedureService;
import bsu.rpact.medionefrontend.session.FhirCashingContainer;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.components.ListContentPanel;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.IconItem;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.hl7.fhir.r4.model.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Route(value = "documents", layout = MainLayout.class)
@PageTitle("Documents")
public class PatientDocumentView extends VerticalLayout {

    private final ObservationService observationService;
    private final ProcedureService procedureService;
    private final PatientService patientService;
    private final SessionManager sessionManager;
    private final FhirCashingContainer fhirCashingContainer;
    private final ImageUtils imageUtils;

    private final ListContentPanel listContentPanel;
    private final TextField searchField;
    private final List<RippleClickableCard> cardList;
    private final Checkbox observations;
    private final Checkbox reports;
    private final Checkbox procedures;
    private final Button searchButton;
    private final DatePicker datePicker;
    private List<Observation> observationList;
    private List<DiagnosticReport> diagnosticReportList;
    private List<Procedure> procedureList;
    private Patient patient;
    private HorizontalLayout pagingLayout;
    private Integer itemsPerPage = 7;
    private Integer currentPage = 1;
    private Integer totalPages;
    private Label currentNumber = new Label();

    public PatientDocumentView(ObservationService observationService, ProcedureService procedureService, PatientService patientService, SessionManager sessionManager, FhirCashingContainer fhirCashingContainer, ImageUtils imageUtils) {
        this.observationService = observationService;
        this.procedureService = procedureService;
        this.patientService = patientService;
        this.sessionManager = sessionManager;
        this.fhirCashingContainer = fhirCashingContainer;
        this.imageUtils = imageUtils;
        Optional<Patient> optionalPatient = patientService.getSelf();
        if (optionalPatient.isPresent()) {
            patient = optionalPatient.get();
        }
        setDefaultHorizontalComponentAlignment(Alignment.START);
        listContentPanel = new ListContentPanel();
        observations = new Checkbox();
        observations.setLabel("Observations");
        observations.setValue(true);
        reports = new Checkbox();
        reports.setLabel("Diagnostic reports");
        procedures = new Checkbox();
        procedures.setLabel("Procedures");
        procedures.setValue(true);
        searchButton = new Button("Search");
        searchButton.addClickListener(e -> {
            setupSearch();
        });
        searchField = new TextField();
        searchField.setPlaceholder("Search criteria");
        cardList = getAllCards();
        totalPages = (int) Math.ceil((double) cardList.size() / itemsPerPage);
        pagingLayout = setupPagingLayout();
        datePicker = new DatePicker();
        datePicker.setLabel("Issued at");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        listContentPanel.add(searchField, observations, reports, procedures, datePicker, searchButton);
        populateCurrentCards(listContentPanel, cardList);
        listContentPanel.add(pagingLayout);
        add(listContentPanel);

    }

    private void setupSearch() {

    }

    private void populateCurrentCards(ListContentPanel listContentPanel, List cardList) {
        listContentPanel.removeAsList(cardList);
        listContentPanel.remove(pagingLayout);
        boolean firstPage = currentPage == 1;
        boolean lastPage = currentPage == totalPages;
        if (firstPage) {
            boolean notFull = cardList.size() < itemsPerPage;
            listContentPanel.addAsList(cardList.subList(0, notFull ? cardList.size() : itemsPerPage));
        } else if (lastPage) {
            listContentPanel.addAsList(cardList.subList(itemsPerPage * (currentPage - 1), cardList.size()));
        } else {
            listContentPanel.addAsList(cardList.subList(itemsPerPage * (currentPage - 1), itemsPerPage * currentPage));
        }
        listContentPanel.add(pagingLayout);
        if (!lastPage) {
            pagingLayout.setAlignItems(Alignment.CENTER);
        } else {
            pagingLayout.setAlignItems(Alignment.END);
        }
        currentNumber.setText(currentPage.toString());
    }

    private List<RippleClickableCard> getAllCards() {
        List<RippleClickableCard> cardList = new LinkedList();
        List<DomainResource> domainResources = sortDomainResourcesToList(buildDomainResourcesMap());
        for (DomainResource resource : domainResources){
            cardList.add(getProperCard(resource));
        }
        return cardList;
    }

    private RippleClickableCard getProperCard(DomainResource resource) {
        if (resource instanceof Observation){
            Observation observation = (Observation) resource;
            return getObservationCard(observation);
        }
        if (resource instanceof DiagnosticReport){
            DiagnosticReport diagnosticReport = (DiagnosticReport) resource;
            return getDiagnosticReportCard(diagnosticReport);
        }
        if (resource instanceof Procedure){
            Procedure procedure = (Procedure) resource;
            return getProcedureCard(procedure);
        }
        return null;
    }

    private Map<Date, DomainResource> buildDomainResourcesMap() {
        Map<Date, DomainResource> domainResources = new HashMap<>();
        if(observations.getValue()){
            List<Observation> observationList = observationService.search(Patient.class, patient.getId());
            observationList.stream().forEach(item -> {
                if (item!=null && item.getIssued()!=null)
                domainResources.put(item.getIssued(), item);
            });
        }
        if(reports.getValue()){

        }
        if(procedures.getValue()){
            List<Procedure> procedureList = procedureService.search(Patient.class, patient.getId());
            procedureList.stream().forEach(item -> {
                if (item!=null && item.getPerformedDateTimeType()!=null)
                    domainResources.put(item.getPerformedDateTimeType().getValue(), item);
            });
        }
        return domainResources;
    }

    private List<DomainResource> sortDomainResourcesToList(Map<Date,DomainResource> map){
        Map<Date,DomainResource> sortedMap = new TreeMap<>(map);
        return new ArrayList<>(sortedMap.values());
    }

    private RippleClickableCard getObservationCard(Observation observation){
        Image img = imageUtils.getImageByDocumentType(observation.getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        Optional<Identifier> frontendId = observation.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        String coding = observation.getCode().getCoding().get(0).getDisplay();
        String display = frontendId.isPresent() ? coding + " [" + frontendId.get().getValue() + "]" : coding + " UNDEFINED";
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(observation),
                new IconItem(img, display, observation.getIssued() != null ? observation.getIssued().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
    }

    private RippleClickableCard getDiagnosticReportCard(DiagnosticReport diagnosticReport){
        Image img = imageUtils.getImageByDocumentType(diagnosticReport.getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        Optional<Identifier> frontendId = diagnosticReport.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        String coding = diagnosticReport.getCode().getCoding().get(0).getDisplay();
        String display = frontendId.isPresent() ? coding + " [" + frontendId.get().getValue() + "]" : coding + " UNDEFINED";
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(diagnosticReport),
                new IconItem(img, display, diagnosticReport.getIssued() != null ? diagnosticReport.getIssued().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
    }

    private RippleClickableCard getProcedureCard(Procedure procedure){
        Image img = imageUtils.getImageByDocumentType(procedure.getResourceType().toString());
        img.setWidth("40px");
        img.setHeight("40px");
        Optional<Identifier> frontendId = procedure.getIdentifier().stream().filter(item -> item.getSystem().equals(FhirId.Frontend.name())).findFirst();
        String coding = procedure.getCode().getCoding().get(0).getDisplay();
        String display = frontendId.isPresent() ? coding + " [" + frontendId.get().getValue() + "]" : coding + " UNDEFINED";
        RippleClickableCard card = new RippleClickableCard(
                getComponentEventListener(procedure),
                new IconItem(img, display, procedure.getPerformedDateTimeType().getValue().toString() != null ? procedure.getPerformedDateTimeType().getValue().toString() : null)
        );
        card.setWidthFull();
        card.setHeight("100px");
        return card;
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
                case "DiagnosticReport":{
                    DiagnosticReport report = (DiagnosticReport) domainResource;
                    fhirCashingContainer.setReport(report);
                    UI.getCurrent().navigate(DiagnosticReportView.class);
                    break;
                }
                case "Procedure":{
                    Procedure procedure = (Procedure) domainResource;
                    fhirCashingContainer.setProcedure(procedure);
                    UI.getCurrent().navigate(ProcedureView.class);
                    break;
                }
            }
        };
    }

    private HorizontalLayout setupPagingLayout() {
        HorizontalLayout pagingLayout = new HorizontalLayout();
        pagingLayout.setAlignItems(Alignment.CENTER);
        pagingLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Button buttonLeft = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        buttonLeft.addClickListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Button buttonDoubleLeft = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        buttonDoubleLeft.addClickListener(e -> {
            if (currentPage > 1) {
                currentPage = 1;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Button buttonRight = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        buttonRight.addClickListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Button buttonDoubleRight = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        buttonDoubleRight.addClickListener(e -> {
            if (currentPage < totalPages) {
                currentPage = totalPages;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Label page = new Label("Page ");
        currentNumber.setText(currentPage.toString());
        Label of = new Label("of");
        Label totalNumber = new Label(totalPages.toString());
        HorizontalLayout labelLayout = new HorizontalLayout(page, currentNumber, of, totalNumber);
        labelLayout.setAlignItems(Alignment.CENTER);
        labelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        labelLayout.setHeight("14.5%");
        pagingLayout.add(buttonDoubleLeft, buttonLeft, labelLayout, buttonRight, buttonDoubleRight);
        return pagingLayout;
    }


}