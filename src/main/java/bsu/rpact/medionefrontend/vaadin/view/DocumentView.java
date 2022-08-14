package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "documents", layout = MainLayout.class)
@PageTitle("Documents")
public class DocumentView extends VerticalLayout {

    public DocumentView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }
}