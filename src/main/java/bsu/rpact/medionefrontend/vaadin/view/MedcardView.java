package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Illness;
import bsu.rpact.medionefrontend.entity.Medcard;
import bsu.rpact.medionefrontend.entity.Operation;
import bsu.rpact.medionefrontend.exception.MedcardNotSetException;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.service.MedcardService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Route(value = "medcard", layout = MainLayout.class)
@PageTitle("Medcard")
public class MedcardView extends VerticalLayout {

    private final static int charLimit = 200;
    private final static String residentalAddressRegex = "^[#.0-9a-zA-Z\\s,-]+$";

    private final SessionManager sessionManager;
    private final MedcardService medcardService;

    public MedcardView(SessionManager sessionManager, MedcardService medcardService) {
        this.sessionManager = sessionManager;
        this.medcardService = medcardService;
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
        Grid<Illness> illnessGrid = new Grid<>(Illness.class, false);
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
        add(illnessLayout);

        add(new H3("Operations"));
        Grid<Operation> operationGrid = new Grid<>(Operation.class, false);
        operationGrid.addColumn(Operation::getId).setHeader("№");
        operationGrid.addColumn(Operation::getName).setHeader("Name");
        operationGrid.addColumn(Operation::getDescription).setHeader("Description");
        operationGrid.addColumn(Operation::getOperationDate).setHeader("Date");
        operationGrid.setItems(medcard.getOperationList());
        operationGrid.setAllRowsVisible(true);
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
        add(operationLayout);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout dialogLayout = new VerticalLayout(new Label("Enter your residental address"));
        TextArea textArea = new TextArea();
        textArea.setMaxLength(charLimit);
        textArea.setValueChangeMode(ValueChangeMode.EAGER);
        Pattern pattern = Pattern.compile(residentalAddressRegex);
        textArea.addValueChangeListener(e -> {
            if (!pattern.matcher(e.getValue()).matches()) {
                e.getSource().setInvalid(true);
                e.getSource().setHelperText("Invalid address format." +
                        "Address cannot be empty, you can use only letters, numbers, commas and dots");
            } else {
                e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
            }
        });
        dialogLayout.add(textArea);
        Button apply = new Button("Apply");
        apply.addClickListener(e -> {
            if(!textArea.isInvalid()){
                MessageResponse response = medcardService.createSelf(textArea.getValue());
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
}
