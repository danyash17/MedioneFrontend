package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Illness;
import bsu.rpact.medionefrontend.entity.Medcard;
import bsu.rpact.medionefrontend.entity.Operation;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.pojo.other.Country;
import bsu.rpact.medionefrontend.service.CountryService;
import bsu.rpact.medionefrontend.service.MedcardService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.List;
import java.util.Optional;

@Route(value = "medcard", layout = MainLayout.class)
@PageTitle("Medcard")
public class MedcardView extends VerticalLayout implements LocaleChangeObserver {

    private final static int addressCharLimit = 200;
    private final static int homeCharLimit = 10;
    public static final String FLAGSAPI_COM = "https://flagsapi.com/";
    public static final String FORMAT = "/flat/64.png";

    private final SessionManager sessionManager;
    private final MedcardService medcardService;
    private final CountryService countryService;
    private final ValidatorUtils validatorUtils;
    private String medcardNotSetYet = getTranslation("medcard.not_set_yet");
    private String doYouWantToRegisterNewOne = getTranslation("medcard.do_you_want_to_register_another_one");
    private String patientMedcard = getTranslation("medcard.patient_medcard");
    private String createDdotSpace = getTranslation("medcard.create_ddot_space");
    private String expiringDdotSpace = getTranslation("medcard.expiring_ddot_space");
    private String residentalAddressDdotSpace = getTranslation("medcard.residental_address_ddot_space");
    private String illnesses = getTranslation("medcard.illnesses");
    private String operations = getTranslation("medcard.operations");
    private String register = getTranslation("medcard.register");
    private String name = getTranslation("medcard.name");
    private String description = getTranslation("medcard.description");
    private String date = getTranslation("medcard.date");
    private String search = getTranslation("medcard.search");
    private String illFrom = getTranslation("medcard.ill_from");
    private String illTo = getTranslation("medcard.ill_to");
    private String enterYourResidentalStreetAddress = getTranslation("medcard.enter_your_residental_address");
    private String enterYourHomeNumber = getTranslation("medcard.enter_your_home_number");
    private String apply = getTranslation("medcard.apply");
    private String selectYourCountry = getTranslation("medcard.select_your_country");
    private final H3 medcardNotSetYetH3 = new H3();
    private final H4 doYouWantToRegisterNewOneH4 = new H4();
    private final Button registerButton = new Button();
    private final H2 patientMedcardH2 = new H2();
    private final Label createdLabel = new Label();
    private final Label expiringAtLabel = new Label();
    private final Label residentalAddressLabel = new Label();
    private final H3 illnessesH3 = new H3();
    private final H3 operationsH3 = new H3();
    private Label selectYourCountryLabel = new Label();
    private Label streetAddressLabel = new Label();
    private Label homeNumberLabel = new Label();
    private Button applyButton = new Button();
    private final PaginatedGrid<Operation> operationGrid = new PaginatedGrid<>();
    private final PaginatedGrid<Illness> illnessGrid = new PaginatedGrid<>();
    private TextField operationSearchField = new TextField();
    private TextField illnessSearchField = new TextField();

    Optional<Medcard> optionalMedcard;

    public MedcardView(SessionManager sessionManager, MedcardService medcardService, CountryService countryService, ValidatorUtils validatorUtils) {
        this.sessionManager = sessionManager;
        this.medcardService = medcardService;
        this.countryService = countryService;
        this.validatorUtils = validatorUtils;
        optionalMedcard = medcardService.getSelf();
        if (optionalMedcard.isEmpty()) {
            medcardNotSetYetH3.setText(medcardNotSetYet);
            add(medcardNotSetYetH3);
            doYouWantToRegisterNewOneH4.setText(doYouWantToRegisterNewOne);
            add(doYouWantToRegisterNewOneH4);
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(true);
            VerticalLayout dialogLayout = createDialogLayout();
            dialog.add(dialogLayout);
            registerButton.setText(register);
            registerButton.addClickListener(e -> {
                dialog.open();
            });
            add(registerButton);
            return;
        }
        Medcard medcard = optionalMedcard.get();
        patientMedcardH2.setText(patientMedcard);
        add(patientMedcardH2);
        createdLabel.setText(createDdotSpace + medcard.getDateCreated());
        add(createdLabel);
        expiringAtLabel.setText(expiringDdotSpace + medcard.getValidTo());
        add(expiringAtLabel);
        residentalAddressLabel.setText(residentalAddressDdotSpace + medcard.getResidentalAddress());
        add(residentalAddressLabel);
        illnessesH3.setText(illnesses);
        add(illnessesH3);
        VerticalLayout illnessLayout = getIllnessLayout(medcard, illnessGrid);
        add(illnessLayout);

        operationsH3.setText(operations);
        add(operationsH3);
        VerticalLayout operationLayout = getOperationLayout(medcard, operationGrid);
        add(operationLayout);
    }

    public VerticalLayout getOperationLayout(Medcard medcard, PaginatedGrid<Operation> operationGrid) {
        operationGrid.addColumn(Operation::getId).setHeader("№");
        operationGrid.addColumn(Operation::getName).setHeader(name).setKey("name");
        operationGrid.addColumn(Operation::getDescription).setHeader(description).setKey("description");
        operationGrid.addColumn(Operation::getOperationDate).setHeader(date).setKey("date");
        operationGrid.setItems(medcard.getOperationList());
        operationGrid.setAllRowsVisible(true);
        operationGrid.setPageSize(10);
        operationGrid.setPaginatorSize(5);
        GridListDataView<Operation> operationDataView =
                operationGrid.setItems(medcard.getOperationList());
        operationGrid.getColumns().stream().forEach(item -> {
            item.setResizable(true);
            item.setSortable(true);
        });
        operationSearchField.setWidth("20%");
        operationSearchField.setPlaceholder(search);
        operationSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        operationSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        operationSearchField.addValueChangeListener(e -> operationDataView.refreshAll());

        operationDataView.addFilter(item -> {
            String searchTerm = operationSearchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            boolean matchesId = matchesTerm(String.valueOf(item.getId()),
                    searchTerm);
            boolean matchesDescription = matchesTerm(item.getDescription(), searchTerm);
            boolean matchesName = matchesTerm(item.getName(),
                    searchTerm);
            boolean matchesDate = matchesTerm(String.valueOf(item.getOperationDate()),
                    searchTerm);
            return matchesId || matchesDescription || matchesName || matchesDate;
        });
        VerticalLayout operationLayout = new VerticalLayout(operationSearchField, operationGrid);
        operationLayout.setPadding(false);
        return operationLayout;
    }

    public VerticalLayout getIllnessLayout(Medcard medcard, PaginatedGrid<Illness> illnessGrid) {
        illnessGrid.addColumn(Illness::getId).setHeader("№");
        illnessGrid.addColumn(Illness::getDescription).setHeader(description).setKey("description");
        illnessGrid.addColumn(Illness::getIllFrom).setHeader(illFrom).setKey("illFrom");
        illnessGrid.addColumn(Illness::getIllTo).setHeader(illTo).setKey("illTo");
        GridListDataView<Illness> illnessDataView =
                illnessGrid.setItems(medcard.getIllnessList());
        illnessGrid.getColumns().stream().forEach(item -> {
            item.setResizable(true);
            item.setSortable(true);
        });
        illnessGrid.setAllRowsVisible(true);
        illnessGrid.setPageSize(10);
        illnessGrid.setPaginatorSize(5);
        illnessSearchField.setWidth("20%");
        illnessSearchField.setPlaceholder(search);
        illnessSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        illnessSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        illnessSearchField.addValueChangeListener(e -> illnessDataView.refreshAll());

        illnessDataView.addFilter(item -> {
            String searchTerm = illnessSearchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            boolean matchesId = matchesTerm(String.valueOf(item.getId()),
                    searchTerm);
            boolean matchesDescription = matchesTerm(item.getDescription(), searchTerm);
            boolean matchesFrom = matchesTerm(String.valueOf(item.getIllFrom()),
                    searchTerm);
            boolean matchesTo = matchesTerm(String.valueOf(item.getIllTo()),
                    searchTerm);
            return matchesId || matchesDescription || matchesFrom || matchesTo;
        });
        VerticalLayout illnessLayout = new VerticalLayout(illnessSearchField, illnessGrid);
        illnessLayout.setPadding(false);
        return illnessLayout;
    }

    private static boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private VerticalLayout createDialogLayout() {
        selectYourCountryLabel.setText(selectYourCountry);
        VerticalLayout dialogLayout = new VerticalLayout(selectYourCountryLabel);
        ComboBox<Country> countries = new ComboBox();
        List<Country> countryList = countryService.getAllCountries();
        countryList.forEach(item -> {
            item.setName(StringUtils.capitalize(item.getName()));
            item.setFlag(FLAGSAPI_COM + item.getCode() + FORMAT);
        });
        countries.setItems(countryList);
        countries.setRenderer(createRenderer());
        countries.setItemLabelGenerator(country -> country.getName());
        countries.setAllowCustomValue(true);

        TextArea addressTextArea = new TextArea();
        addressTextArea.setMaxLength(addressCharLimit);
        addressTextArea.setValueChangeMode(ValueChangeMode.EAGER);
        addressTextArea.addValueChangeListener(e -> {
            if (!validatorUtils.isValidResidentalAddress(e.getValue())) {
                e.getSource().setInvalid(true);
                e.getSource().setHelperText(getTranslation("medcard.invalid_address_format"));
            } else {
                e.getSource().setHelperText(e.getValue().length() + "/" + addressCharLimit);
            }
        });

        TextArea homeTextArea = new TextArea();
        homeTextArea.setMaxLength(homeCharLimit);
        homeTextArea.setValueChangeMode(ValueChangeMode.EAGER);
        homeTextArea.addValueChangeListener(e -> {
            if (!validatorUtils.isValidHomeNumber(e.getValue())) {
                e.getSource().setInvalid(true);
                e.getSource().setHelperText(getTranslation("medcard.invalid_homenumber_format"));
            } else {
                e.getSource().setHelperText(e.getValue().length() + "/" + homeCharLimit);
            }
        });

        streetAddressLabel.setText(enterYourResidentalStreetAddress);
        homeNumberLabel.setText(enterYourHomeNumber);

        dialogLayout.add(countries, streetAddressLabel, addressTextArea, homeNumberLabel, homeTextArea);
        applyButton.setText(apply);
        applyButton.addClickListener(e -> {
            if (!addressTextArea.isInvalid()) {
                MessageResponse response = medcardService.
                        createSelf(parseResidentalAddress(countries, addressTextArea, homeTextArea));
                UI.getCurrent().getPage().reload();
                UiUtils.generateSuccessNotification(response.getMessage());
            }
        });
        dialogLayout.add(applyButton);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Renderer<Country> createRenderer() {
        StringBuilder tpl = new StringBuilder();
        tpl.append("<div style=\"display: flex;\">");
        tpl.append("  <img style=\"height: var(--lumo-size-m); margin-right: var(--lumo-space-s);\" src=\"${item.flag}\" alt=\"Portrait of ${item.name} ${item.code}\" />");
        tpl.append("  <div>");
        tpl.append("    ${item.name} ${item.code}");
        tpl.append("    <div style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">${item.region}</div>");
        tpl.append("  </div>");
        tpl.append("</div>");

        return LitRenderer.<Country>of(tpl.toString()).withProperty("flag", Country::getFlag)
                .withProperty("name", Country::getName).withProperty("code", Country::getCode)
                .withProperty("region", Country::getRegion);
    }

    private String parseResidentalAddress(ComboBox<Country> countries, TextArea addressTextArea, TextArea homeTextArea) {
        return countries.getValue().getName() + ", " + addressTextArea.getValue() + ", " + homeTextArea.getValue();
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        medcardNotSetYet = getTranslation("medcard.not_set_yet");
        doYouWantToRegisterNewOne = getTranslation("medcard.do_you_want_to_register_another_one");
        patientMedcard = getTranslation("medcard.patient_medcard");
        createDdotSpace = getTranslation("medcard.create_ddot_space");
        expiringDdotSpace = getTranslation("medcard.expiring_ddot_space");
        residentalAddressDdotSpace = getTranslation("medcard.residental_address_ddot_space");
        illnesses = getTranslation("medcard.illnesses");
        operations = getTranslation("medcard.operations");
        register = getTranslation("medcard.register");
        name = getTranslation("medcard.name");
        description = getTranslation("medcard.description");
        date = getTranslation("medcard.date");
        search = getTranslation("medcard.search");
        illFrom = getTranslation("medcard.ill_from");
        illTo = getTranslation("medcard.ill_to");
        selectYourCountry = getTranslation("medcard.select_your_country");
        enterYourResidentalStreetAddress = getTranslation("medcard.enter_your_residental_address");
        enterYourHomeNumber = getTranslation("medcard.enter_your_home_number");
        apply = getTranslation("medcard.apply");
        medcardNotSetYetH3.setText(medcardNotSetYet);
        doYouWantToRegisterNewOneH4.setText(doYouWantToRegisterNewOne);
        registerButton.setText(register);
        patientMedcardH2.setText(patientMedcard);
        illnessesH3.setText(illnesses);
        operationsH3.setText(operations);
        selectYourCountryLabel.setText(selectYourCountry);
        streetAddressLabel.setText(enterYourResidentalStreetAddress);
        homeNumberLabel.setText(enterYourHomeNumber);
        applyButton.setText(apply);
        operationSearchField.setPlaceholder(search);
        illnessSearchField.setPlaceholder(search);

        if(optionalMedcard!=null && optionalMedcard.isPresent()){
            Medcard medcard = optionalMedcard.get();
            createdLabel.setText(createDdotSpace + medcard.getDateCreated());
            expiringAtLabel.setText(expiringDdotSpace + medcard.getValidTo());
            residentalAddressLabel.setText(residentalAddressDdotSpace + medcard.getResidentalAddress());
        }
        if(illnessGrid!=null){
            illnessGrid.getColumnByKey("description").setHeader(description);
            illnessGrid.getColumnByKey("illFrom").setHeader(illFrom);
            illnessGrid.getColumnByKey("illTo").setHeader(illTo);
        }
        if(operationGrid!=null){
            operationGrid.getColumnByKey("name").setHeader(name);
            operationGrid.getColumnByKey("description").setHeader(description);
            operationGrid.getColumnByKey("date").setHeader(date);
        }
    }
}
