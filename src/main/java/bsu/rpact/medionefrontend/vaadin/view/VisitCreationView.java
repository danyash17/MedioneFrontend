package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.entity.Speciality;
import bsu.rpact.medionefrontend.enums.SpecialityName;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.SpecialityService;
import bsu.rpact.medionefrontend.vaadin.DoctorButton;
import bsu.rpact.medionefrontend.vaadin.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "newvisit", layout = MainLayout.class)
@PageTitle("Create Visit")
public class VisitCreationView extends VerticalLayout {

    private final SpecialityService specialityService;
    private final DoctorService doctorService;

    public VisitCreationView(SpecialityService specialityService, DoctorService doctorService) {
        this.specialityService = specialityService;
        this.doctorService = doctorService;
        add(new H2("New visit"));
        add(new H3("Step 1 : Choose competence areas"));
        List<DoctorButton> doctorButtons = new ArrayList<>();
        Div div = createButtons(this.specialityService.getAllSpecialities(), doctorButtons);
        add(div);
        Button search = new Button("Search doctors");
        search.addClickListener(e -> {
            List<SpecialityName> selectedButtons = doctorButtons.stream().
                    filter(button -> button.isSelected()).map(button -> button.getSpecialityName()).
                    collect(Collectors.toList());
            List<Doctor> doctorList = doctorService.getProperDoctors(selectedButtons);
        });
        HorizontalLayout layout = new HorizontalLayout(search);
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        add(layout);
    }

    private Div createButtons(List<Speciality> specialityList, List<DoctorButton> buttonList) {
        Div div = new Div();
        div.setMaxWidth("1000px");
        for (Speciality speciality : specialityList) {
            DoctorButton button = new DoctorButton(speciality.getDescription());
            button.setSpecialityName(SpecialityName.valueOf(speciality.getDescription()));
            setupButton(button);
            div.add(button);
            buttonList.add(button);
        }
        return div;
    }

    private void setupButton(DoctorButton button) {
        button.getStyle()
                .set("margin-right", "0.5rem");
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.setDisableOnClick(false);
        button.addClickListener(e -> {
            button.setSelected(!button.isSelected());
            if (button.isSelected()) {
                button.removeThemeVariants(ButtonVariant.LUMO_TERTIARY);
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            }
        });
    }
}
