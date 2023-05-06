package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.PatientService;
import bsu.rpact.medionefrontend.service.medical.MedicationRequestService;
import bsu.rpact.medionefrontend.utils.CalculatorUtils;
import bsu.rpact.medionefrontend.utils.mapper.FhirMedicationRequestMapper;
import bsu.rpact.medionefrontend.utils.pdf.MedicalForm01Mapper;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.helper.MedicationDivBuilder;
import com.mlottmann.vstepper.BinderContent;
import com.mlottmann.vstepper.Step;
import com.mlottmann.vstepper.StepContent;
import com.mlottmann.vstepper.VStepper;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route(value = "medicationPrescriptionOrigination", layout = MainLayout.class)
@PageTitle("New Medication Prescription")
public class MedicationPrescriptionOriginateView extends VerticalLayout {

    public static final String MH = "MH";
    public static final String МН_PREF = "МН-";
    public static final String PATIENT = "Patient";
    public static final String MEDICATION = "Medication";
    public static final String SUMMARY = "Summary";
    private final MedicationDivBuilder medicationDivBuilder;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MedicationRequestService medicationRequestService;

    public MedicationPrescriptionOriginateView(MedicationDivBuilder medicationDivBuilder, PatientService patientService, DoctorService doctorService, MedicationRequestService medicationRequestService) {
        this.medicationDivBuilder = medicationDivBuilder;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.medicationRequestService = medicationRequestService;
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        MedicationPrescriptionRq rq = new MedicationPrescriptionRq();
        rq.setDoctor(doctorService.getDoctorSelf());
        PdfViewer pdfViewer = new PdfViewer();
        VStepper wizard = new VStepper();
        wizard.setWidth("100%");
        wizard.setHeight("100%");
        wizard.addStep(PATIENT, createHeaderStepContent(rq));
        wizard.addStep(MEDICATION, createMainStepContent(rq));
        wizard.addStep(SUMMARY, createSummaryStepContent(rq, pdfViewer));

        List<Step> steps = getSteps(wizard);
        steps.get(1).addCompleteListener(e -> {
            try {
                pdfViewer.setSrc(fileToStreamResource(new MedicalForm01Mapper().map(rq)));
                new FhirMedicationRequestMapper().map(rq);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        add(wizard);
    }

    public StreamResource fileToStreamResource(File file) throws FileNotFoundException {
        StreamResource streamResource = new StreamResource(file.getName(),
                () -> {
                    try {
                        return new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
        return streamResource;
    }

    private List<Step> getSteps(VStepper wizard) {
        Field steps = null;
        try {
            steps = wizard.getClass().getDeclaredField("steps");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        steps.setAccessible(true);
        List<Step> stepList = null;
        try {
            stepList = (List<Step>) steps.get(wizard);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return stepList;
    }

    private Component createSummaryStepContent(MedicationPrescriptionRq rq, PdfViewer pdfViewer) {
        Binder<MedicationPrescriptionRq> binder = new Binder<>();
        BinderContent<MedicationPrescriptionRq> content = new BinderContent<>(binder,pdfViewer);
        rq.setAuthoredOn(LocalDate.now());
        pdfViewer.setAddPrintButton(true);
        content.setValue(rq);
        content.setWidth("100%");
        content.setHeight("100%");
        return content;
    }

    private StepContent createHeaderStepContent(MedicationPrescriptionRq request) {
        Checkbox pref = new Checkbox();
        Binder<MedicationPrescriptionRq> binder = new Binder<>();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        Div patientLookupDiv = buildPatientLookupRow(request, binder);
        Div preferentialDiv = buildPreferentialDiv(request, pref, binder);
        Div serieNumberDiv = buildSerieNumberDiv(request, pref, binder);
        Div validityTimeDiv = buildValidityTimeDiv(request, binder);
        Div activeAfterDiv = buildActiveAfterDiv(request, binder);
        layout.add(patientLookupDiv, preferentialDiv);
        BinderContent<MedicationPrescriptionRq> content =
                new BinderContent<>(binder, layout,
                        serieNumberDiv, validityTimeDiv, activeAfterDiv);
        content.setWidth("100%");
        content.setHeight("100%");
        content.setValue(request);
        return content;
    }

    private Div buildActiveAfterDiv(MedicationPrescriptionRq request, Binder<MedicationPrescriptionRq> binder) {
        DatePicker datePicker = new DatePicker();
        binder.forField(datePicker).withValidator(new Validator<LocalDate>() {
            @Override
            public ValidationResult apply(LocalDate localDate, ValueContext valueContext) {
                return localDate==null ? ValidationResult.error("Capture active after date") : ValidationResult.ok();
            }
        }).bind(new ValueProvider<MedicationPrescriptionRq, LocalDate>() {
            @Override
            public LocalDate apply(MedicationPrescriptionRq rq) {
                return rq.getActiveAfter();
            }
        }, new Setter<MedicationPrescriptionRq, LocalDate>() {
            @Override
            public void accept(MedicationPrescriptionRq rq, LocalDate localDate) {
                rq.setActiveAfter(localDate);
            }
        });
        return createRow("Active after", datePicker);
    }

    private Div buildValidityTimeDiv(MedicationPrescriptionRq request, Binder<MedicationPrescriptionRq> binder) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setItems(IntStream.iterate(60, i -> i+30).limit(11).boxed().collect(Collectors.toList()));
        binder.forField(comboBox).withValidator(new Validator<Integer>() {
            @Override
            public ValidationResult apply(Integer integer, ValueContext valueContext) {
                return integer==null ? ValidationResult.error("Capture validity time") : ValidationResult.ok();
            }
        }).bind(new ValueProvider<MedicationPrescriptionRq, Integer>() {
            @Override
            public Integer apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationPrescriptionRq.getValidity();
            }
        }, new Setter<MedicationPrescriptionRq, Integer>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, Integer integer) {
                medicationPrescriptionRq.setValidity(integer);
            }
        });
        return createRow("Validity time (days)", comboBox);
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
        pref.setEnabled(false);
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
                    request.setPatient(patient);
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
                        return !s.isEmpty() && request.getPatient()!=null ? ValidationResult.ok() : ValidationResult.error("Capture a patient");
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
