package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "notes", layout = MainLayout.class)
@PageTitle("Notes")
public class NoteView extends VerticalLayout {

    public NoteView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }
}