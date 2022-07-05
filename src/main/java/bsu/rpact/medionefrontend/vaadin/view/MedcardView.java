package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Illness;
import bsu.rpact.medionefrontend.entity.Medcard;
import bsu.rpact.medionefrontend.entity.Operation;
import bsu.rpact.medionefrontend.service.MedcardService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "medcard", layout = MainLayout.class)
@PageTitle("Medcard")
public class MedcardView extends VerticalLayout {

    private final SessionManager sessionManager;
    private final MedcardService medcardService;

    public MedcardView(SessionManager sessionManager, MedcardService medcardService) {
        this.sessionManager = sessionManager;
        this.medcardService = medcardService;
        Medcard medcard = medcardService.getSelf();
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
}
