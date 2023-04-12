package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.service.PatientService;
import bsu.rpact.medionefrontend.service.medical.MedicationRequestService;
import bsu.rpact.medionefrontend.utils.CalculatorUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.helper.MedicationDivBuilder;
import com.mlottmann.vstepper.BinderContent;
import com.mlottmann.vstepper.StepContent;
import com.mlottmann.vstepper.VStepper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Route(value = "medicationPrescriptionOrigination", layout = MainLayout.class)
@PageTitle("New Medication Prescription")
public class MedicationPrescriptionOriginateView extends VerticalLayout {

    public static final String MH = "MH";
    public static final String МН_PREF = "МН-";
    private final MedicationDivBuilder medicationDivBuilder;
    private final PatientService patientService;
    private final MedicationRequestService medicationRequestService;

    public MedicationPrescriptionOriginateView(MedicationDivBuilder medicationDivBuilder, PatientService patientService, MedicationRequestService medicationRequestService) {
        this.medicationDivBuilder = medicationDivBuilder;
        this.patientService = patientService;
        this.medicationRequestService = medicationRequestService;
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        MedicationPrescriptionRq rq = new MedicationPrescriptionRq();
        VStepper wizard = new VStepper();
        wizard.setWidth("100%");
        wizard.setHeight("100%");
        wizard.addStep("Patient", createHeaderStepContent(rq));
        wizard.addStep("Medication", createMainStepContent(rq));
        wizard.addStep("Summary", new Label("Summary"));

        add(wizard);
    }

    private StepContent createHeaderStepContent(MedicationPrescriptionRq request) {
        Checkbox pref = new Checkbox();
        Binder<MedicationPrescriptionRq> binder = new Binder<>();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        Div patientLookupDiv = buildPatientLookupRow(request, binder);
        Div preferentialDiv = buildPreferentialDiv(request, pref, binder);
        Div serieNumberDiv = buildSerieNumberDiv(request, pref, binder);
        layout.add(patientLookupDiv, preferentialDiv);
        BinderContent<MedicationPrescriptionRq> content =
                new BinderContent<>(binder, layout,
                        serieNumberDiv);
        content.setWidth("100%");
        content.setHeight("100%");
        content.setValue(request);
        return content;
    }

    private StepContent createMainStepContent(MedicationPrescriptionRq request) {
        Binder<MedicationPrescriptionRq> binder = new Binder<>();
        binder.setBean(request);
        VerticalLayout verticalLayout = new VerticalLayout();
        Div medicationDiv = medicationDivBuilder.buildMedicationDiv(request, binder, verticalLayout);
        BinderContent<MedicationPrescriptionRq> content =
                new BinderContent<>(binder, medicationDiv, verticalLayout);
        content.setWidth("100%");
        content.setHeight("100%");
        content.setValue(request);
        return content;
    }

    private Div buildSerieNumberDiv(MedicationPrescriptionRq request, Checkbox pref, Binder<MedicationPrescriptionRq> binder) {
        TextField preambule = new TextField();
        preambule.setReadOnly(true);
        preambule.getElement().getStyle().set("width", "85px");
        preambule.setValue(MH);
        TextField serie = new TextField();
        Button randomizer = new Button();
        randomizer.setIcon(VaadinIcon.MAGIC.create());
        serie.setReadOnly(true);
        serie.getElement().getStyle().set("width", "85px");
        String currentYear = Integer.toString(LocalDate.now().getYear());
        serie.setValue(currentYear.substring(1, currentYear.length()));
        TextField number = new TextField();
        number.setPattern("[0-9]{7}");
        number.getElement().getStyle().set("height", "100px");
        randomizer.addClickListener(e -> {
            String uuid = String.format("%07d", new Random().nextInt(10000000));
            number.setValue(uuid.substring(0, 7));
        });
        number.addValueChangeListener(e -> {
            if (!medicationRequestService.isIdentifierUnique(preambule.getValue() + serie.getValue() + number.getValue())) {
                number.setInvalid(true);
                number.setErrorMessage("There is already a prescription with such identifier");
            }
            if (number.isInvalid()) {
                number.setPlaceholder("0000000");
                number.setErrorMessage("Number must contain exactly 7 digits");
            }
        });
        pref.addValueChangeListener(e -> {
            if (pref.getValue().booleanValue()) {
                preambule.setValue(МН_PREF);
            } else preambule.setValue(MH);
        });
        binder.forField(number).withValidator(new Validator<String>() {
            @Override
            public ValidationResult apply(String s, ValueContext valueContext) {
                return s.isEmpty() ? ValidationResult.error("Please capture serie and number") : ValidationResult.ok();
            }
        }).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                if (medicationPrescriptionRq.getSerieNum() == null) return "";
                String s = medicationPrescriptionRq.getSerieNum();
                return s.substring(s.length() - 8, s.length() - 1);
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationPrescriptionRq.setSerieNum(buildSerieNumString(preambule, serie, number));
            }
        });
        return createRow("Serie and number  ", preambule, serie, number, randomizer);
    }

    private String buildSerieNumString(TextField preambule, TextField serie, TextField number) {
        return preambule.getValue() + serie.getValue() + number.getValue();
    }

    private Div buildPreferentialDiv(MedicationPrescriptionRq request, Checkbox pref, Binder<MedicationPrescriptionRq> binder) {
        Div div = createRow("Preferential prescription", pref);
        div.getElement().getStyle().set("flex-grow", "1");
        div.getElement().getStyle().set("width", "50%");
        binder.forField(pref).bind(MedicationPrescriptionRq::getPreferential, MedicationPrescriptionRq::setPreferential);
        return div;
    }

    public Div buildPatientLookupRow(MedicationPrescriptionRq request, Binder<MedicationPrescriptionRq> binder) {
        AtomicReference<Patient> patientAtomicReference = new AtomicReference<>();
        TextField patientField = new TextField();
        Button lookup = new Button();
        Icon icon = new Icon(VaadinIcon.CHECK);
        icon.setColor("green");
        icon.getElement().getStyle().set("width", "100px");
        icon.setVisible(false);
        lookup.setIcon(VaadinIcon.SEARCH.create());
        lookup.addClickListener(e -> showPatientLookupDialog(patientField.getValue(), new Consumer<Patient>() {
            @Override
            public void accept(Patient patient) {
                if (patient != null) {
                    patientAtomicReference.set(patient);
                    patientField.setValue(patient.getCredentials().getFirstName()
                            + " "
                            + patient.getCredentials().getPatronymic()
                            + " "
                            + patient.getCredentials().getLastName());
                    icon.setVisible(true);
                    patientField.setReadOnly(true);
                    lookup.setEnabled(false);
                }
            }
        }));
        icon.addClickListener(e -> {
            icon.setVisible(false);
            patientField.clear();
            patientField.setReadOnly(false);
            lookup.setEnabled(true);
        });
        patientField.setErrorMessage("Please capture a patient");
        lookup.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        binder.forField(patientField)
                .withValidator(new Validator<String>() {
                    @Override
                    public ValidationResult apply(String s, ValueContext valueContext) {
                        return s.isEmpty() ? ValidationResult.error("Patient must be captured") : ValidationResult.ok();
                    }
                }).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
                    @Override
                    public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                        if (medicationPrescriptionRq.getPatient() == null) return "";
                        Patient patient = medicationPrescriptionRq.getPatient();
                        return patient.getCredentials().getFirstName()
                                + " "
                                + patient.getCredentials().getPatronymic()
                                + " "
                                + patient.getCredentials().getLastName();
                    }
                }, new Setter<MedicationPrescriptionRq, String>() {
                    @Override
                    public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                        medicationPrescriptionRq.setPatient(patientAtomicReference.get());
                    }
                });
        return createRow("Patient   ", patientField, lookup, icon);
    }

    public void showPatientLookupDialog(String searchTerm, Consumer<Patient> selectionHandler) {
        Dialog dialog = new Dialog();
        dialog.setWidth("100%");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        Grid<Patient> grid = new Grid<>(Patient.class);
        grid.setColumnReorderingAllowed(false);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.removeAllColumns();
        grid.setItems(patientService.findBySearchTerm(searchTerm));
        grid.addColumn(patient -> {
            return patient.getCredentials().getFirstName();
        }).setKey("firstName").setHeader("First name");
        grid.addColumn(patient -> {
            return patient.getCredentials().getPatronymic();
        }).setKey("patronymic").setHeader("Patronymic");
        grid.addColumn(patient -> {
            return patient.getCredentials().getLastName();
        }).setKey("lastName").setHeader("Last name");
        grid.addColumn(patient -> {
            return CalculatorUtils.getAge(patient.getCredentials().getBirthDate());
        }).setKey("age").setHeader("Age");
        grid.addColumn(patient -> {
            return patient.getMedcard() != null ? patient.getMedcard().getResidentalAddress() : "";
        }).setKey("residentalAddress").setHeader("Residental address");
        grid.addColumn(patient -> {
            return patient.getCredentials().getPhone();
        }).setKey("phone").setHeader("Phone");
        grid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent()) {
                selectionHandler.accept(event.getFirstSelectedItem().get());
                dialog.close();
            }
        });
        dialog.add(grid);
        dialog.open();
    }

    private Div createRow(String caption, Component... input) {
        Div row = new Div();
        row.add(new Label(caption));
        for (Component comp : input) {
            row.add(comp);
        }
        row.addClassName("row");
        return row;
    }
}
