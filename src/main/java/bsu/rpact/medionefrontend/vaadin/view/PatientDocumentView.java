package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.entity.medical.DiagnosticReportContainer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private ListContentPanel listContentPanel;
    private TextField searchField;
    private List<RippleClickableCard> cardList;
    private final Checkbox observations;
    private final Checkbox reports;
    private final Checkbox procedures;
    private final Button searchButton;
    private DatePicker datePicker;
    private List<Observation> displayableObservationList;
    private Map<DiagnosticReport, Observation> displayableDiagnosticReportMap;
    private List<Procedure> displayableProcedureList;
    private List<Observation> storedObservationList;
    private Map<DiagnosticReport, Observation> storedDiagnosticReportMap;
    private List<Procedure> storedProcedureList;
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
        storedObservationList = observationService.search(Patient.class, patient.getId());
        storedDiagnosticReportMap = diagnosticReportService.searchIncluded(Patient.class, patient.getId());
        storedProcedureList = procedureService.search(Patient.class, patient.getId());
        displayableObservationList = new ArrayList<>();
        displayableProcedureList = new ArrayList<>();
        displayableDiagnosticReportMap = new HashMap<DiagnosticReport,Observation>();
        displayableObservationList.addAll(storedObservationList);
        displayableProcedureList.addAll(storedProcedureList);
        displayableDiagnosticReportMap.putAll(storedDiagnosticReportMap);
        datePicker = new DatePicker();
        searchField = new TextField();
        datePicker.setLabel("Issued at");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchField.setPlaceholder("Search criteria");
        listContentPanel.add(searchField, observations, reports, procedures, datePicker, searchButton);
        setupMainArea();
    }

    private void setupMainArea() {
        cardList = getAllCards();
        totalPages = (int) Math.ceil((double) cardList.size() / itemsPerPage);
        pagingLayout = setupPagingLayout();
        populateCurrentCards(cardList);
        listContentPanel.add(pagingLayout);
        add(listContentPanel);
    }

    private void setupSearch() {
        String criteria = searchField.getValue();
        if (observations.getValue()){
            displayableObservationList = doSearchObservation(criteria);
        }
        if (procedures.getValue()){
            displayableProcedureList = doProcedureSearch(criteria);
        }
        if (reports.getValue()){
            displayableDiagnosticReportMap = doReportSearch(criteria);
        }
        setupMainArea();
    }

    private Map<DiagnosticReport, Observation> doReportSearch(String criteria) {
        return storedDiagnosticReportMap.entrySet()
                .stream()
                .filter(map ->{
                    String display = rippleCardFactory.getDisplayString(map.getKey().getIdentifier(), map.getKey().getCode());
                    Pattern pattern = Pattern.compile(criteria, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(display);
                    return matcher.find();
                })
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private List<Procedure> doProcedureSearch(String criteria) {
        return storedProcedureList.stream().filter(procedure -> {
            String display = rippleCardFactory.getDisplayString(procedure.getIdentifier(), procedure.getCode());
            Pattern pattern = Pattern.compile(criteria, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(display);
            return matcher.find();
        }).collect(Collectors.toList());
    }

    private List<Observation> doSearchObservation(String criteria) {
        return storedObservationList.stream().filter(observation -> {
            String display = rippleCardFactory.getDisplayString(observation.getIdentifier(), observation.getCode());
            Pattern pattern = Pattern.compile(criteria, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(display);
            return matcher.find();
        }).collect(Collectors.toList());
    }

    private void populateCurrentCards(List cardList) {
        listContentPanel.removeAllCards();
        listContentPanel.removeAllHorizontalLayouts();
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
            DiagnosticReportContainer container = new DiagnosticReportContainer();
            container.setReport(diagnosticReport);
            List<Observation> properObservations = new ArrayList<>();
            displayableDiagnosticReportMap.forEach((key, value) -> {
                if(key.equals(diagnosticReport)){
                    properObservations.add(value);
                }
            });
            container.setObservationList(properObservations);
            return rippleCardFactory.getDiagnosticReportCard(container);
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
            observationService.search(Patient.class, patient.getId());
            displayableObservationList.stream().forEach(item -> {
                if (item!=null && item.getIssued()!=null)
                domainResources.put(item.getIssued(), item);
            });
        }
        if(reports.getValue()){
            displayableDiagnosticReportMap.keySet().stream().forEach(item -> {
                if (item!=null && item.getIssued()!=null)
                    domainResources.put(item.getIssued(), item);
            });
        }
        if(procedures.getValue()){
            displayableProcedureList.stream().forEach(item -> {
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
                populateCurrentCards(cardList);
            }
        });
        Button buttonDoubleLeft = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        buttonDoubleLeft.addClickListener(e -> {
            if (currentPage > 1) {
                currentPage = 1;
                populateCurrentCards(cardList);
            }
        });
        Button buttonRight = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        buttonRight.addClickListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                populateCurrentCards(cardList);
            }
        });
        Button buttonDoubleRight = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        buttonDoubleRight.addClickListener(e -> {
            if (currentPage < totalPages) {
                currentPage = totalPages;
                populateCurrentCards(cardList);
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