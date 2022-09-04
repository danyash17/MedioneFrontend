package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.service.VisitService;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Route(value = "visit", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitView extends VerticalLayout {

    private final VisitService visitService;
    private final ImageUtils imageUtils;

    public VisitView(VisitService visitService, ImageUtils imageUtils) {
        this.visitService = visitService;
        this.imageUtils = imageUtils;
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        add(new H2("Visits"));
        Grid<Visit> visitGrid = new Grid<>(Visit.class, false);
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            return dateFormat.format(visit.getDatetime());
        }).setHeader("Date and Time").setTextAlign(ColumnTextAlign.START);
        grid.addColumn(createStatusComponentRenderer()).setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        AtomicReference<Doctor> atomicDoctorReference = new AtomicReference<>();
        AtomicReference<Visit> atomicVisitReference = new AtomicReference<>();
        Grid.Column<Visit> editColumn = grid.addComponentColumn(visit -> {
            Button editButton = new Button("Edit");
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

    private void setupGrid(Grid<Visit> activeGrid) {
        doGridInit(activeGrid);
        List<Visit> visitList = visitService.getAllMyVisitsSelf();
        visitList.sort(new Comparator<Visit>() {
            @Override
            public int compare(Visit o1, Visit o2) {
                return o1.getDatetime().toLocalDateTime().compareTo(o2.getDatetime().toLocalDateTime());
            }
        });
        activeGrid.setItems(visitList);
    }

    private static final SerializableBiConsumer<Span, Visit> statusComponentUpdater = (span, visit) -> {
        String theme = String
                .format("badge %s", visit.getActive() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(visit.getActive() ? "Upcoming" : "Archived");
    };

    private static ComponentRenderer<Span, Visit> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
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
                    return imageUtils.chacheByteArrToImage(doctor.getDoctorPhoto(), fullName);
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
}