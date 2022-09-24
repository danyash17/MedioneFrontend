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
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.badge.Badge;
import org.vaadin.klaudeta.PaginatedGrid;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "visitPatient", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitViewPatient extends VerticalLayout {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
    private final SessionManager sessionManager;
    private final VisitService visitService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final ImageUtils imageUtils;
    private final PatientService patientService;

    public VisitViewPatient(SessionManager sessionManager, VisitService visitService, DoctorSpecialityService doctorSpecialityService, ImageUtils imageUtils, DoctorService doctorService, PatientService patientService) {
        this.sessionManager = sessionManager;
        this.visitService = visitService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.imageUtils = imageUtils;
        this.patientService = patientService;
        Optional<Patient> patient = patientService.getSelf();
        if(patient.isPresent() && patient.get().getVisitSchedule()==null){
            add(new H3("Visit schedule not set yet"));
            add(new H4("Do you want to create new one?"));
            Button button = new Button("Create");
            button.addClickListener(e -> {
                visitService.createVisitScheduleBySelf();
                UI.getCurrent().getPage().reload();
            });
            add(button);
            return;
        }
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        add(new H2("Visits"));
        PaginatedGrid<Visit> visitGrid = new PaginatedGrid<>();
        setupGrid(visitGrid);
        add(visitGrid);
        Paragraph paragraph = new Paragraph("Want to do a visit reservation?");
        Button button = new Button("New visit");
        button.addClickListener(e -> {
            UI.getCurrent().navigate(VisitCreationView.class);
        });
        add(paragraph, button);
    }

    private void doGridInit(Grid<Visit> grid) {
        Editor<Visit> editor = grid.getEditor();
        Grid.Column<Visit> reasonColumn = grid.addColumn(Visit::getReason).setHeader("Reason").setTextAlign(ColumnTextAlign.START);
        grid.addColumn(createEmployeeRenderer()).setHeader("Doctor")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.START);
        Grid.Column<Visit> dtColumn = grid.addColumn(visit -> {
            return DATE_FORMAT.format(visit.getDatetime());
        }).setHeader("Date and Time").setTextAlign(ColumnTextAlign.START);
        grid.addComponentColumn(visit -> {
            if(visit.getActive()){
                Badge badge = new Badge("Impending");
                badge.setVariant(Badge.BadgeVariant.NORMAL);
                badge.setPrimary(true);
                badge.setPill(true);
                badge.setIcon(VaadinIcon.CLOCK.create());
                return badge;
            }
            else {
                Badge badge = new Badge("Completed");
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
            Button editButton = new Button("Edit");
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

        Binder<Visit> binder = new Binder<>(Visit.class);
        editor.setBinder(binder);
        editor.setBuffered(true);
        TextField reasonField = new TextField();
        reasonField.setWidthFull();
        binder.forField(reasonField)
                .asRequired("Reason must not be empty")
                .bind(Visit::getReason, Visit::setReason);
        reasonColumn.setEditorComponent(reasonField);

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setWidthFull();
        binder.forField(dateTimePicker).asRequired("Date time must not be empty")
                .bind(valueProvider -> valueProvider.getDatetime().toLocalDateTime(),
                        (Visit visit, LocalDateTime dt) -> {
                            visit.setDatetime(Timestamp.valueOf(dt));
                        });
        setupDateTimePicker(dateTimePicker, atomicDoctorReference);
        dtColumn.setEditorComponent(dateTimePicker);

        Button saveButton = new Button("Save", e -> {
            getSaveConfirmationDialog(editor, atomicVisitReference.get()).open();
        });
        Button cancelButton = new Button(VaadinIcon.TRASH.create(), e -> {
            getDeleteConfirmationDialog(editor).open();
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        grid.addSelectionListener(e -> {
            setupEnquiryDialog(e);
        });
    }

    private void setupEnquiryDialog(SelectionEvent<Grid<Visit>, Visit> e) {
        Dialog dialog = new Dialog();
        Visit visit = e.getFirstSelectedItem().get();
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
        H3 diagnosis = new H3("Final diagnosis");
        Label diagLabel = new Label(visit.getDiagnosis());
        layout.add(diagnosis, diagLabel);
        H3 comments = new H3("Comments");
        Label commentsLabel = new Label(visit.getComments());
        layout.add(comments, commentsLabel);
    }

    private Dialog getSaveConfirmationDialog(Editor<Visit> editor, Visit visit) {
        Dialog dialog = new Dialog();
        dialog.add(new H3("Are you sure you want to save this visit?"));
        dialog.setCloseOnEsc(true);
        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button saveButton = new Button("Save", (e) -> {
            dialog.close();
            editor.save();
            visitService.update(visit);
            UI.getCurrent().getPage().reload();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.add(new HorizontalLayout(cancelButton, saveButton));
        return dialog;
    }

    private Dialog getDeleteConfirmationDialog(Editor<Visit> editor) {
        Dialog dialog = new Dialog();
        dialog.add(new H3("Are you sure you want to delete this visit?"));
        dialog.setCloseOnEsc(true);
        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button deleteButton = new Button("Delete", (e) -> {
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
        }, "The selected day of week is not available").withValidator(startDateTime -> {
            LocalTime valueTime = LocalTime.of(startDateTime.getHour(), startDateTime.getMinute());
            List<Visit> visitList = visitService.getAllVisitsOfDoctor(doctorReference.get().getId());
            boolean validDaySchedule = !(LocalTime.of(8, 0).isAfter(valueTime)
                    || (LocalTime.of(12, 0).isBefore(valueTime) && LocalTime.of(13, 0).isAfter(valueTime))
                    || LocalTime.of(16, 0).isBefore(valueTime));
            boolean validVisitSchedule = visitList.stream().allMatch(currentVisit ->
                    !currentVisit.getDatetime().toLocalDateTime().toLocalTime().equals(valueTime));
            return validDaySchedule && validVisitSchedule;
        }, "This datetime is not available for booking").bind(
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

        layout.add(new H1("Visit enquiry"));
        layout.add(new Label("Booking time: " + DATE_FORMAT.format(visit.getDatetime())));
        layout.add(new Label("Visit reason: " + visit.getReason()));
        if (!visit.getActive()) {
            setupVisitResults(layout, visit);
        }
        VerticalLayout doctorLayout = new VerticalLayout();
        setupDoctorSection(container, doctorLayout);
        Details details = new Details("About doctor", doctorLayout);
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
        layout.add(new Paragraph("Current hospital: " + container.getDoctor().getHospital()));
        layout.add(new H4(container.getDoctor().getCommonInfo()));
        Grid<RepresentativeDoctorSpecialityPojo> grid = new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
        grid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader("Speciality");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader("Institute of Accreditation");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader("Work Experience");
        grid.setItems(doctorSpecialityService.getDoctorSpecialities(container.getDoctor().getId()));
        grid.setAllRowsVisible(true);
        layout.add(grid);
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }
}