package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.entity.Visit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

public class VisitDiv extends Div {

    public VisitDiv() {
        Grid<Visit> activeGrid = new Grid<>(Visit.class, false);
        activeGrid.addColumn(Visit::getId).setHeader("№");
        activeGrid.addColumn(Visit::getReason).setHeader("Reason");
        activeGrid.addColumn(Visit::getDoctor).setHeader("Doctor");
        activeGrid.addColumn(Visit::getDatetime).setHeader("Date and Time");
        activeGrid.setAllRowsVisible(true);
        setGridStyles(activeGrid);
        Grid<Visit> archiveGrid = new Grid<>(Visit.class, false);
        archiveGrid.addColumn(Visit::getId).setHeader("№");
        archiveGrid.addColumn(Visit::getReason).setHeader("Reason");
        archiveGrid.addColumn(Visit::getDoctor).setHeader("Doctor");
        archiveGrid.addColumn(Visit::getDatetime).setHeader("Date and Time");
        archiveGrid.setAllRowsVisible(true);
        setGridStyles(archiveGrid);
        Div container = new Div(activeGrid,archiveGrid);
        setContainerStyles(container);
        add(container);
    }

    private void setContainerStyles(Div container) {
        container.getStyle().set("display", "flex").set("flex-direction", "row")
                .set("flex-wrap", "wrap");
    }

    private void setGridStyles(Grid<Visit> grid) {
        grid.getStyle()
                .set("width", "550px").set("height", "100%")
                .set("margin-left", "0.5rem").set("margin-top", "0.5rem")
                .set("align-self", "unset");
    }

}
