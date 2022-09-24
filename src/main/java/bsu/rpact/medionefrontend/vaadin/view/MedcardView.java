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
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Route(value = "medcard", layout = MainLayout.class)
@PageTitle("Medcard")
public class MedcardView extends VerticalLayout {

    private final static int addressCharLimit = 200;
    private final static int homeCharLimit = 10;
    private final static String residentalAddressRegex = "^[#.0-9a-zA-Z\\s,-]+$";
    private final static String homeNumberRegex = "[0-9]{1,}[A-Za-z0-9]{0,}";
    public static final String FLAGSAPI_COM = "https://flagsapi.com/";
    public static final String FORMAT = "/flat/64.png";

    private final SessionManager sessionManager;
    private final MedcardService medcardService;
    private final CountryService countryService;

    public MedcardView(SessionManager sessionManager, MedcardService medcardService, CountryService countryService) {
        this.sessionManager = sessionManager;
        this.medcardService = medcardService;
        this.countryService = countryService;
        Optional<Medcard> optionalMedcard = medcardService.getSelf();
        if (optionalMedcard.isEmpty()) {
            add(new H3("Medcard not set yet"));
            add(new H4("Do you want to register new one?"));
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(true);
            VerticalLayout dialogLayout = createDialogLayout();
            dialog.add(dialogLayout);
            Button button = new Button("Register");
            button.addClickListener(e -> {
                dialog.open();
            });
            add(button);
            return;
        }
        Medcard medcard = optionalMedcard.get();
        add(new H2("Patient medcard"));
        add(new Label("Created: " + medcard.getDateCreated()));
        add(new Label("Expiring at: " + medcard.getValidTo()));
        add(new Label("Residental address: " + medcard.getResidentalAddress()));
        add(new H3("Illnesses"));
        PaginatedGrid<Illness> illnessGrid = new PaginatedGrid<>();
        VerticalLayout illnessLayout = getIllnessLayout(medcard, illnessGrid);
        add(illnessLayout);

        add(new H3("Operations"));
        PaginatedGrid<Operation> operationGrid = new PaginatedGrid<>();
        VerticalLayout operationLayout = getOperationLayout(medcard, operationGrid);
        add(operationLayout);
    }

    public static VerticalLayout getOperationLayout(Medcard medcard, PaginatedGrid<Operation> operationGrid) {
        operationGrid.addColumn(Operation::getId).setHeader("№");
        operationGrid.addColumn(Operation::getName).setHeader("Name");
        operationGrid.addColumn(Operation::getDescription).setHeader("Description");
        operationGrid.addColumn(Operation::getOperationDate).setHeader("Date");
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
        TextField operationSearchField = new TextField();
        operationSearchField.setWidth("20%");
        operationSearchField.setPlaceholder("Search");
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

    public static VerticalLayout getIllnessLayout(Medcard medcard, PaginatedGrid<Illness> illnessGrid) {
        illnessGrid.addColumn(Illness::getId).setHeader("№");
        illnessGrid.addColumn(Illness::getDescription).setHeader("Description");
        illnessGrid.addColumn(Illness::getIllFrom).setHeader("Ill From");
        illnessGrid.addColumn(Illness::getIllTo).setHeader("Ill To");
        GridListDataView<Illness> illnessDataView =
                illnessGrid.setItems(medcard.getIllnessList());
        illnessGrid.getColumns().stream().forEach(item -> {
            item.setResizable(true);
            item.setSortable(true);
        });
        illnessGrid.setAllRowsVisible(true);
        illnessGrid.setPageSize(10);
        illnessGrid.setPaginatorSize(5);
        TextField illnessSearchField = new TextField();
        illnessSearchField.setWidth("20%");
        illnessSearchField.setPlaceholder("Search");
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
        VerticalLayout dialogLayout = new VerticalLayout(new Label("Select your country"));
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
        Pattern addressPattern = Pattern.compile(residentalAddressRegex);
        addressTextArea.addValueChangeListener(e -> {
            if (!addressPattern.matcher(e.getValue()).matches()) {
                e.getSource().setInvalid(true);
                e.getSource().setHelperText("Invalid address format." +
                        "Address cannot be empty, you can use only letters, numbers, commas and dots");
            } else {
                e.getSource().setHelperText(e.getValue().length() + "/" + addressCharLimit);
            }
        });

        TextArea homeTextArea = new TextArea();
        homeTextArea.setMaxLength(homeCharLimit);
        homeTextArea.setValueChangeMode(ValueChangeMode.EAGER);
        Pattern homeNumberPattern = Pattern.compile(homeNumberRegex);
        homeTextArea.addValueChangeListener(e -> {
            if (!homeNumberPattern.matcher(e.getValue()).matches()) {
                e.getSource().setInvalid(true);
                e.getSource().setHelperText("Invalid home number format." +
                        "Home number cannot be empty, you can use only letters and numbers");
            } else {
                e.getSource().setHelperText(e.getValue().length() + "/" + homeCharLimit);
            }
        });

        Label streetAddress = new Label("Enter your residental street address");
        Label homeNumber = new Label("Enter your home number");

        dialogLayout.add(countries, streetAddress, addressTextArea, homeNumber, homeTextArea);
        Button apply = new Button("Apply");
        apply.addClickListener(e -> {
            if (!addressTextArea.isInvalid()) {
                MessageResponse response = medcardService.
                        createSelf(parseResidentalAddress(countries, addressTextArea, homeTextArea));
                UI.getCurrent().getPage().reload();
                UiUtils.generateSuccessNotification(response.getMessage());
            }
        });
        dialogLayout.add(apply);
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

}
