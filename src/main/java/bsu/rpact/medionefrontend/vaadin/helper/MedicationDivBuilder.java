package bsu.rpact.medionefrontend.vaadin.helper;

import bsu.rpact.medionefrontend.pojo.medical.*;
import bsu.rpact.medionefrontend.pojo.other.Href;
import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.service.medical.web.MedicationFormService;
import bsu.rpact.medionefrontend.service.medical.web.RegistryMedicationService;
import bsu.rpact.medionefrontend.vaadin.components.MedicationDetails;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@org.springframework.stereotype.Component
public class MedicationDivBuilder {

    public static final Integer MAX_MEDICATIONS_IN_PRESCRIPTION = 3;
    @Autowired
    private MedicationFormService medicationFormService;
    @Autowired
    private RegistryMedicationService registryMedicationService;

    public Div buildMedicationDiv(MedicationPrescriptionRq request, Binder<MedicationPrescriptionRq> binder, VerticalLayout medicationsLayout) {
        AtomicReference<RegistryMedication> registryMedicationAtomicReference = new AtomicReference<>();
        TextField medicationField = new TextField();
        medicationField.getElement().getStyle().set("width","580px");
        Button lookup = new Button();
        Icon icon = new Icon(VaadinIcon.CHECK);
        icon.setColor("green");
        icon.getElement().getStyle().set("width", "100px");
        icon.setVisible(false);
        Button add = new Button();
        add.setIcon(VaadinIcon.PLUS.create());
        add.setVisible(false);
        lookup.setIcon(VaadinIcon.SEARCH.create());
        lookup.addClickListener(e -> showMedicationLookupDialog(medicationField.getValue(), new Consumer<RegistryMedication>() {
            @Override
            public void accept(RegistryMedication registryMedication) {
                if (registryMedication != null) {
                    registryMedicationAtomicReference.set(registryMedication);
                    medicationField.setValue(registryMedication.getTradeName());
                    icon.setVisible(true);
                    if (medicationsLayout.getChildren().count() != MAX_MEDICATIONS_IN_PRESCRIPTION) add.setVisible(true);
                    medicationField.setReadOnly(true);
                    lookup.setEnabled(false);
                }
            }
        }));
        icon.addClickListener(e -> {
            clearMedicationLookup(icon, add, medicationField, lookup);
        });
        add.addClickListener(e -> {
            clearMedicationLookup(icon, add, medicationField, lookup);
            medicationField.setInvalid(false);
            Button remove = new Button();
            MedicationDetails details = buildMedicationDetails(binder, registryMedicationAtomicReference.get(), remove);
            remove.setIcon(VaadinIcon.CLOSE_SMALL.create());
            remove.addThemeVariants(ButtonVariant.LUMO_SMALL);
            remove.addClassName("transparent-button");
            remove.addClickListener(event -> {
                medicationsLayout.remove(details);
                binder.getBean().getMedicationDetails().remove(details);
                if (medicationsLayout.getChildren().count() < MAX_MEDICATIONS_IN_PRESCRIPTION) add.setVisible(true);
                medicationField.setValue(registryMedicationAtomicReference.get().getTradeName());
            });
            medicationsLayout.add(details);
        });
        medicationField.setErrorMessage("Please capture a medication");
        lookup.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return createRow("Medication   ", medicationField, lookup, icon, add);
    }

    private MedicationDetails buildMedicationDetails(Binder<MedicationPrescriptionRq> binder, RegistryMedication registryMedication, Button remove) {
        MedicationDetails medicationDetails = new MedicationDetails();
        medicationDetails.setRegistryMedication(registryMedication);
        medicationDetails.setOpened(true);
        binder.getBean().getMedicationDetails().add(medicationDetails);
        Div header = getMedicationAnchorDiv(registryMedication);
        medicationDetails.setSummary(header);
        header.add(remove);
        ComboBox<MedicationForm> medicationFormComboBox = new ComboBox<>();
        medicationFormComboBox.setItemLabelGenerator(MedicationForm::getDisplay);
        medicationFormComboBox.setItems(medicationFormService.getMedicationFormsFromSnomed());
        medicationFormComboBox.getElement().getStyle().set("width","400px");
        binder.forField(medicationFormComboBox).bind(new ValueProvider<MedicationPrescriptionRq, MedicationForm>() {
            @Override
            public MedicationForm apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getMedicationForm();
            }
        }, new Setter<MedicationPrescriptionRq, MedicationForm>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, MedicationForm medicationForm) {
                medicationDetails.setMedicationForm(medicationFormComboBox.getValue());
            }
        });
        ComboBox<String> dosageMethodComboBox = new ComboBox<>();
        dosageMethodComboBox.setItems(DosageMethodsStrings.getDosageMethods());
        medicationDetails.addContent(new HorizontalLayout(createRow("Medication form", medicationFormComboBox),
                createRow("Dosage method", dosageMethodComboBox)));
        Div onceDiv = buildOnceDiv(binder, medicationDetails);
        onceDiv.setVisible(false);
        Div periodicallyDiv = buildPeriodicallyDiv(binder, medicationDetails);
        periodicallyDiv.setVisible(false);
        Div onDemandDiv = buildOnDemandDiv(binder, medicationDetails);
        onDemandDiv.setVisible(false);
        Div tetrationDiv = buildTetrationDiv(binder, medicationDetails);
        tetrationDiv.setVisible(false);
        TextArea comment = new TextArea();
        comment.setLabel("Comment");
        comment.getElement().getStyle().set("width", "100%");
        binder.forField(comment).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getComment();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.setComment(s);
            }
        });
        bindDosageCombobox(dosageMethodComboBox, onceDiv, periodicallyDiv, onDemandDiv, tetrationDiv);
        medicationDetails.addContent(onceDiv, periodicallyDiv, onDemandDiv, tetrationDiv, comment);
        return medicationDetails;
    }

    private void bindDosageCombobox(ComboBox<String> dosageMethodComboBox, Div onceDiv, Div periodicallyDiv, Div onDemandDiv, Div tetrationDiv) {
        dosageMethodComboBox.addValueChangeListener(e -> {
            switch (dosageMethodComboBox.getValue()){
                case DosageMethodsStrings.ONCE:
                    onceDiv.setVisible(true);
                    periodicallyDiv.setVisible(false);
                    onDemandDiv.setVisible(false);
                    tetrationDiv.setVisible(false);
                    break;
                case DosageMethodsStrings.PERIODICALLY:
                    onceDiv.setVisible(false);
                    periodicallyDiv.setVisible(true);
                    onDemandDiv.setVisible(false);
                    tetrationDiv.setVisible(false);
                    break;
                case DosageMethodsStrings.ON_DEMAND:
                    onceDiv.setVisible(false);
                    periodicallyDiv.setVisible(false);
                    onDemandDiv.setVisible(true);
                    tetrationDiv.setVisible(false);
                    break;
                case DosageMethodsStrings.TITRATION_METHOD:
                    onceDiv.setVisible(false);
                    periodicallyDiv.setVisible(false);
                    onDemandDiv.setVisible(false);
                    tetrationDiv.setVisible(true);
                    break;
            }
        });
    }

    private Div buildTetrationDiv(Binder<MedicationPrescriptionRq> binder, MedicationDetails medicationDetails) {
        Div div = new Div();
        NumberField quantityField = new NumberField();
        quantityField.setHasControls(true);
        quantityField.setMin(0);
        binder.forField(quantityField).bind(new ValueProvider<MedicationPrescriptionRq, Double>() {
            @Override
            public Double apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getTetrationDosageMethod().getAmount();
            }
        }, new Setter<MedicationPrescriptionRq, Double>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, Double aDouble) {
                medicationDetails.getTetrationDosageMethod().setAmount(aDouble);
            }
        });
        ComboBox<String> measureUnitsCombobox = new ComboBox<>();
        measureUnitsCombobox.setItems(MedicationMeasureUnitsStrings.getMedicationMeasureUnits());
        binder.forField(measureUnitsCombobox).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getTetrationDosageMethod().getUnit();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getTetrationDosageMethod().setUnit(s);
            }
        });
        TextField intTimesField = new TextField();
        intTimesField.setPattern("[0-9]*");
        intTimesField.setPreventInvalidInput(true);
        binder.forField(intTimesField).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return Integer.toString(medicationDetails.getTetrationDosageMethod().getTimes());
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getTetrationDosageMethod().setTimes(Integer.parseInt(s));
            }
        });
        Label label = new Label("times each");
        TextField timeQuantityField = new TextField();
        timeQuantityField.setPattern("[0-9]*");
        timeQuantityField.setPreventInvalidInput(true);
        binder.forField(timeQuantityField).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return Integer.toString(medicationDetails.getTetrationDosageMethod().getTimePeriodQuantity());
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getTetrationDosageMethod().setTimePeriodQuantity(Integer.parseInt(s));
            }
        });
        ComboBox<String> timePeriodCombobox = new ComboBox<>();
        timePeriodCombobox.setItems(MedicationTimePeriodsStrings.getMedicationTimePeriods());
        binder.forField(timePeriodCombobox).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getTetrationDosageMethod().getTimePeriod();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getTetrationDosageMethod().setTimePeriod(s);
            }
        });
        div.add(quantityField, measureUnitsCombobox, intTimesField, label, timeQuantityField, timePeriodCombobox);
        NumberField coefField = new NumberField();
        coefField.setHasControls(true);
        coefField.setMin(0);
        binder.forField(coefField).bind(new ValueProvider<MedicationPrescriptionRq, Double>() {
            @Override
            public Double apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getTetrationDosageMethod().getCoefficient();
            }
        }, new Setter<MedicationPrescriptionRq, Double>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, Double aDouble) {
                medicationDetails.getTetrationDosageMethod().setCoefficient(aDouble);
            }
        });
        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setItems("Decreasing", "Increasing");
        binder.forField(radioButtonGroup).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getTetrationDosageMethod().getCoefTrend();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getTetrationDosageMethod().setCoefTrend(s);
            }
        });
        HorizontalLayout rbLayout = new HorizontalLayout();
        rbLayout.setSpacing(true);
        rbLayout.add(radioButtonGroup);
        radioButtonGroup.setRequiredIndicatorVisible(true);
        radioButtonGroup.setLabel("Coefficient Trend");
        radioButtonGroup.setRenderer(new TextRenderer<>(String::toString));
        radioButtonGroup.setItemEnabledProvider(Objects::nonNull);
        div.add(quantityField, measureUnitsCombobox, intTimesField, label, timeQuantityField, timePeriodCombobox,
                createRow("Tetration coefficient", coefField),
                rbLayout);
        return div;
    }

    private Div buildPeriodicallyDiv(Binder<MedicationPrescriptionRq> binder, MedicationDetails medicationDetails) {
        Div div = new Div();
        NumberField quantityField = new NumberField();
        quantityField.setHasControls(true);
        quantityField.setMin(0);
        binder.forField(quantityField).bind(new ValueProvider<MedicationPrescriptionRq, Double>() {
            @Override
            public Double apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getPeriodicalDosageMethod().getAmount();
            }
        }, new Setter<MedicationPrescriptionRq, Double>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, Double aDouble) {
                medicationDetails.getPeriodicalDosageMethod().setAmount(aDouble);
            }
        });
        ComboBox<String> measureUnitsCombobox = new ComboBox<>();
        measureUnitsCombobox.setItems(MedicationMeasureUnitsStrings.getMedicationMeasureUnits());
        binder.forField(measureUnitsCombobox).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getPeriodicalDosageMethod().getUnit();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getPeriodicalDosageMethod().setUnit(s);
            }
        });
        TextField intTimesField = new TextField();
        intTimesField.setPattern("[0-9]*");
        intTimesField.setPreventInvalidInput(true);
        binder.forField(intTimesField).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return Integer.toString(medicationDetails.getPeriodicalDosageMethod().getTimes());
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getPeriodicalDosageMethod().setTimes(Integer.parseInt(s));
            }
        });
        Label label = new Label("times each");
        TextField timeQuantityField = new TextField();
        timeQuantityField.setPattern("[0-9]*");
        timeQuantityField.setPreventInvalidInput(true);
        binder.forField(timeQuantityField).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return Integer.valueOf(medicationDetails.getPeriodicalDosageMethod().getTimePeriodQuantity()).toString();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getPeriodicalDosageMethod().setTimePeriodQuantity(Integer.parseInt(s));
            }
        });
        ComboBox<String> timePeriodCombobox = new ComboBox<>();
        timePeriodCombobox.setItems(MedicationTimePeriodsStrings.getMedicationTimePeriods());
        binder.forField(timePeriodCombobox).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getPeriodicalDosageMethod().getTimePeriod();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getPeriodicalDosageMethod().setTimePeriod(s);
            }
        });
        div.add(quantityField, measureUnitsCombobox, intTimesField, label, timeQuantityField, timePeriodCombobox);
        return div;
    }

    private Div buildOnDemandDiv(Binder<MedicationPrescriptionRq> binder, MedicationDetails medicationDetails) {
        NumberField quantityField = new NumberField();
        quantityField.setHasControls(true);
        quantityField.setMin(0);
        binder.forField(quantityField).bind(new ValueProvider<MedicationPrescriptionRq, Double>() {
            @Override
            public Double apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getOnDemandDosageMethod().getAmount();
            }
        }, new Setter<MedicationPrescriptionRq, Double>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, Double aDouble) {
                medicationDetails.getOnDemandDosageMethod().setAmount(aDouble);
            }
        });
        ComboBox<String> measureUnitsCombobox = new ComboBox<>();
        measureUnitsCombobox.setItems(MedicationMeasureUnitsStrings.getMedicationMeasureUnits());
        binder.forField(measureUnitsCombobox).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getOnDemandDosageMethod().getUnit();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getOnDemandDosageMethod().setUnit(s);
            }
        });
        return createRow("Amount", quantityField, measureUnitsCombobox);
    }

    private Div buildOnceDiv(Binder<MedicationPrescriptionRq> binder, MedicationDetails medicationDetails) {
        NumberField quantityField = new NumberField();
        quantityField.setHasControls(true);
        quantityField.setMin(0);
        binder.forField(quantityField).bind(new ValueProvider<MedicationPrescriptionRq, Double>() {
            @Override
            public Double apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getOnceDosageMethod().getAmount();
            }
        }, new Setter<MedicationPrescriptionRq, Double>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, Double aDouble) {
                medicationDetails.getOnceDosageMethod().setAmount(aDouble);
            }
        });
        ComboBox<String> measureUnitsCombobox = new ComboBox<>();
        measureUnitsCombobox.setItems(MedicationMeasureUnitsStrings.getMedicationMeasureUnits());
        binder.forField(measureUnitsCombobox).bind(new ValueProvider<MedicationPrescriptionRq, String>() {
            @Override
            public String apply(MedicationPrescriptionRq medicationPrescriptionRq) {
                return medicationDetails.getOnceDosageMethod().getUnit();
            }
        }, new Setter<MedicationPrescriptionRq, String>() {
            @Override
            public void accept(MedicationPrescriptionRq medicationPrescriptionRq, String s) {
                medicationDetails.getOnceDosageMethod().setUnit(s);
            }
        });
        return createRow("Amount", quantityField, measureUnitsCombobox);
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

    private void clearMedicationLookup(Icon icon, Button add, TextField medicationField, Button lookup) {
        icon.setVisible(false);
        add.setVisible(false);
        medicationField.clear();
        medicationField.setReadOnly(false);
        lookup.setEnabled(true);
    }


    public void showMedicationLookupDialog(String searchTerm, Consumer<RegistryMedication> consumer) {
        Dialog dialog = new Dialog();
        dialog.setWidth("100%");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        Grid<RegistryMedication> grid = new Grid<>(RegistryMedication.class);
        grid.setColumnReorderingAllowed(false);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.removeAllColumns();
        List<RegistryMedication> registryMedicationList = registryMedicationService.searchMedicationInRegistryByName(searchTerm);
        grid.setItems(registryMedicationList);
        addSearchField(dialog, grid, registryMedicationList);
        grid.addColumn(medication -> {
            return medication.getOrderNumber();
        }).setKey("orderNumber").setHeader("Order number").setFlexGrow(1).setResizable(true);
        grid.addComponentColumn(medication -> {
            return getMedicationAnchorDiv(medication);
        }).setKey("tradeName").setHeader("Trade name").setFlexGrow(1).setResizable(true).setAutoWidth(true);
        grid.addColumn(medication -> {
            return medication.getInternationalName();
        }).setKey("intName").setHeader("International Name").setFlexGrow(1).setResizable(true);
        grid.addColumn(medication -> {
            return medication.getManufacturer();
        }).setKey("manufacturer").setHeader("Manufacturer").setFlexGrow(1).setResizable(true);
        grid.addColumn(medication -> {
            return medication.getApplicant();
        }).setKey("applicant").setHeader("Applicant").setFlexGrow(1).setResizable(true);
        grid.addColumn(medication -> {
            return medication.getIdNumber();
        }).setKey("idNumber").setHeader("Id Number").setFlexGrow(1).setResizable(true);
        grid.addColumn(medication -> {
            return medication.getRegistrationDate();
        }).setKey("registrationDate").setHeader("Registration Date").setFlexGrow(1).setResizable(true);
        grid.addColumn(medication -> {
            return medication.getExpirationDate();
        }).setKey("expirationDate").setHeader("Expiration Date").setFlexGrow(1).setResizable(true);
        grid.addColumn(medication -> {
            return medication.getOriginal();
        }).setKey("original").setHeader("Original").setFlexGrow(1).setResizable(true);
        grid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent()) {
                consumer.accept(event.getFirstSelectedItem().get());
                dialog.close();
            }
        });
        dialog.add(grid);
        dialog.open();
    }

    private Div getMedicationAnchorDiv(RegistryMedication medication) {
        String tradeName = medication.getTradeName();
        Div div = new Div();
        for (Href href : medication.getHrefs()) {
            String label = href.getLabel();
            String link = href.getLink();
            int index = tradeName.indexOf(label);
            while (index != -1) {
                String textBefore = tradeName.substring(0, index);
                if (!textBefore.isEmpty()) {
                    div.add(new Text(textBefore));
                }
                String labelText = tradeName.substring(index, index + label.length());
                Anchor anchor = new Anchor(RcethRegistryItem.baseUrl + link, labelText);
                anchor.setTarget("_blank");
                div.add(anchor);
                tradeName = tradeName.substring(index + label.length());
                index = tradeName.indexOf(label);
            }
        }
        if (!tradeName.isEmpty()) {
            div.add(new Text(tradeName));
        }
        return div;
    }

    private void addSearchField(Dialog dialog, Grid<RegistryMedication> grid, List<RegistryMedication> registryMedicationList) {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search by trade name");
        searchField.addValueChangeListener(event -> {
            String searchValue = event.getValue();
            if (searchValue != null && !searchValue.isEmpty()) {
                // Find the first item in the grid that matches the search value
                Optional<RegistryMedication> firstMatch = registryMedicationList.stream()
                        .filter(medication -> medication.getTradeName().toLowerCase().contains(searchValue.toLowerCase()))
                        .findFirst();
                // Scroll to the first matching item
                firstMatch.ifPresent(medication -> {
                    grid.scrollToIndex(Integer.parseInt(medication.getOrderNumber()) - 1);
                });
            } else {
                grid.deselectAll();
            }
        });
        dialog.add(searchField);
    }
}
