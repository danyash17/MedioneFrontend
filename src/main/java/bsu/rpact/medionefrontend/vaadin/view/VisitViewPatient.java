package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.other.DoctorPhotoUrlContainer;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.DoctorSpecialityService;
import bsu.rpact.medionefrontend.service.PatientService;
import bsu.rpact.medionefrontend.service.VisitService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.addons.badge.Badge;
import org.vaadin.klaudeta.PaginatedGrid;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "visitPatient", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitViewPatient extends VerticalLayout implements LocaleChangeObserver {

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", VaadinSession.getCurrent().getLocale());
    private final SessionManager sessionManager;
    private final VisitService visitService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final ImageUtils imageUtils;
    private final PatientService patientService;
    private final H3 visitScheduleNotSetYet = new H3(getTranslation("visits.schedule_not_set"));
    private final H4 doYouWantToCreateNewOne = new H4(getTranslation("visits.do_you_want_to_create_new_one"));
    private final Button buttonCreate = new Button(getTranslation("visits.create"));
    private final Paragraph doYouWantToDoAVisitReservation = new Paragraph(getTranslation("visits.do_you_want_to_do_a_visit_reservation"));
    private final H2 visits = new H2(getTranslation("visits.visits"));
    private final Button newVisit = new Button(getTranslation("visits.new_visit"));
    private String reason = getTranslation("visits.reason");
    private String doctor = getTranslation("visits.doctor");
    private String dateAndTime = getTranslation("visits.datetime");
    private String impending = getTranslation("visits.impending");
    private String completed = getTranslation("visits.completed");
    private String edit = getTranslation("visits.edit");
    private String reasonMustNotBeEmpty = getTranslation("visits.reason_must_not_be_empty");
    private String dateAndTimeMustBeNotEmpty = getTranslation("visits.date_time_must_not_be_empty");
    private String save = getTranslation("visits.save");
    private String finalDiagnosis = getTranslation("visits.final_diagnosis");
    private String comments = getTranslation("visits.comments");
    private String areYouSureYouWantToSaveThisVisit = getTranslation("visits.are_you_sure_you_want_to_save_this_visit");
    private String cancel = getTranslation("visits.cancel");
    private String areYouSureYouWantToDeleteThisVisit = getTranslation("visits.are_you_sure_you_want_to_delete_this_visit");
    private String delete = getTranslation("visits.delete");
    private String selectedDayOfWeekIsNotAvailable = getTranslation("visits.selected_day_of_week_is_not_available");
    private String thisDateTimeIsNotAvailableForBooking = getTranslation("visits.this_date_time_is_not_available_for_booking");
    private String visitEnquiry = getTranslation("visits.visit_enquiry");
    private String bookingTime = getTranslation("visits.booking_time");
    private String visitReason = getTranslation("visits.visit_reason");
    private String aboutDoctor = getTranslation("visits.about_doctor");
    private String currentHospital = getTranslation("profile.current_hospital");
    private String instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
    private String workExperience = getTranslation("profile.work_experience");
    private String speciality = getTranslation("profile.speciality");
    private Grid<RepresentativeDoctorSpecialityPojo> doctorSpecialityGrid = new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
    private final PaginatedGrid<Visit> visitGrid = new PaginatedGrid<>();
    private Binder<Visit> binder;

    public VisitViewPatient(SessionManager sessionManager, VisitService visitService, DoctorSpecialityService doctorSpecialityService, ImageUtils imageUtils, DoctorService doctorService, PatientService patientService) {
        this.sessionManager = sessionManager;
        this.visitService = visitService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.imageUtils = imageUtils;
        this.patientService = patientService;
        Optional<Patient> patient = patientService.getSelf();
        if(patient.isPresent() && patient.get().getVisitSchedule()==null){
            add(visitScheduleNotSetYet);
            add(doYouWantToCreateNewOne);
            buttonCreate.addClickListener(e -> {
                visitService.createVisitScheduleBySelf();
                UI.getCurrent().getPage().reload();
            });
            add(buttonCreate);
            return;
        }
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        add(visits);
        setupGrid(visitGrid);
        add(visitGrid);
        newVisit.addClickListener(e -> {
            UI.getCurrent().navigate(VisitCreationView.class);
        });
        add(doYouWantToDoAVisitReservation, newVisit);
    }

    private void doGridInit(Grid<Visit> grid) {
        Editor<Visit> editor = grid.getEditor();
        Grid.Column<Visit> reasonColumn = grid.addColumn(Visit::getReason).setHeader(reason).setKey("reason").setTextAlign(ColumnTextAlign.START);
        grid.addColumn(createEmployeeRenderer()).setHeader(doctor).setKey("doctor")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.START);
        Grid.Column<Visit> dtColumn = grid.addColumn(visit -> {
            return DATE_FORMAT.format(visit.getDatetime());
        }).setHeader(dateAndTime).setKey("datetime").setTextAlign(ColumnTextAlign.START);
        grid.addComponentColumn(visit -> {
            if(visit.getActive()){
                Badge badge = new Badge(impending);
                badge.setVariant(Badge.BadgeVariant.NORMAL);
                badge.setPrimary(true);
                badge.setPill(true);
                badge.setIcon(VaadinIcon.CLOCK.create());
                return badge;
            }
            else {
                Badge badge = new Badge(completed);
                badge.setVariant(Badge.BadgeVariant.SUCCESS);
                badge.setPrimary(true);
                badge.setPill(true);
                badge.setIcon(VaadinIcon.CHECK.create());
                return badge;
            }
        }).setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        AtomicReference<Doctor> atomicDoctorReference = new AtomicReference<>();
        AtomicReference<Visit> atomicVisitReference = new AtomicReference<>();
        Grid.Column<Visit> editColumn = grid.addComponentColumn(visit -> {
            Button editButton = new Button(edit);
            editButton.setEnabled(visit.getActive());
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                atomicDoctorReference.set(visit.getDoctor());
                atomicVisitReference.set(visit);
                grid.getEditor().editItem(visit);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);
        grid.setAllRowsVisible(true);

        binder = new Binder<>(Visit.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        TextField reasonField = new TextField();
        reasonField.setWidthFull();
        binder.forField(reasonField)
                .asRequired(reasonMustNotBeEmpty)
                .bind(Visit::getReason, Visit::setReason);
        reasonColumn.setEditorComponent(reasonField);

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setWidthFull();
        binder.forField(dateTimePicker).asRequired(dateAndTimeMustBeNotEmpty)
                .bind(valueProvider -> valueProvider.getDatetime().toLocalDateTime(),
                        (Visit visit, LocalDateTime dt) -> {
                            visit.setDatetime(Timestamp.valueOf(dt));
                        });
        setupDateTimePicker(dateTimePicker, atomicDoctorReference);
        dtColumn.setEditorComponent(dateTimePicker);

        Button saveButton = new Button("OK", e -> {
            getSaveConfirmationDialog(editor, atomicVisitReference.get()).open();
        });
        Button cancelButton = new Button(VaadinIcon.TRASH.create(), e -> {
            getDeleteConfirmationDialog(editor,atomicVisitReference).open();
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        grid.addItemDoubleClickListener(e -> {
            setupEnquiryDialog(e);
        });
    }

    private void setupEnquiryDialog(ItemDoubleClickEvent<Visit> e) {
        Dialog dialog = new Dialog();
        Visit visit = e.getItem();
        Doctor doctor = visit.getDoctor();
        DoctorPhotoUrlContainer doctorPhotoUrlContainer = new DoctorPhotoUrlContainer();
        initContainer(visit, doctor, doctorPhotoUrlContainer);
        dialog.add(createDialogLayout(doctorPhotoUrlContainer, dialog, visit));
        dialog.setCloseOnEsc(true);
        dialog.setWidth("800px");
        dialog.open();
    }

    private void initContainer(Visit visit, Doctor doctor, DoctorPhotoUrlContainer doctorPhotoUrlContainer) {
        doctorPhotoUrlContainer.setDoctor(visit.getDoctor());
        String fullName = doctor.getCredentials().getFirstName() +
                doctor.getCredentials().getPatronymic() +
                doctor.getCredentials().getLastName();
        doctorPhotoUrlContainer.setPhotoUrl(imageUtils.chacheByteArrToImageDoctor(visit.getDoctor().getDoctorPhoto(), fullName));
    }

    private void setupVisitResults(VerticalLayout layout, Visit visit) {
        H3 diagnosis = new H3(finalDiagnosis);
        Label diagLabel = new Label(visit.getDiagnosis());
        layout.add(diagnosis, diagLabel);
        H3 comments = new H3(this.comments);
        Label commentsLabel = new Label(visit.getComments());
        layout.add(comments, commentsLabel);
    }

    private Dialog getSaveConfirmationDialog(Editor<Visit> editor, Visit visit) {
        Dialog dialog = new Dialog();
        dialog.add(new H3(areYouSureYouWantToSaveThisVisit));
        dialog.setCloseOnEsc(true);
        Button cancelButton = new Button(cancel, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button saveButton = new Button(save, (e) -> {
            dialog.close();
            editor.save();
            visitService.update(visit);
            UI.getCurrent().navigate(VisitViewPatient.class);
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.add(new HorizontalLayout(cancelButton, saveButton));
        return dialog;
    }

    private Dialog getDeleteConfirmationDialog(Editor<Visit> editor, AtomicReference<Visit> visitAtomicReference) {
        Dialog dialog = new Dialog();
        dialog.add(new H3(areYouSureYouWantToDeleteThisVisit));
        dialog.setCloseOnEsc(true);
        Button cancelButton = new Button(cancel, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button deleteButton = new Button(delete, (e) -> {
            visitService.deleteVisitBySelf(visitAtomicReference.get().getId());
            visitGrid.getListDataView().removeItem(visitAtomicReference.get());
            dialog.close();
            editor.cancel();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        dialog.add(new HorizontalLayout(cancelButton, deleteButton));
        return dialog;
    }

    private void setupGrid(PaginatedGrid<Visit> activeGrid) {
        doGridInit(activeGrid);
        List<Visit> visitList = visitService.getAllMyVisitsSelf();
        visitList.sort(new Comparator<Visit>() {
            @Override
            public int compare(Visit o1, Visit o2) {
                return o1.getDatetime().toLocalDateTime().compareTo(o2.getDatetime().toLocalDateTime());
            }
        });
        activeGrid.setItems(visitList);
        activeGrid.setPageSize(10);
        activeGrid.setPaginatorSize(5);
    }

    private Renderer<Visit> createEmployeeRenderer() {
        return LitRenderer.<Visit>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.fullName} </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("pictureUrl", visit -> {
                    Doctor doctor = visit.getDoctor();
                    String fullName = doctor.getCredentials().getFirstName() +
                            doctor.getCredentials().getPatronymic() +
                            doctor.getCredentials().getLastName();
                    return imageUtils.chacheByteArrToImageDoctor(doctor.getDoctorPhoto(), fullName);
                })
                .withProperty("fullName", visit -> {
                    Doctor doctor = visit.getDoctor();
                    return doctor.getCredentials().getFirstName() + " " +
                            doctor.getCredentials().getPatronymic() + " " +
                            doctor.getCredentials().getLastName();
                });
    }

    private void setupDateTimePicker(DateTimePicker dateTimePicker, AtomicReference<Doctor> doctorReference) {
        dateTimePicker.setStep(Duration.ofMinutes(30));
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
        }, thisDateTimeIsNotAvailableForBooking).bind(
                valueProvider -> valueProvider.getDatetime().toLocalDateTime(),
                (Visit vis, LocalDateTime dt) -> {
                    vis.setDatetime(Timestamp.valueOf(dt));
                });
        dateTimePicker.addValueChangeListener(e -> {
            if (binder.validate().isOk()) {

            }
        });
    }

    private VerticalLayout createDialogLayout(DoctorPhotoUrlContainer container, Dialog dialog, Visit visit) {
        VerticalLayout layout = new VerticalLayout();
        doctorSpecialityGrid = new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
        layout.add(new H1(visitEnquiry));
        layout.add(new Label(bookingTime + DATE_FORMAT.format(visit.getDatetime())));
        layout.add(new Label(visitReason + visit.getReason()));
        if (!visit.getActive()) {
            setupVisitResults(layout, visit);
        }
        VerticalLayout doctorLayout = new VerticalLayout();
        setupDoctorSection(container, doctorLayout);
        Details details = new Details(aboutDoctor, doctorLayout);
        details.setOpened(false);
        layout.add(details);
        return layout;
    }

    private void setupDoctorSection(DoctorPhotoUrlContainer container, VerticalLayout layout) {
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        Image image = new Image(container.getPhotoUrl(), "");
        image.setMaxWidth("530px");
        image.setMaxHeight("630px");
        layout.setMaxWidth("700px");
        layout.add(image);
        layout.add(new H2(container.getDoctor().getCredentials().getFirstName() + " " +
                container.getDoctor().getCredentials().getPatronymic() + " " +
                container.getDoctor().getCredentials().getLastName()));
        layout.add(new Paragraph(currentHospital + container.getDoctor().getHospital()));
        layout.add(new H4(container.getDoctor().getCommonInfo()));
        doctorSpecialityGrid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader(speciality);
        doctorSpecialityGrid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader(instituteOfAccreditation);
        doctorSpecialityGrid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader(workExperience);
        doctorSpecialityGrid.setItems(doctorSpecialityService.getDoctorSpecialities(container.getDoctor().getId()));
        doctorSpecialityGrid.setAllRowsVisible(true);
        layout.add(doctorSpecialityGrid);
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        visitScheduleNotSetYet.setText(getTranslation("visits.schedule_not_set"));
        doYouWantToCreateNewOne.setText((getTranslation("visits.do_you_want_to_create_new_one")));
        buttonCreate.setText(getTranslation("visits.create"));
        doYouWantToDoAVisitReservation.setText(getTranslation("visits.do_you_want_to_do_a_visit_reservation"));
        visits.setText(getTranslation("visits.visits"));
        newVisit.setText(getTranslation("visits.new_visit"));
        reason = getTranslation("visits.reason");
        doctor = getTranslation("visits.doctor");
        dateAndTime = getTranslation("visits.datetime");
        impending = getTranslation("visits.impending");
        completed = getTranslation("visits.completed");
        edit = getTranslation("visits.edit");
        reasonMustNotBeEmpty = getTranslation("visits.reason_must_not_be_empty");
        dateAndTimeMustBeNotEmpty = getTranslation("visits.date_time_must_not_be_empty");
        save = getTranslation("visits.save");
        finalDiagnosis = getTranslation("visits.final_diagnosis");
        comments = getTranslation("visits.comments");
        areYouSureYouWantToSaveThisVisit = getTranslation("visits.are_you_sure_you_want_to_save_this_visit");
        cancel = getTranslation("visits.cancel");
        areYouSureYouWantToDeleteThisVisit = getTranslation("visits.are_you_sure_you_want_to_delete_this_visit");
        delete = getTranslation("visits.delete");
        selectedDayOfWeekIsNotAvailable = getTranslation("visits.selected_day_of_week_is_not_available");
        thisDateTimeIsNotAvailableForBooking = getTranslation("visits.this_date_time_is_not_available_for_booking");
        visitEnquiry = getTranslation("visits.visit_enquiry");
        bookingTime = getTranslation("visits.booking_time");
        visitReason = getTranslation("visits.visit_reason");
        aboutDoctor = getTranslation("visits.about_doctor");
        currentHospital = getTranslation("profile.current_hospital");
        instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
        workExperience = getTranslation("profile.work_experience");
        speciality = getTranslation("profile.speciality");
        visitGrid.getColumnByKey("reason").setHeader(reason);
        visitGrid.getColumnByKey("doctor").setHeader(doctor);
        visitGrid.getColumnByKey("datetime").setHeader(dateAndTime);
    }
}