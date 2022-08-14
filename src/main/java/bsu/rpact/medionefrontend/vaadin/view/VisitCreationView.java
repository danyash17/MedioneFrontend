package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.entity.Speciality;
import bsu.rpact.medionefrontend.enums.SpecialityName;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.other.DoctorPhotoUrlContainer;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.DoctorSpecialityService;
import bsu.rpact.medionefrontend.service.SpecialityService;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.vaadin.components.DoctorButton;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "newvisit", layout = MainLayout.class)
@PageTitle("Create Visit")
public class VisitCreationView extends VerticalLayout {

    private final SpecialityService specialityService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;

    public VisitCreationView(SpecialityService specialityService, DoctorSpecialityService doctorSpecialityService, DoctorService doctorService, ImageUtils imageUtils) {
        this.specialityService = specialityService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        add(new H2("New visit"));
        add(new H3("Step 1 : Choose competence areas"));
        List<DoctorButton> doctorButtons = new ArrayList<>();
        Div div = createButtons(this.specialityService.getAllSpecialities(), doctorButtons);
        add(div);

        Grid<DoctorPhotoUrlContainer> grid = new Grid<>(DoctorPhotoUrlContainer.class, false);
        setupGrid(grid);

        HorizontalLayout searchLayout = getSearchLayout(doctorService, doctorButtons, grid);
        add(searchLayout);
        add(grid);

        add();
    }

    private void setupGrid(Grid<DoctorPhotoUrlContainer> grid) {
        grid.addColumn(createAvatarRenderer()).setHeader("Photo")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(container -> {
                    return container.getDoctor().getCredentials().getFirstName() + " " +
                            container.getDoctor().getCredentials().getPatronymic() + " " +
                            container.getDoctor().getCredentials().getLastName();
                }).setHeader("Credentials").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(container -> StringUtils.join(container.getDoctor().getSpecialityList(), ',')).
                setHeader(createSpecialityHeader()).setAutoWidth(true);
        grid.addColumn(createStatusComponentRenderer()).setHeader("Status").setAutoWidth(true);
        grid.addColumn(createSelectRenderer(grid)).setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setAllRowsVisible(true);
    }

    private ComponentRenderer<Span, DoctorPhotoUrlContainer> createStatusComponentRenderer() {
        final SerializableBiConsumer<Span, DoctorPhotoUrlContainer> statusComponentUpdater = (span, container) -> {
            boolean isAvailable = container.getDoctor().getAvailable();
            String theme = String
                    .format("badge %s", isAvailable ? "success" : "error");
            span.getElement().setAttribute("theme", theme);
            span.setText(container.getDoctor().getAvailable() ? "Available" : "Busy");
        };
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }

    private HorizontalLayout getSearchLayout(DoctorService doctorService, List<DoctorButton> doctorButtons, Grid<DoctorPhotoUrlContainer> grid) {
        Button search = new Button("Search doctors");
        search.addClickListener(e -> {
            List<SpecialityName> selectedButtons = doctorButtons.stream().
                    filter(button -> button.isSelected()).map(button -> button.getSpecialityName()).
                    collect(Collectors.toList());
            List<Doctor> doctorList = doctorService.getProperDoctors(selectedButtons);
            List<DoctorPhotoUrlContainer> doctorPhotoUrlContainers = doctorList.stream().
                    map(doctor -> new DoctorPhotoUrlContainer
                            (doctor, imageUtils.chacheByteArrToImage(doctor.getDoctorPhoto(),
                                    doctor.getCredentials().getFirstName() +
                                            doctor.getCredentials().getPatronymic() +
                                            doctor.getCredentials().getLastName()
                            ))).
                    collect(Collectors.toList());
            grid.setItems(doctorPhotoUrlContainers);
        });
        HorizontalLayout layout = new HorizontalLayout(search);
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setWidthFull();
        return layout;
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

    private Renderer<DoctorPhotoUrlContainer> createAvatarRenderer() {
        return LitRenderer.<DoctorPhotoUrlContainer>of(
                        "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.lastName}\" title=\"${item.title}\" theme=\"large\" alt=\"User avatar\"></vaadin-avatar>")
                .withProperty("pictureUrl", DoctorPhotoUrlContainer::getPhotoUrl)
                .withProperty("lastName", doctorPhotoUrlContainer -> doctorPhotoUrlContainer.getDoctor().getCredentials().getLastName());
    }

    private Renderer<DoctorPhotoUrlContainer> createSelectRenderer(
            Grid<DoctorPhotoUrlContainer> grid) {
        return LitRenderer.<DoctorPhotoUrlContainer>of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Toggle details</vaadin-button>")
                .withFunction("handleClick", container -> {
                    Dialog dialog = new Dialog();
                    dialog.setCloseOnEsc(true);
                    VerticalLayout dialogLayout = createDialogLayout(container);
                    dialog.add(dialogLayout);
                    dialog.open();
                });
    }

    private VerticalLayout createDialogLayout(DoctorPhotoUrlContainer container) {
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        Image image = new Image(container.getPhotoUrl(), "");
        image.setMaxWidth("530px");
        image.setMaxHeight("630px");
        layout.setMaxWidth("700px");
        layout.add(image);
        layout.add(new H2(container.getDoctor().getCredentials().getFirstName() + " " +
                container.getDoctor().getCredentials().getPatronymic() + " " +
                container.getDoctor().getCredentials().getLastName()));
        layout.add(new Paragraph("Current hospital: " + container.getDoctor().getHospital()));
        layout.add(new H4(container.getDoctor().getCommonInfo()));
        Grid<RepresentativeDoctorSpecialityPojo> grid = new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
        grid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader("Speciality");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader("Institute of Accreditation");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader("Work Experience");
        grid.setItems(doctorSpecialityService.getDoctorSpecialities(container.getDoctor().getId()));
        grid.setAllRowsVisible(true);
        layout.add(grid);
        return layout;
    }

    private Component createSpecialityHeader() {
        Span span = new Span("Specialities");
        Icon icon = VaadinIcon.INFO_CIRCLE.create();
        icon.getElement()
                .setAttribute("title", "For extra information click the Toggle Details");
        icon.getStyle().set("height", "var(--lumo-font-size-m)")
                .set("color", "var(--lumo-contrast-70pct)");

        HorizontalLayout layout = new HorizontalLayout(span, icon);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);

        return layout;
    }


}
