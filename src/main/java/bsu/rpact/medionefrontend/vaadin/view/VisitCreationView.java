package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.entity.Speciality;
import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.enums.SpecialityName;
import bsu.rpact.medionefrontend.pojo.PatientVisitPojo;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.other.DoctorPhotoUrlContainer;
import bsu.rpact.medionefrontend.service.*;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.vaadin.components.DoctorButton;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.addons.badge.Badge;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Route(value = "newvisit", layout = MainLayout.class)
@PageTitle("Create Visit")
public class VisitCreationView extends VerticalLayout implements LocaleChangeObserver {

    private final SpecialityService specialityService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;
    private final VisitService visitService;
    private final PatientService patientService;
    private final Grid<DoctorPhotoUrlContainer> grid;
    private H2 newVisit = new H2(getTranslation("visits.new_visit"));
    private H3 stepOneChooseCompetenceAreasAndDoctor = new H3(getTranslation("visits.step_one_choose_competence_areas_and_doctor"));
    private H3 stepThreeFormYourPrescriptVisitReason = new H3(getTranslation("visits.step_three_form_your_prescription_visit_reason"));
    private Button confirmButton = new Button(getTranslation("visits.confirm"));
    private TextArea visitReasonTextArea = new TextArea(getTranslation("visits.reason"));
    private String doctor = getTranslation("visits.doctor");
    private String doctorDDotSpace = getTranslation("visits.doctor_ddot_space");
    private String reasonDDotSpace = getTranslation("visits.reason_ddot_space");
    private H3 areYouSureYouWantToCreateVisitWithThisProperties = new H3(getTranslation("visits.are_you_sure_you_want_to_create_visit_with_these_properties"));
    private H3 stepTwoChooseApplicableDateAndTime = new H3(getTranslation("visits.step_two_choose_applicable_date_and_time"));
    private String appointmentDateAndTime = getTranslation("visits.appointment_date_and_time");
    private DateTimePicker dateTimePicker = new DateTimePicker();
    private String helperText = getTranslation("visits.pattern");
    private String selectedDayOfWeekIsNotAvailable = getTranslation("visits.selected_day_of_week_is_not_available");
    private String thisDatetimeIsNotAvailableForBooking = getTranslation("visits.this_date_time_is_not_available_for_booking");
    private String photo = getTranslation("visits.photo");
    private String available = getTranslation("profile.available");
    private String busy = getTranslation("profile.unreachable");
    private String status = getTranslation("visits.status");
    private Button searchDoctors = new Button(getTranslation("visits.search_doctors"));
    private String speciality = getTranslation("visits.speciality");
    private String instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
    private String workExperience = getTranslation("profile.work_experience");
    private String forExtraInformationClickToggleDetails = getTranslation("visits.for_extra_information_click_toggle_details");
    private String toggleDetails = getTranslation("visits.toggle_details");
    private String credentials = getTranslation("visits.credentials");
    private Span specialites = new Span(getTranslation("visits.specialities"));
    private String hospital = getTranslation("profile.current_hospital");
    private Button cancelButton = new Button(getTranslation("visits.cancel"));

    public VisitCreationView(SpecialityService specialityService, DoctorSpecialityService doctorSpecialityService, DoctorService doctorService, ImageUtils imageUtils, VisitService visitService, PatientService patientService) {
        this.specialityService = specialityService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        this.visitService = visitService;
        this.patientService = patientService;
        add(newVisit);
        add(stepOneChooseCompetenceAreasAndDoctor);
        List<DoctorButton> doctorButtons = new ArrayList<>();
        Div div = createButtons(this.specialityService.getAllSpecialities(), doctorButtons);
        add(div);
        grid = new Grid<>(DoctorPhotoUrlContainer.class, false);
        VerticalLayout datetimeLayout = new VerticalLayout();
        VerticalLayout reasonLayout = new VerticalLayout();
        AtomicReference<Doctor> doctorAtomicReference = new AtomicReference<>();
        Visit visit = new Visit();
        setupGrid(grid, datetimeLayout, doctorAtomicReference, visit);
        setupDatetimeLayout(datetimeLayout, reasonLayout, doctorAtomicReference, visit);
        setupReasonLayout(reasonLayout, visit);
        HorizontalLayout searchLayout = getSearchLayout(doctorService, doctorButtons, grid);
        add(searchLayout);
        add(grid);
        add(datetimeLayout);
        add(reasonLayout);
    }

    private void setupReasonLayout(VerticalLayout reasonLayout, Visit visit) {
        reasonLayout.setVisible(false);
        reasonLayout.add(stepThreeFormYourPrescriptVisitReason);
        confirmButton.setEnabled(false);
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        visitReasonTextArea = new TextArea();
        visitReasonTextArea.setWidthFull();
        Paragraph dialogPropsParagraph = new Paragraph();
        visitReasonTextArea.addValueChangeListener(e -> {
            if (!e.getSource().isEmpty()) {
                visit.setReason(e.getValue());
                doctor = doctorDDotSpace;
                String properties = doctor + visit.getDoctor().getCredentials().getFirstName() + " " +
                        visit.getDoctor().getCredentials().getPatronymic() + " " +
                        visit.getDoctor().getCredentials().getLastName() + "; " +
                        "Date and time: " + visit.getDatetime().toLocalDateTime().format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                                .withLocale(Locale.ROOT)) +
                        " " + visit.getDatetime().toLocalDateTime().toLocalTime() + "; " +
                        reasonDDotSpace + visit.getReason();
                dialogPropsParagraph.setText(properties);
                confirmButton.setEnabled(true);
            } else {
                confirmButton.setEnabled(false);
            }
        });
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        confirmButton.addClickListener(buttonClickEvent -> dialog.open());
        dialog.add(areYouSureYouWantToCreateVisitWithThisProperties);
        dialog.add(dialogPropsParagraph);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        cancelButton.addClickListener(e -> dialog.close());
        cancelButton.getStyle().set("margin-right", "auto");
        Button okButton = new Button("OK", (e) -> dialog.close());
        okButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        okButton.addClickListener(e -> {
            createVisit(visit);
            UiUtils.generateSuccessNotification(getTranslation("visit.visit_created_successfully"));
            UI.getCurrent().navigate(VisitViewPatient.class);
        });
        reasonLayout.add(visitReasonTextArea);
        reasonLayout.add(confirmButton);
        buttonLayout.add(cancelButton, okButton);
        dialog.add(buttonLayout);
    }

    private void createVisit(Visit visit) {
        PatientVisitPojo visitPojo = new PatientVisitPojo();
        visitPojo.setDatetime(visit.getDatetime());
        visitPojo.setActive(Boolean.TRUE);
        visitPojo.setComments("");
        visitPojo.setDiagnosis("");
        visitPojo.setReason(visit.getReason());
        visitPojo.setDoctorId(visit.getDoctor().getId());
        visitService.createVisitBySelf(visitPojo);
    }

    private void setupDatetimeLayout(VerticalLayout datetimeLayout, VerticalLayout reasonLayout, AtomicReference<Doctor> doctorAtomicReference, Visit visit) {
        datetimeLayout.add(stepTwoChooseApplicableDateAndTime);
        setupDateTimePicker(dateTimePicker, visit, doctorAtomicReference, reasonLayout);
        datetimeLayout.add(dateTimePicker);
        datetimeLayout.setVisible(false);
    }

    private void setupDateTimePicker(DateTimePicker dateTimePicker, Visit visit, AtomicReference<Doctor> doctorReference, VerticalLayout reasonLayout) {
        dateTimePicker.setLabel(appointmentDateAndTime);
        dateTimePicker.setStep(Duration.ofMinutes(30));
        dateTimePicker.setHelperText(helperText);
        dateTimePicker.setAutoOpen(true);
        LocalDateTime now = LocalDateTime.now().minusMinutes(LocalDateTime.now().getMinute());
        dateTimePicker.setMin(now);
        dateTimePicker.setMax(now.plusDays(60));
        dateTimePicker.setValue(now.plusDays(120));
        final Binder<Visit> binder = new Binder<>(Visit.class);
        binder.forField(dateTimePicker).withValidator(startDateTime -> {
            boolean validWeekDay = startDateTime.getDayOfWeek().getValue() >= 1
                    && startDateTime.getDayOfWeek().getValue() <= 5;
            return validWeekDay;
        }, selectedDayOfWeekIsNotAvailable).withValidator(startDateTime -> {
            LocalTime valueTime = LocalTime.of(startDateTime.getHour(), startDateTime.getMinute());
            List<Visit> visitList = visitService.getAllVisitsOfDoctor(doctorReference.get().getId());
            boolean validDaySchedule = !(LocalTime.of(8, 0).isAfter(valueTime)
                    || (LocalTime.of(12, 0).isBefore(valueTime) && LocalTime.of(13, 0).isAfter(valueTime))
                    || LocalTime.of(16, 0).isBefore(valueTime));
            boolean validVisitSchedule = visitList.stream().allMatch(currentVisit ->
                    !currentVisit.getDatetime().toLocalDateTime().toLocalTime().equals(valueTime));
            return validDaySchedule && validVisitSchedule;
        }, thisDatetimeIsNotAvailableForBooking).bind(
                valueProvider -> valueProvider.getDatetime().toLocalDateTime(),
                (Visit vis, LocalDateTime dt) -> {
                    vis.setDatetime(Timestamp.valueOf(dt));
                });
        dateTimePicker.addValueChangeListener(e -> {
            if (binder.validate().isOk()) {
                visit.setDatetime(Timestamp.valueOf(e.getValue()));
                reasonLayout.setVisible(true);
            } else {
                reasonLayout.setVisible(false);
            }
        });
    }

    private void setupGrid(Grid<DoctorPhotoUrlContainer> grid, VerticalLayout datetimeLayout, AtomicReference<Doctor> selectedDoctor, Visit visit) {
        grid.addColumn(createAvatarRenderer()).setHeader(photo)
                .setAutoWidth(true).setKey("photo").setFlexGrow(0);
        grid.addColumn(container -> {
            return container.getDoctor().getCredentials().getFirstName() + " " +
                    container.getDoctor().getCredentials().getPatronymic() + " " +
                    container.getDoctor().getCredentials().getLastName();
        }).setHeader(credentials).setKey("credentials").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(container -> StringUtils.join(container.getDoctor().getSpecialityList(), ',')).
                setHeader(createSpecialityHeader()).setAutoWidth(true);
        grid.addComponentColumn(container -> {
            if(container.getDoctor().getAvailable()){
                Badge badge = new Badge(available);
                badge.setVariant(Badge.BadgeVariant.SUCCESS);
                badge.setPrimary(true);
                badge.setPill(true);
                badge.setIcon(VaadinIcon.CHECK_CIRCLE.create());
                return badge;
            }
            else {
                Badge badge = new Badge(busy);
                badge.setVariant(Badge.BadgeVariant.ERROR);
                badge.setPrimary(true);
                badge.setPill(true);
                badge.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
                return badge;
            }
        }).setHeader(status).setKey("status").setAutoWidth(true);
        grid.addComponentColumn(container -> {
            Button toggleButton = new Button();
            toggleButton.setText(toggleDetails);
            toggleButton.addClickListener(e -> {
                Dialog dialog = new Dialog();
                dialog.setCloseOnEsc(true);
                VerticalLayout dialogLayout = createDialogLayout(container);
                dialog.add(dialogLayout);
                dialog.open();
            });
            return toggleButton;
        }).setKey("toggle").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent() && e.getFirstSelectedItem().get().getDoctor().getAvailable()) {
                datetimeLayout.setVisible(true);
                selectedDoctor.set(e.getFirstSelectedItem().get().getDoctor());
                visit.setDoctor(e.getFirstSelectedItem().get().getDoctor());
            } else {
                datetimeLayout.setVisible(false);
                UiUtils.generateErrorNotification(getTranslation("visits.the_doctor_is_busy_select_another")).open();
            }
        });
        grid.setAllRowsVisible(true);
    }

    private HorizontalLayout getSearchLayout(DoctorService doctorService, List<DoctorButton> doctorButtons, Grid<DoctorPhotoUrlContainer> grid) {
        searchDoctors.addClickListener(e -> {
            List<SpecialityName> selectedButtons = doctorButtons.stream().
                    filter(button -> button.isSelected()).map(button -> button.getSpecialityName()).
                    collect(Collectors.toList());
            List<Doctor> doctorList = doctorService.getProperDoctors(selectedButtons).stream().
                    filter(doctor -> doctor.getVisitSchedule()!=null).collect(Collectors.toList());
            List<DoctorPhotoUrlContainer> doctorPhotoUrlContainers = doctorList.stream().
                    map(doctor -> new DoctorPhotoUrlContainer
                            (doctor, imageUtils.chacheByteArrToImageDoctor(doctor.getDoctorPhoto(),
                                    doctor.getCredentials().getFirstName() +
                                            doctor.getCredentials().getPatronymic() +
                                            doctor.getCredentials().getLastName()
                            ))).
                    collect(Collectors.toList());
            grid.setItems(doctorPhotoUrlContainers);
        });
        HorizontalLayout layout = new HorizontalLayout(searchDoctors);
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
        layout.add(new Paragraph(hospital + ": " + container.getDoctor().getHospital()));
        layout.add(new H4(container.getDoctor().getCommonInfo()));
        Grid<RepresentativeDoctorSpecialityPojo> grid = new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
        grid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader(speciality);
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader(instituteOfAccreditation);
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader(workExperience);
        grid.setItems(doctorSpecialityService.getDoctorSpecialities(container.getDoctor().getId()));
        grid.setAllRowsVisible(true);
        layout.add(grid);
        return layout;
    }

    private Component createSpecialityHeader() {
        Icon icon = VaadinIcon.INFO_CIRCLE.create();
        icon.getElement()
                .setAttribute("title", forExtraInformationClickToggleDetails);
        icon.getStyle().set("height", "var(--lumo-font-size-m)")
                .set("color", "var(--lumo-contrast-70pct)");

        HorizontalLayout layout = new HorizontalLayout(specialites, icon);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);

        return layout;
    }


    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        status = getTranslation("visits.status");
        newVisit.setText(getTranslation("visits.new_visit"));
        stepOneChooseCompetenceAreasAndDoctor.setText(getTranslation("visits.step_one_choose_competence_areas_and_doctor"));
        stepThreeFormYourPrescriptVisitReason.setText(getTranslation("visits.step_three_form_your_prescription_visit_reason"));
        confirmButton.setText(getTranslation("visits.confirm"));
        visitReasonTextArea.setLabel(getTranslation("visits.reason"));
        doctor = getTranslation("visits.doctor");
        doctorDDotSpace = getTranslation("visits.doctor_ddot_space");
        reasonDDotSpace = getTranslation("visits.reason_ddot_space");
        areYouSureYouWantToCreateVisitWithThisProperties.setText(getTranslation("visits.are_you_sure_you_want_to_create_visit_with_these_properties"));
        stepTwoChooseApplicableDateAndTime.setText(getTranslation("visits.step_two_choose_applicable_date_and_time"));
        appointmentDateAndTime = getTranslation("visits.appointment_date_and_time");
        helperText = getTranslation("visits.pattern");
        selectedDayOfWeekIsNotAvailable = getTranslation("visits.selected_day_of_week_is_not_available");
        thisDatetimeIsNotAvailableForBooking = getTranslation("visits.this_date_time_is_not_available_for_booking");
        photo = getTranslation("visits.photo");
        available = getTranslation("profile.available");
        busy = getTranslation("profile.unreachable");
        searchDoctors.setText(getTranslation("visits.search_doctors"));
        speciality = getTranslation("visits.speciality");
        instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
        workExperience = getTranslation("profile.work_experience");
        forExtraInformationClickToggleDetails = getTranslation("visits.for_extra_information_click_toggle_details");
        toggleDetails = getTranslation("visits.toggle_details");
        credentials = getTranslation("visits.credentials");
        specialites.setText(getTranslation("visits.specialities"));
        hospital = getTranslation("profile.current_hospital");
        cancelButton.setText(getTranslation("visits.cancel"));

        dateTimePicker.setLabel(appointmentDateAndTime);
        dateTimePicker.setHelperText(helperText);
        grid.getColumnByKey("photo").setHeader(photo);
        grid.getColumnByKey("credentials").setHeader(credentials);
        grid.getColumnByKey("status").setHeader(status);
        grid.getListDataView().refreshAll();
    }
}
