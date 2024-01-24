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
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
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
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "visitDoctor", layout = MainLayout.class)
@PageTitle("Visits")
public class VisitViewDoctor extends VerticalLayout implements LocaleChangeObserver {

    public SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", VaadinSession.getCurrent().getLocale());
    private final VisitService visitService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final DoctorService doctorService;
    private final ImageUtils imageUtils;
    private final H3 visitScheduleNotSetYet = new H3(getTranslation("visits.schedule_not_set"));
    private final H4 doYouWantToCreateNewOne = new H4(getTranslation("visits.do_you_want_to_create_new_one"));
    private final Button create = new Button(getTranslation("visits.create"));
    private final H2 visits = new H2(getTranslation("visits.visits"));
    private final PaginatedGrid<Visit> visitGrid = new PaginatedGrid<>();
    private TextField operationSearchField = new TextField();
    private TextField illnessSearchField = new TextField();
    private String reason = getTranslation("visits.reason");
    private String patient = getTranslation("visits.patient");
    private String impending = getTranslation("visits.impending");
    private String completed = getTranslation("visits.completed");
    private String process = getTranslation("visits.process");
    private String processVisitResults = getTranslation("visits.process_visit_results");
    private String diagnosis = getTranslation("visits.diagnosis");
    private String commentsLabel = getTranslation("visits.comments");
    private String cancel = getTranslation("visits.cancel");
    private String processButtonLabel = getTranslation("visits.process");
    private H2 areYouSureYouWantCompleteAndArchiveThisVisit = new H2(getTranslation("visits.are_you_sure_you_want_to_complete_and_archive_this_visit"));
    private H3 finalDiagnosis = new H3(getTranslation("visits.final_diagnosis"));
    private H3 comments = new H3(getTranslation("visits.comments"));
    private H3 areYouSureYouWantToSaveThisVisit = new H3(getTranslation("visits.are_you_sure_you_want_to_save_this_visit"));
    private String save = getTranslation("visits.save");
    private H3 areYouSureYouWantToDeleteThisVisit = new H3(getTranslation("visits.are_you_sure_you_want_to_delete_this_visit"));
    private String delete = getTranslation("visits.delete");
    private H1 visitEnquiry = new H1(getTranslation("visits.visit_enquiry"));
    private String bookingTime = getTranslation("visits.booking_time");
    private String visitReason = getTranslation("visits.visit_reason");
    private String aboutPatient = getTranslation("visits.about_patient");
    private H2 patientMedcard = new H2(getTranslation("visits.patient_medcard"));
    private String created = getTranslation("visits.created");
    private String expiringAt = getTranslation("visits.expiring_at");
    private String residentalAddress = getTranslation("visits.residental_address");
    private String illnesses = getTranslation("visits.illnesses");
    private String operations = getTranslation("visits.operations");
    private String dateAndTime = getTranslation("visits.date_and_time");

    public VisitViewDoctor(VisitService visitService, DoctorSpecialityService doctorSpecialityService, DoctorService doctorService, ImageUtils imageUtils) {
        this.visitService = visitService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.doctorService = doctorService;
        this.imageUtils = imageUtils;
        if(doctorService.getDoctorSelf().getVisitSchedule()==null){
            add(visitScheduleNotSetYet);
            add(doYouWantToCreateNewOne);
            create.addClickListener(e -> {
                visitService.createVisitScheduleBySelf();
                UI.getCurrent().getPage().reload();
            });
            add(create);
            return;
        }
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        add(visits);
        setupGrid(visitGrid);
        add(visitGrid);
    }

    private void doGridInit(Grid<Visit> grid) {
        grid.getEditor().setBinder(new Binder<>(Visit.class));
        grid.addColumn(Visit::getReason).setHeader(reason).setTextAlign(ColumnTextAlign.START).setKey("reason");
        grid.addColumn(createRenderer()).setHeader(patient).setKey("patient")
                .setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.START);
        grid.addColumn(visit -> {
            return DATE_FORMAT.format(visit.getDatetime());
        }).setHeader(dateAndTime).setKey("datetime").setTextAlign(ColumnTextAlign.START);
        grid.addComponentColumn(visit -> {
            if(visit.getActive()){
                Badge badge = new Badge();
                badge.setText(impending);
                badge.setVariant(Badge.BadgeVariant.NORMAL);
                badge.setPrimary(true);
                badge.setPill(true);
                badge.setIcon(VaadinIcon.CLOCK.create());
                return badge;
            }
            else {
                Badge badge = new Badge();
                badge.setText(completed);
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
            Button editButton = new Button();
            editButton.setText(process);
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

        grid.addItemDoubleClickListener(e -> {
            setupEnquiryDialog(e);
        });
    }

    private Dialog getProcessingDialog(Patient patient, Visit visit) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.add(new H2(processVisitResults));
        TextArea diagnosis = new TextArea();
        diagnosis.setLabel(this.diagnosis);
        layout.add(diagnosis);
        TextArea comments = new TextArea();
        comments.setLabel(commentsLabel);
        layout.add(comments);
        Button cancelButton = new Button(cancel, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button processButton = new Button(processButtonLabel, (e) -> {
            getProcessConfirmationDialog(visit, diagnosis.getValue(), comments.getValue()).open();
        });
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.add(layout);
        dialog.add(new HorizontalLayout(cancelButton, processButton));
        return dialog;
    }

    private Dialog getProcessConfirmationDialog(Visit visit, String diagnosis, String comments) {
        Dialog dialog = new Dialog();
        Button cancelButton = new Button(cancel, (e) -> dialog.close());
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
        dialog.add(areYouSureYouWantCompleteAndArchiveThisVisit);
        dialog.add(new HorizontalLayout(cancelButton, processButton));
        return dialog;
    }

    private void setupEnquiryDialog(ItemDoubleClickEvent<Visit> e) {
        Dialog dialog = new Dialog();
        Visit visit = e.getItem();
        dialog.add(createDialogLayout(dialog, visit));
        dialog.setCloseOnEsc(true);
        dialog.setWidth("800px");
        dialog.open();
    }

    private void setupVisitResults(VerticalLayout layout, Visit visit) {
        Label diagLabel = new Label(visit.getDiagnosis());
        layout.add(finalDiagnosis, diagLabel);
        Label commentsLabel = new Label(visit.getComments());
        layout.add(comments, commentsLabel);
    }

    private Dialog getSaveConfirmationDialog(Editor<Visit> editor, Visit visit) {
        Dialog dialog = new Dialog();
        dialog.add(areYouSureYouWantToSaveThisVisit);
        dialog.setCloseOnEsc(true);
        Button cancelButton = new Button(cancel, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button saveButton = new Button(save, (e) -> {
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
        dialog.add(areYouSureYouWantToDeleteThisVisit);
        dialog.setCloseOnEsc(true);
        Button cancelButton = new Button(cancel, (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        Button deleteButton = new Button(delete, (e) -> {
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
                    String name = patient.getCredentials().getFirstName() + patient.getCredentials().getLastName();
                    return name;
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
        layout.add(visitEnquiry);
        layout.add(new Label(bookingTime + DATE_FORMAT.format(visit.getDatetime())));
        layout.add(new Label(visitReason + visit.getReason()));
        if (!visit.getActive()) {
            setupVisitResults(layout, visit);
        }
        VerticalLayout patientLayout = new VerticalLayout();
        setupPatientSection(patientLayout, visit.getPatient());
        Details details = new Details(aboutPatient, patientLayout);
        details.setOpened(false);
        layout.add(details);
        return layout;
    }

    private void setupPatientSection(VerticalLayout layout, Patient patient) {
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.setWidth("700px");
        Medcard medcard = patient.getMedcard();
        layout.add(patientMedcard);
        layout.add(new Label(created + medcard.getDateCreated()));
        layout.add(new Label(expiringAt + medcard.getValidTo()));
        layout.add(new Label(residentalAddress + medcard.getResidentalAddress()));
        layout.add(new H3(illnesses));
        PaginatedGrid<Illness> illnessGrid = new PaginatedGrid<>();
        VerticalLayout illnessLayout = getIllnessLayout(medcard, illnessGrid);
        illnessGrid.setWidthFull();
        layout.add(illnessLayout);

        layout.add(new H3(operations));
        PaginatedGrid<Operation> operationGrid = new PaginatedGrid<>();
        operationGrid.setWidthFull();
        VerticalLayout operationLayout = getOperationLayout(medcard, operationGrid);
        layout.add(operationLayout);
    }

    public VerticalLayout getOperationLayout(Medcard medcard, PaginatedGrid<Operation> operationGrid) {
        String name = getTranslation("medcard.name");
        String description = getTranslation("medcard.description");
        String date = getTranslation("medcard.date");
        String search = getTranslation("medcard.search");
        operationGrid.addColumn(Operation::getId).setHeader("№");
        operationGrid.addColumn(Operation::getName).setHeader(name).setKey("name");
        operationGrid.addColumn(Operation::getDescription).setHeader(description).setKey("description");
        operationGrid.addColumn(Operation::getOperationDate).setHeader(date).setKey("date");
        operationGrid.setItems(medcard.getOperationList());
        operationGrid.setAllRowsVisible(true);
        operationGrid.setPageSize(10);
        operationGrid.setPaginatorSize(5);
        GridListDataView<Operation> operationDataView =
                operationGrid.setItems(medcard.getOperationList());
        operationGrid.getColumns().stream().forEach(item -> {
            item.setResizable(true);
            item.setSortable(true);
        });
        operationSearchField.setWidth("20%");
        operationSearchField.setPlaceholder(search);
        operationSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        operationSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        operationSearchField.addValueChangeListener(e -> operationDataView.refreshAll());

        operationDataView.addFilter(item -> {
            String searchTerm = operationSearchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            boolean matchesId = matchesTerm(String.valueOf(item.getId()),
                    searchTerm);
            boolean matchesDescription = matchesTerm(item.getDescription(), searchTerm);
            boolean matchesName = matchesTerm(item.getName(),
                    searchTerm);
            boolean matchesDate = matchesTerm(String.valueOf(item.getOperationDate()),
                    searchTerm);
            return matchesId || matchesDescription || matchesName || matchesDate;
        });
        VerticalLayout operationLayout = new VerticalLayout(operationSearchField, operationGrid);
        operationLayout.setPadding(false);
        return operationLayout;
    }

    public VerticalLayout getIllnessLayout(Medcard medcard, PaginatedGrid<Illness> illnessGrid) {
        String illFrom = getTranslation("medcard.ill_from");
        String illTo = getTranslation("medcard.ill_to");
        String description = getTranslation("medcard.description");
        String search = getTranslation("medcard.search");
        illnessGrid.addColumn(Illness::getId).setHeader("№");
        illnessGrid.addColumn(Illness::getDescription).setHeader(description).setKey("description");
        illnessGrid.addColumn(Illness::getIllFrom).setHeader(illFrom).setKey("illFrom");
        illnessGrid.addColumn(Illness::getIllTo).setHeader(illTo).setKey("illTo");
        GridListDataView<Illness> illnessDataView =
                illnessGrid.setItems(medcard.getIllnessList());
        illnessGrid.getColumns().stream().forEach(item -> {
            item.setResizable(true);
            item.setSortable(true);
        });
        illnessGrid.setAllRowsVisible(true);
        illnessGrid.setPageSize(10);
        illnessGrid.setPaginatorSize(5);
        illnessSearchField.setWidth("20%");
        illnessSearchField.setPlaceholder(search);
        illnessSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        illnessSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        illnessSearchField.addValueChangeListener(e -> illnessDataView.refreshAll());

        illnessDataView.addFilter(item -> {
            String searchTerm = illnessSearchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            boolean matchesId = matchesTerm(String.valueOf(item.getId()),
                    searchTerm);
            boolean matchesDescription = matchesTerm(item.getDescription(), searchTerm);
            boolean matchesFrom = matchesTerm(String.valueOf(item.getIllFrom()),
                    searchTerm);
            boolean matchesTo = matchesTerm(String.valueOf(item.getIllTo()),
                    searchTerm);
            return matchesId || matchesDescription || matchesFrom || matchesTo;
        });
        VerticalLayout illnessLayout = new VerticalLayout(illnessSearchField, illnessGrid);
        illnessLayout.setPadding(false);
        return illnessLayout;
    }

    private static boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", VaadinSession.getCurrent().getLocale());
        visitScheduleNotSetYet.setText(getTranslation("visits.schedule_not_set"));
        doYouWantToCreateNewOne.setText(getTranslation("visits.do_you_want_to_create_new_one"));
        create.setText(getTranslation("visits.create"));
        visits.setText(getTranslation("visits.visits"));
        reason = getTranslation("visits.reason");
        patient = getTranslation("visits.patient");
        impending = getTranslation("visits.impending");
        completed = getTranslation("visits.completed");
        process = getTranslation("visits.process");
        processVisitResults = getTranslation("visits.process_visit_results");
        diagnosis = getTranslation("visits.diagnosis");
        commentsLabel = getTranslation("visits.comments");
        cancel = getTranslation("visits.cancel");
        processButtonLabel = getTranslation("visits.process");
        areYouSureYouWantCompleteAndArchiveThisVisit.setText(getTranslation("visits.are_you_sure_you_want_to_complete_and_archive_this_visit"));
        finalDiagnosis.setText(getTranslation("visits.final_diagnosis"));
        comments.setText(getTranslation("visits.comments"));
        areYouSureYouWantToSaveThisVisit.setText(getTranslation("visits.are_you_sure_you_want_to_save_this_visit"));
        save = getTranslation("visits.save");
        areYouSureYouWantToDeleteThisVisit.setText(getTranslation("visits.are_you_sure_you_want_to_delete_this_visit"));
        delete = getTranslation("visits.delete");
        visitEnquiry.setText(getTranslation("visits.visit_enquiry"));
        bookingTime = getTranslation("visits.booking_time");
        visitReason = getTranslation("visits.visit_reason");
        aboutPatient = getTranslation("visits.about_patient");
        patientMedcard.setText(getTranslation("visits.patient_medcard"));
        created = getTranslation("visits.created");
        expiringAt = getTranslation("visits.expiring_at");
        residentalAddress = getTranslation("visits.residental_address");
        illnesses = getTranslation("visits.illnesses");
        operations = getTranslation("visits.operations");
        dateAndTime = getTranslation("visits.date_and_time");

        visitGrid.getColumnByKey("reason").setHeader(reason);
        visitGrid.getColumnByKey("patient").setHeader(patient);
        visitGrid.getColumnByKey("datetime").setHeader(dateAndTime);
        visitGrid.getListDataView().refreshAll();
    }
}