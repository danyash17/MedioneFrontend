package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.service.PatientService;
import bsu.rpact.medionefrontend.service.medical.DiagnosticReportService;
import bsu.rpact.medionefrontend.service.medical.ObservationService;
import bsu.rpact.medionefrontend.service.medical.ProcedureService;
import bsu.rpact.medionefrontend.session.FhirCashingContainer;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.utils.RippleCardFactory;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.components.ListContentPanel;
import com.github.appreciated.card.RippleClickableCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hl7.fhir.r4.model.*;

import java.util.*;

@Route(value = "documents", layout = MainLayout.class)
@PageTitle("Documents")
public class PatientDocumentView extends VerticalLayout {

    private final ObservationService observationService;
    private final ProcedureService procedureService;
    private final DiagnosticReportService diagnosticReportService;
    private final PatientService patientService;
    private final SessionManager sessionManager;
    private final FhirCashingContainer fhirCashingContainer;
    private final ImageUtils imageUtils;
    private final RippleCardFactory rippleCardFactory;

    private final ListContentPanel listContentPanel;
    private final TextField searchField;
    private final List<RippleClickableCard> cardList;
    private final Checkbox observations;
    private final Checkbox reports;
    private final Checkbox procedures;
    private final Button searchButton;
    private final DatePicker datePicker;
    private List<Observation> observationList;
    private Map<DiagnosticReport, Observation> diagnosticReportMap;
    private List<Procedure> procedureList;
    private Patient patient;
    private HorizontalLayout pagingLayout;
    private Integer itemsPerPage = 7;
    private Integer currentPage = 1;
    private Integer totalPages;
    private Label currentNumber = new Label();

    public PatientDocumentView(ObservationService observationService, ProcedureService procedureService, DiagnosticReportService diagnosticReportService, PatientService patientService, SessionManager sessionManager, FhirCashingContainer fhirCashingContainer, ImageUtils imageUtils, RippleCardFactory rippleCardFactory) {
        this.observationService = observationService;
        this.procedureService = procedureService;
        this.diagnosticReportService = diagnosticReportService;
        this.patientService = patientService;
        this.sessionManager = sessionManager;
        this.fhirCashingContainer = fhirCashingContainer;
        this.imageUtils = imageUtils;
        this.rippleCardFactory = rippleCardFactory;
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
        reports.setValue(true);
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
            return rippleCardFactory.getObservationCard(observation);
        }
        if (resource instanceof DiagnosticReport){
            DiagnosticReport diagnosticReport = (DiagnosticReport) resource;
            return rippleCardFactory.getDiagnosticReportCard(diagnosticReport);
        }
        if (resource instanceof Procedure){
            Procedure procedure = (Procedure) resource;
            return rippleCardFactory.getProcedureCard(procedure);
        }
        return null;
    }

    private Map<Date, DomainResource> buildDomainResourcesMap() {
        Map<Date, DomainResource> domainResources = new HashMap<>();
        if(observations.getValue()){
            observationList = observationService.search(Patient.class, patient.getId());
            observationList.stream().forEach(item -> {
                if (item!=null && item.getIssued()!=null)
                domainResources.put(item.getIssued(), item);
            });
        }
        if(reports.getValue()){
            diagnosticReportMap = diagnosticReportService.searchIncluded(Patient.class, patient.getId());
            diagnosticReportMap.keySet().stream().forEach(item -> {
                if (item!=null && item.getIssued()!=null)
                    domainResources.put(item.getIssued(), item);
            });
        }
        if(procedures.getValue()){
            procedureList = procedureService.search(Patient.class, patient.getId());
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