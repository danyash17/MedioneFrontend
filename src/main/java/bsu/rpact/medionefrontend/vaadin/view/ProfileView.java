package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Credentials;
import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.enums.SpecialityName;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.service.CredentialsService;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.DoctorSpecialityService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.i18n.components.UploadLocalizator;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;
import de.f0rce.cropper.Cropper;
import de.f0rce.cropper.settings.CropperSettings;
import de.f0rce.cropper.settings.enums.ViewMode;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.vaadin.addons.badge.Badge;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends VerticalLayout implements LocaleChangeObserver {

    private final SessionManager sessionManager;
    private final CredentialsService credentialsService;
    private final DoctorService doctorService;
    private final DoctorSpecialityService doctorSpecialityService;
    private final AuthService authService;
    private final ImageUtils imageUtils;

    private final static String[] allowedAvatarExtensions = new String[]{"image/*"};
    private final static int allowedFileSize = 20 * 1024 * 1024;
    private final static ByteArrayOutputStream os = new ByteArrayOutputStream(allowedFileSize);
    private final ComboBox<SpecialityName> specialityNameComboBox = new ComboBox<>();
    private final Button discardCancel = new Button();
    private final H3 profileData = new H3(getTranslation("profile.profile_data"));
    private String currentHospital = getTranslation("profile.current_hospital");
    private String firstName = getTranslation("profile.first_name");
    private String patronymic = getTranslation("profile.patronymic");
    private String lastName = getTranslation("profile.last_name");
    private String phone = getTranslation("profile.phone");
    private String patient = getTranslation("profile.patient");
    private String doctor = getTranslation("profile.doctor");
    private String administrator = getTranslation("profile.admin");
    private String role = getTranslation("profile.role");
    private String enabled = getTranslation("profile.enabled");
    private String disabled = getTranslation("profile.disabled");
    private String twoFactor = getTranslation("profile.twofact");
    private final H3 profileSettings = new H3(getTranslation("profile.profile_settings"));
    private String use2Fa = getTranslation("profile.use2Fa");
    private String applyLabel = getTranslation("profile.apply");
    private String messageAvailable = getTranslation("profile.messages.available");
    private String messageUnreachable = getTranslation("profile.messages_unreachable");
    private final H4 currentDoctorStatus = new H4(getTranslation("profile.current_doctor_status"));
    private String uploadAvatar = getTranslation("profile.upload_avatar");
    private String addSpeciality = getTranslation("profile.add_speciality");
    private String discardCancelLabel = getTranslation("profile.discard_cancel");
    private String manageSpecialities = getTranslation("profile.manage_specialities");
    private final H4 changePassword = new H4(getTranslation("profile.change_password"));
    private String newPassword = getTranslation("profile.new_password");
    private String confirmNewPassword = getTranslation("profile.confirm_password");
    private String youSuccessfullyChangedYourPassword = getTranslation("profile.messages.you_successfully_changed_your_password");
    private String passwordsDoesNotMatch = getTranslation("profile.messages.passwords_do_not_match");
    private String passwordPattern = getTranslation("register.messages.password_pattern");
    private String availableLabel = getTranslation("profile.available");
    private String unreachableLabel = getTranslation("profile.unreachable");
    private String specialityLabel = getTranslation("profile.speciality");
    private String instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
    private String workExperience = getTranslation("profile.work_experience");
    private String manage = getTranslation("profile.manage");
    private String instituteMustNotBeEmpty = getTranslation("profile.institute_must_not_be_empty");
    private String saveLabel = getTranslation("profile.save");
    private String specialityIsSuccessfullySaved = getTranslation("profile.messages.speciality_is_successfully_saved");
    private String specialityIsSuccessfullyUpdated = getTranslation("profile.messages.speciality_is_successfully_updated");
    private String somethingWentWrongPleaseContactAdministrator = getTranslation("profile.messages.something_went_wrong_please_contact_administrator");
    private String editLabel = getTranslation("profile.edit");
    private String cropLabel = getTranslation("profile.crop");
    private String deleteConfirm = getTranslation("profile.deletion_confirmation");
    private String doYouSureWantToDeleteSpeciality = getTranslation("profile.messages.do_you_sure_want_to_delete_speciality");
    private String cancelLabel = getTranslation("profile.cancel");
    private String specialitySuccessfullyDeleted = getTranslation("profile.messages.speciality_successfully_deleted");
    private String yes = getTranslation("profile.yes");
    private String avatarUploaded = getTranslation("profile.messages.avatar_uploaded");

    private Image avatar = new Image();
    private String currentMimeType;
    private byte[] croppedAvatar = new byte[allowedFileSize];

    private Boolean firstCall = true;
    private Boolean newSpeciality = false;

    private AtomicReference<RepresentativeDoctorSpecialityPojo> editReference = new AtomicReference<>();

    private class FileReceiver implements Receiver {

        @Override
        public OutputStream receiveUpload(String fileName, String mimeType) {
            os.reset();
            return os;
        }
    }

    public ProfileView(SessionManager sessionManager, CredentialsService credentialsService, DoctorService doctorService, DoctorSpecialityService doctorSpecialityService, AuthService authService, ImageUtils imageUtils) {
        this.sessionManager = sessionManager;
        this.credentialsService = credentialsService;
        this.doctorService = doctorService;
        this.doctorSpecialityService = doctorSpecialityService;
        this.authService = authService;
        this.imageUtils = imageUtils;
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        add(profileData);
        add(new Label(firstName + sessionManager.getFirstNameAttribute()));
        add(new Label(patronymic + sessionManager.getPatronymicAttribute()));
        add(new Label(lastName + sessionManager.getLastNameAttribute()));
        add(new Label(phone + sessionManager.getPhoneAttribute()));
        boolean isDoctor = false;
        Badge badgeRole = new Badge();
        badgeRole.setPrimary(true);
        badgeRole.setPill(true);
        switch (sessionManager.getRoleAttribute()){
            case "PATIENT": {
                badgeRole.setText(patient);
                badgeRole.setIcon(VaadinIcon.USER.create());
                badgeRole.setVariant(Badge.BadgeVariant.NORMAL);
                break;
            }
            case "DOCTOR": {
                badgeRole.setText(doctor);
                badgeRole.setIcon(VaadinIcon.DOCTOR.create());
                badgeRole.setVariant(Badge.BadgeVariant.NORMAL);
                isDoctor = true;
                break;
            }
            case "ADMIN": {
                badgeRole.setText(administrator);
                badgeRole.setIcon(VaadinIcon.SPECIALIST.create());
                badgeRole.setVariant(Badge.BadgeVariant.NORMAL);
                break;
            }
        }
        add(new HorizontalLayout(new Label(role), badgeRole));
        if(isDoctor){
            Doctor doctor = doctorService.getDoctorSelf();
            Badge badgeHospital = new Badge();
            badgeHospital.setPrimary(true);
            badgeHospital.setPill(true);
            badgeHospital.setText(doctor.getHospital());
            badgeHospital.setVariant(Badge.BadgeVariant.NORMAL);
            badgeHospital.setIcon(VaadinIcon.HOSPITAL.create());
            add(new HorizontalLayout(new Label(currentHospital), badgeHospital));
        }
        Badge badge2Fa = new Badge();
        badge2Fa.setPrimary(true);
        badge2Fa.setPill(true);
        if(sessionManager.get2FaAttribute().booleanValue()){
            badge2Fa.setText(enabled);
            badge2Fa.setVariant(Badge.BadgeVariant.CONTRAST);
            badge2Fa.setIcon(VaadinIcon.LOCK.create());
        }
        else {
            badge2Fa.setText(disabled);
            badge2Fa.setVariant(Badge.BadgeVariant.SUCCESS);
            badge2Fa.setIcon(VaadinIcon.UNLOCK.create());
        }
        add(new HorizontalLayout(new Label(twoFactor), badge2Fa));
        add(profileSettings);
        Checkbox checkbox = new Checkbox();
        checkbox.setLabel(use2Fa);
        checkbox.setValue(session.getAttribute("2FA").equals(Boolean.TRUE));
        add(checkbox);
        HorizontalLayout layout = new HorizontalLayout();
        Button apply = new Button(applyLabel, e -> {
            Credentials credentials = credentialsService.getSelf();
            credentials.setEnabled2Fa(checkbox.getValue());
            credentialsService.update(credentials, true);
            sessionManager.set2FaAttribute(checkbox.getValue());
            UI.getCurrent().navigate(ProfileView.class);
        });
        apply.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(apply);
        add(layout);
        if(sessionManager.getRoleAttribute().equals("DOCTOR")) {
            Doctor doctor = doctorService.getDoctorSelf();
            ToggleButton status = new ToggleButton();
            populateAvailability(status, doctor.getAvailable());
            status.addValueChangeListener(e -> {
                doctor.setAvailable(e.getValue());
                doctorService.updateSelf(doctor);
                populateAvailability(status, doctor.getAvailable());
                Notification notification = doctor.getAvailable() ?
                        UiUtils.generateSuccessNotification(messageAvailable) :
                        UiUtils.generateErrorNotification(messageUnreachable);
                notification.open();
            });
            add(currentDoctorStatus);
            add(status);
            avatar.setSrc(imageUtils.chacheByteArrToImageDoctor(doctor.getDoctorPhoto(),
                    doctor.getCredentials().getFirstName() +
                            doctor.getCredentials().getPatronymic() +
                            doctor.getCredentials().getLastName()));
            avatar.setMaxHeight("630px");
            avatar.setMaxWidth("530px");
            avatar.addClickListener(imageClickEvent -> {
                if(!firstCall) {
                    Dialog dialog = new Dialog();
                    dialog.setHeight("600px");
                    dialog.setWidth("1050px");
                    dialog.setCloseOnOutsideClick(false);
                    this.addToDialog(os, dialog, currentMimeType);
                }
            });
            Upload upload = new Upload(new FileReceiver());
            upload.setAcceptedFileTypes(allowedAvatarExtensions);
            upload.setMaxFiles(1);
            upload.setMaxFileSize(allowedFileSize);
            upload.setI18n(new UploadLocalizator());
            upload.addSucceededListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setHeight("600px");
                dialog.setWidth("1050px");
                dialog.setCloseOnOutsideClick(false);
                currentMimeType = event.getMIMEType();
                firstCall = false;
                this.addToDialog(os, dialog, event.getMIMEType());
            });
            Button applyAvatar = new Button(applyLabel, e -> {
                doctor.setDoctorPhoto(croppedAvatar);
                doctorService.updateSelf(doctor);
                UI.getCurrent().navigate(ProfileView.class);
            });
            applyAvatar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            add(new Div(new H4(uploadAvatar), upload));
            add(avatar);
            add(new HorizontalLayout(applyAvatar));
            List<RepresentativeDoctorSpecialityPojo> currentSpecialityList =
                    doctorSpecialityService.getDoctorSpecialities(doctor.getId());
            Grid<RepresentativeDoctorSpecialityPojo> grid = initGrid(doctor, currentSpecialityList);
            refreshSpecialityCombobox(grid);
            specialityNameComboBox.setItemLabelGenerator(SpecialityName::name);
            Button add = new Button(addSpeciality);
            add.addClickListener(e -> {
                newSpeciality = true;
                startSpecialityInit(specialityNameComboBox.getValue(), grid, currentSpecialityList);
                refreshSpecialityCombobox(grid);
                specialityNameComboBox.setValue(null);
            });
            discardCancel.setEnabled(false);
            discardCancel.setText(discardCancelLabel);
            discardCancel.addClickListener(e -> {
                GridListDataView<RepresentativeDoctorSpecialityPojo> listDataView = grid.getListDataView();
                grid.getEditor().cancel();
                if(newSpeciality) {
                    newSpeciality = false;
                    listDataView.removeItem(listDataView.getItem(listDataView.getItemCount() - 1));
                }
                refreshSpecialityCombobox(grid);
                specialityNameComboBox.setValue(null);
                discardCancel.setEnabled(false);
            });
            add(new Div(new H4(manageSpecialities), new HorizontalLayout(specialityNameComboBox, add, discardCancel)));
            add(grid);
        }
        add(changePassword);
        PasswordField password = new PasswordField();
        password.setLabel(newPassword);
        PasswordField passwordConfirmation = new PasswordField();
        passwordConfirmation.setLabel(confirmNewPassword);
        Button applyPassword = new Button(applyLabel);
        applyPassword.addClickListener(e -> {
            if(ValidatorUtils.isValidPassword(password.getValue())){
                if(password.getValue().equals(passwordConfirmation.getValue())) {
                    Credentials credentials = credentialsService.getSelf();
                    credentials.setPassword(password.getValue());
                    credentialsService.update(credentials, true);
                    UiUtils.generateSuccessNotification(youSuccessfullyChangedYourPassword).open();
                    password.clear();
                    passwordConfirmation.clear();
                    password.setInvalid(false);
                    passwordConfirmation.setInvalid(false);
                }
                else {
                    passwordConfirmation.clear();
                    passwordConfirmation.setErrorMessage(passwordsDoesNotMatch);
                    passwordConfirmation.setInvalid(true);
                }
            }
            else {
                password.clear();
                passwordConfirmation.clear();
                password.setErrorMessage(passwordPattern);
                password.setInvalid(true);
            }
        });
        applyPassword.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(password,passwordConfirmation,applyPassword);
    }

    private void populateAvailability(ToggleButton status, boolean available) {
        status.setValue(available);
        if(available){
            status.setLabel(availableLabel);
            status.getElement().getStyle().set("color", "#158443");
        }
        else {
            status.setLabel(unreachableLabel);
            status.getElement().getStyle().set("color", "#cf2821");
        }
    }

    private void refreshSpecialityCombobox(Grid grid) {
        specialityNameComboBox.setItems(findProperSpecialities(grid));
    }

    private List<SpecialityName> findProperSpecialities(Grid grid) {
        return Arrays.stream(SpecialityName.values()).filter(specialityName -> {
            return grid.getListDataView().getItems().
                    filter(item -> {
                        RepresentativeDoctorSpecialityPojo pojo = (RepresentativeDoctorSpecialityPojo) item;
                        return pojo.getSpeciality().equals(specialityName.name());
                    }).findAny().isEmpty();
        }).collect(Collectors.toList());
    }

    private Grid initGrid(Doctor doctor, List<RepresentativeDoctorSpecialityPojo> currentSpecialityList) {
        Grid<RepresentativeDoctorSpecialityPojo> grid = new Grid<>(RepresentativeDoctorSpecialityPojo.class, false);
        grid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader(specialityLabel);
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader(instituteOfAccreditation).setKey("institute");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader(workExperience).setTextAlign(ColumnTextAlign.CENTER).setKey("experience");
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, speciality) -> {
                    setupEdit(grid, button, speciality);
                })).setHeader(manage).setKey("manage");
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, speciality) -> {
                    setupDelete(currentSpecialityList, grid, button, speciality, doctor);
                })).setKey("save");
        GridListDataView<RepresentativeDoctorSpecialityPojo> dataView = grid.setItems(currentSpecialityList);
        grid.setAllRowsVisible(true);
        Editor<RepresentativeDoctorSpecialityPojo> editor = grid.getEditor();
        Binder<RepresentativeDoctorSpecialityPojo> binder = new Binder<>();
        editor.setBinder(binder);
        editor.setBuffered(true);
        TextField instituteField = new TextField();
        binder.forField(instituteField)
                .asRequired(instituteMustNotBeEmpty)
                .bind(RepresentativeDoctorSpecialityPojo::getInstitute, RepresentativeDoctorSpecialityPojo::setInstitute);
        grid.getColumnByKey("institute").setEditorComponent(instituteField);
        IntegerField integerField = new IntegerField();
        integerField.setValue(1);
        integerField.setHasControls(true);
        integerField.setMin(1);
        binder.forField(integerField)
                .bind(RepresentativeDoctorSpecialityPojo::getExperience, RepresentativeDoctorSpecialityPojo::setExperience);
        grid.getColumnByKey("experience").setEditorComponent(integerField);
        Button saveButton = new Button(saveLabel, e -> {
            editor.save();
            discardCancel.setEnabled(false);
            MessageResponse response;
            if(newSpeciality) {
                response = doctorSpecialityService.saveDoctorSpecialitySelf(editReference.get());
                populateNotification(response, specialityIsSuccessfullySaved);
            }
            else
            {
                response = doctorSpecialityService.updateDoctorSpecialitySelf(editReference.get());
                populateNotification(response, specialityIsSuccessfullyUpdated);
            }
            editReference.set(null);
            newSpeciality = null;

        });
        grid.getColumnByKey("save").setEditorComponent(saveButton);
        return grid;
    }

    private void populateNotification(MessageResponse response, String message) {
        if(response !=null){
            UiUtils.generateSuccessNotification(message).open();
        }
        else {
            UiUtils.generateErrorNotification(somethingWentWrongPleaseContactAdministrator).open();
        }
    }

    private void setupDelete(List<RepresentativeDoctorSpecialityPojo> currentSpecialityList, Grid<RepresentativeDoctorSpecialityPojo> grid, Button button, RepresentativeDoctorSpecialityPojo speciality, Doctor doctor) {
        button.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(e -> this.removeSpeciality(speciality, currentSpecialityList, grid));
        button.setIcon(new Icon(VaadinIcon.TRASH));
    }

    private void setupEdit(Grid<RepresentativeDoctorSpecialityPojo> grid, Button button, RepresentativeDoctorSpecialityPojo speciality) {
        editReference.set(speciality);
        Editor<RepresentativeDoctorSpecialityPojo> editor = grid.getEditor();
        button.addClickListener(e -> {
            discardCancel.setEnabled(true);
            if (editor.isOpen()) {
                editor.cancel();
            }
            editor.editItem(speciality);
        });
        button.setText(editLabel);
    }

    public void addToDialog(ByteArrayOutputStream os, Dialog dialog, String mimeType) {
        Button cropButton = new Button(cropLabel);

        CropperSettings cropperSettings = new CropperSettings();
        cropperSettings.setAspectRatio(1);
        cropperSettings.setViewMode(ViewMode.ONE);
        cropperSettings.setCroppedImageWidth(1000);
        cropperSettings.setCroppedImageHeight(1000);

        Cropper crop =
                new Cropper(
                        cropperSettings, java.util.Base64.getEncoder().encodeToString(os.toByteArray()), mimeType);
        crop.setHeight("500px");
        crop.setWidth("1000px");
        crop.setEncoderQuality(1.00);

        cropButton.addClickListener(
                event -> {
                    dialog.close();
                    String imageUri = crop.getImageUri();
                    avatar.setSrc(imageUri);
                    croppedAvatar = crop.getImageBase64();
                    UiUtils.generateSuccessNotification(avatarUploaded).open();
                });

        dialog.add(crop, cropButton);
        dialog.open();
    }

    private void refreshGrid(List<RepresentativeDoctorSpecialityPojo> doctorSpecialities,
                             Grid<RepresentativeDoctorSpecialityPojo> grid) {
        if (doctorSpecialities.size() > 0) {
            grid.setVisible(true);
            grid.getDataProvider().refreshAll();
        } else {
            grid.setVisible(false);
        }
    }

    private void startSpecialityInit(SpecialityName specialityName, Grid<RepresentativeDoctorSpecialityPojo> grid,
                                     List<RepresentativeDoctorSpecialityPojo> list) {
        RepresentativeDoctorSpecialityPojo doctorSpecialityPojo = new RepresentativeDoctorSpecialityPojo();
        doctorSpecialityPojo.setSpeciality(specialityName.name());
        doctorSpecialityPojo.setExperience(0);
        doctorSpecialityPojo.setInstitute("");
        editReference.set(doctorSpecialityPojo);
        grid.getListDataView().addItem(doctorSpecialityPojo);
        grid.getEditor().editItem(doctorSpecialityPojo);
        discardCancel.setEnabled(true);
        this.refreshGrid(list, grid);
    }

    private void removeSpeciality(RepresentativeDoctorSpecialityPojo pojo,
                                  List<RepresentativeDoctorSpecialityPojo> list,
                                  Grid<RepresentativeDoctorSpecialityPojo> grid) {
        if (pojo == null)
            return;
        ConfirmDialog
                .createQuestion()
                .withCaption(deleteConfirm)
                .withMessage(doYouSureWantToDeleteSpeciality)
                .withCancelButton(ButtonOption.caption(cancelLabel))
                .withOkButton(() -> {
                    list.remove(pojo);
                    MessageResponse response = doctorSpecialityService.deleteSelf(pojo);
                    populateNotification(response, specialitySuccessfullyDeleted);
                    this.refreshGrid(list, grid);
                    refreshSpecialityCombobox(grid);
                }, ButtonOption.focus(), ButtonOption.caption(yes))
                .open();
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        profileData.setText(getTranslation("profile.profile_data"));
        firstName = getTranslation("profile.first_name");
        patronymic = getTranslation("profile.patronymic");
        lastName = getTranslation("profile.last_name");
        phone = getTranslation("profile.phone");
        patient = getTranslation("profile.patient");
        doctor = getTranslation("profile.doctor");
        administrator = getTranslation("profile.admin");
        role = getTranslation("profile.role");
        enabled = getTranslation("profile.enabled");
        disabled = getTranslation("profile.disabled");
        twoFactor = getTranslation("profile.twofact");
        profileSettings.setText(getTranslation("profile.profile_settings"));
        use2Fa = getTranslation("profile.use2Fa");
        applyLabel = getTranslation("profile.apply");
        messageAvailable = getTranslation("profile.messages.available");
        messageUnreachable = getTranslation("profile.messages_unreachable");
        currentDoctorStatus.setText(getTranslation("profile.current_doctor_status"));
        uploadAvatar = getTranslation("profile.upload_avatar");
        addSpeciality = getTranslation("profile.add_speciality");
        discardCancelLabel = getTranslation("profile.discard_cancel");
        manageSpecialities = getTranslation("profile.manage_specialities");
        changePassword.setText(getTranslation("profile.change_password"));
        newPassword = getTranslation("profile.new_password");
        confirmNewPassword = getTranslation("profile.confirm_password");
        youSuccessfullyChangedYourPassword = getTranslation("profile.messages.you_successfully_changed_your_password");
        passwordsDoesNotMatch = getTranslation("profile.messages.passwords_do_not_match");
        passwordPattern = getTranslation("register.messages.password_pattern");
        availableLabel = getTranslation("profile.available");
        unreachableLabel = getTranslation("profile.unreachable");
        specialityLabel = getTranslation("profile.speciality");
        instituteOfAccreditation = getTranslation("profile.institute_of_accreditation");
        workExperience = getTranslation("profile.work_experience");
        manage = getTranslation("profile.manage");
        instituteMustNotBeEmpty = getTranslation("profile.institute_must_not_be_empty");
        saveLabel = getTranslation("profile.save");
        specialityIsSuccessfullySaved = getTranslation("profile.messages.speciality_is_successfully_saved");
        specialityIsSuccessfullyUpdated = getTranslation("profile.messages.speciality_is_successfully_updated");
        somethingWentWrongPleaseContactAdministrator = getTranslation("profile.messages.something_went_wrong_please_contact_administrator");
        editLabel = getTranslation("profile.edit");
        cropLabel = getTranslation("profile.crop");
        deleteConfirm = getTranslation("profile.deletion_confirmation");
        doYouSureWantToDeleteSpeciality = getTranslation("profile.messages.do_you_sure_want_to_delete_speciality");
        cancelLabel = getTranslation("profile.cancel");
        specialitySuccessfullyDeleted = getTranslation("profile.messages.speciality_successfully_deleted");
        yes = getTranslation("profile.yes");
        avatarUploaded = getTranslation("profile.messages.avatar_uploaded");
    }
}