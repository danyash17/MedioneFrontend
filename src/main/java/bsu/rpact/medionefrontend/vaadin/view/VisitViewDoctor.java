package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.*;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.DoctorSpecialityService;
import bsu.rpact.medionefrontend.service.VisitService;
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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "visitDoctor", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitViewDoctor extends VerticalLayout {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
    private final VisitService visitService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;

    public VisitViewDoctor(VisitService visitService, DoctorSpecialityService doctorSpecialityService, DoctorService doctorService, ImageUtils imageUtils) {
        this.visitService = visitService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        if(doctorService.getDoctorSelf().getVisitSchedule()==null){
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
    }

    private void doGridInit(Grid<Visit> grid) {
        grid.getEditor().setBinder(new Binder<>(Visit.class));
        grid.addColumn(Visit::getReason).setHeader("Reason").setTextAlign(ColumnTextAlign.START);
        grid.addColumn(createRenderer()).setHeader("Patient")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(visit -> {
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
        AtomicReference<Patient> atomicPatientReference = new AtomicReference<>();
        AtomicReference<Visit> atomicVisitReference = new AtomicReference<>();
        grid.addComponentColumn(visit -> {
            Button editButton = new Button("Process");
            editButton.setEnabled(visit.getActive());
            editButton.addClickListener(e -> {
                atomicPatientReference.set(visit.getPatient());
                atomicVisitReference.set(visit);
                grid.getEditor().editItem(visit);
                getProcessingDialog(atomicPatientReference.get(), atomicVisitReference.get()).open();
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);
        grid.setAllRowsVisible(true);

        grid.addSelectionListener(e -> {
            setupEnquiryDialog(e);
        });
    }

    private Dialog getProcessingDialog(Patient patient, Visit visit) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.add(new H2("Process visit results"));
        TextArea diagnosis = new TextArea();
        diagnosis.setLabel("Diagnosis");
        layout.add(diagnosis);
        TextArea comments = new TextArea();
        comments.setLabel("Comments");
        layout.add(comments);
        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button processButton = new Button("Process", (e) -> {
            getProcessConfirmationDialog(visit, diagnosis.getValue(), comments.getValue()).open();
        });
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.add(layout);
        dialog.add(new HorizontalLayout(cancelButton, processButton));
        return dialog;
    }

    private Dialog getProcessConfirmationDialog(Visit visit, String diagnosis, String comments) {
        Dialog dialog = new Dialog();
        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button processButton = new Button("OK", (e) -> {
            visit.setActive(false);
            visit.setDiagnosis(diagnosis);
            visit.setComments(comments);
            visitService.update(visit);
            UI.getCurrent().getPage().reload();
        });
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.add(new H2("Are you sure you want to complete and archive this visit?"));
        dialog.add(new HorizontalLayout(cancelButton, processButton));
        return dialog;
    }

    private void setupEnquiryDialog(SelectionEvent<Grid<Visit>, Visit> e) {
        Dialog dialog = new Dialog();
        Visit visit = e.getFirstSelectedItem().get();
        dialog.add(createDialogLayout(dialog, visit));
        dialog.setCloseOnEsc(true);
        dialog.setWidth("800px");
        dialog.open();
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

    private Renderer<Visit> createRenderer() {
        return LitRenderer.<Visit>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.fullName} </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("pictureUrl", visit -> {
                    Patient patient = visit.getPatient();
                    String fullName = patient.getCredentials().getFirstName() +
                            patient.getCredentials().getPatronymic() +
                            patient.getCredentials().getLastName();
                    return imageUtils.chacheByteArrToImagePatient(null, fullName);
                })
                .withProperty("fullName", visit -> {
                    Patient patient = visit.getPatient();
                    return patient.getCredentials().getFirstName() + " " +
                            patient.getCredentials().getPatronymic() + " " +
                            patient.getCredentials().getLastName();
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

    private VerticalLayout createDialogLayout(Dialog dialog, Visit visit) {
        VerticalLayout layout = new VerticalLayout();

        layout.add(new H1("Visit enquiry"));
        layout.add(new Label("Booking time: " + DATE_FORMAT.format(visit.getDatetime())));
        layout.add(new Label("Visit reason: " + visit.getReason()));
        if (!visit.getActive()) {
            setupVisitResults(layout, visit);
        }
        VerticalLayout patientLayout = new VerticalLayout();
        setupPatientSection(patientLayout, visit.getPatient());
        Details details = new Details("About patient", patientLayout);
        details.setOpened(false);
        layout.add(details);
        return layout;
    }

    private void setupPatientSection(VerticalLayout layout, Patient patient) {
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.setWidth("700px");
        Medcard medcard = patient.getMedcard();
        layout.add(new H2("Patient medcard"));
        layout.add(new Label("Created: " + medcard.getDateCreated()));
        layout.add(new Label("Expiring at: " + medcard.getValidTo()));
        layout.add(new Label("Residental address: " + medcard.getResidentalAddress()));
        layout.add(new H3("Illnesses"));
        PaginatedGrid<Illness> illnessGrid = new PaginatedGrid<>();
        VerticalLayout illnessLayout = MedcardView.getIllnessLayout(medcard, illnessGrid);
        illnessGrid.setWidthFull();
        layout.add(illnessLayout);

        layout.add(new H3("Operations"));
        PaginatedGrid<Operation> operationGrid = new PaginatedGrid<>();
        operationGrid.setWidthFull();
        VerticalLayout operationLayout = MedcardView.getOperationLayout(medcard, operationGrid);
        layout.add(operationLayout);
    }
}