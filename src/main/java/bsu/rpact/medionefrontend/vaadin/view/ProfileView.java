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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;
import de.f0rce.cropper.Cropper;
import de.f0rce.cropper.settings.CropperSettings;
import de.f0rce.cropper.settings.enums.ViewMode;
import org.vaadin.addons.badge.Badge;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends VerticalLayout {

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
        add(new H3("Profile data"));
        add(new Label("First name: " + sessionManager.getFirstNameAttribute()));
        add(new Label("Patronymic: " + sessionManager.getPatronymicAttribute()));
        add(new Label("Last name: " + sessionManager.getLastNameAttribute()));
        add(new Label("Phone: " + sessionManager.getPhoneAttribute()));
        Badge badgeRole = new Badge();
        badgeRole.setPrimary(true);
        badgeRole.setPill(true);
        switch (sessionManager.getRoleAttribute()){
            case "PATIENT": {
                badgeRole.setText("Patient");
                badgeRole.setIcon(VaadinIcon.USER.create());
                badgeRole.setVariant(Badge.BadgeVariant.NORMAL);
                break;
            }
            case "DOCTOR": {
                badgeRole.setText("Doctor");
                badgeRole.setIcon(VaadinIcon.DOCTOR.create());
                badgeRole.setVariant(Badge.BadgeVariant.NORMAL);
                break;
            }
            case "ADMIN": {
                badgeRole.setText("Administrator");
                badgeRole.setIcon(VaadinIcon.SPECIALIST.create());
                badgeRole.setVariant(Badge.BadgeVariant.NORMAL);
                break;
            }
        }
        add(new HorizontalLayout(new Label("Role: "), badgeRole));
        Badge badge2Fa = new Badge();
        badge2Fa.setPrimary(true);
        badge2Fa.setPill(true);
        if(sessionManager.get2FaAttribute().booleanValue()){
            badge2Fa.setText("Enabled");
            badge2Fa.setVariant(Badge.BadgeVariant.CONTRAST);
            badge2Fa.setIcon(VaadinIcon.LOCK.create());
        }
        else {
            badge2Fa.setText("Disabled");
            badge2Fa.setVariant(Badge.BadgeVariant.SUCCESS);
            badge2Fa.setIcon(VaadinIcon.UNLOCK.create());
        }
        add(new HorizontalLayout(new Label("Two-factor authorization"), badge2Fa));
        add(new H3("Profile settings"));
        Checkbox checkbox = new Checkbox();
        checkbox.setLabel("Use Two-Factor Authentication for this account");
        checkbox.setValue(session.getAttribute("2FA").equals("true"));
        add(checkbox);
        HorizontalLayout layout = new HorizontalLayout();
        Button apply = new Button("Apply", e -> {
            Credentials credentials = credentialsService.getSelf();
            credentials.setEnabled2Fa(checkbox.getValue());
            credentialsService.update(credentials, true);
            sessionManager.set2FaAttribute(checkbox.getValue());
            UI.getCurrent().navigate(ProfileView.class);
        });
        layout.add(apply);
        add(layout);
        if(sessionManager.getRoleAttribute().equals("DOCTOR")) {
            Doctor doctor = doctorService.getDoctorSelf();
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
            upload.addSucceededListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setHeight("600px");
                dialog.setWidth("1050px");
                dialog.setCloseOnOutsideClick(false);
                currentMimeType = event.getMIMEType();
                firstCall = false;
                this.addToDialog(os, dialog, event.getMIMEType());
            });
            Button applyAvatar = new Button("Apply", e -> {
                doctor.setDoctorPhoto(croppedAvatar);
                doctorService.updateSelf(doctor);
                UI.getCurrent().navigate(ProfileView.class);
            });
            add(new Div(new H4("Upload avatar"), upload));
            add(avatar);
            add(new HorizontalLayout(applyAvatar));
            List<RepresentativeDoctorSpecialityPojo> currentSpecialityList =
                    doctorSpecialityService.getDoctorSpecialities(doctor.getId());
            Grid<RepresentativeDoctorSpecialityPojo> grid = initGrid(doctor, currentSpecialityList);
            refreshSpecialityCombobox(grid);
            specialityNameComboBox.setItemLabelGenerator(SpecialityName::name);
            Button add = new Button("Add Speciality");
            add.addClickListener(e -> {
                newSpeciality = true;
                startSpecialityInit(specialityNameComboBox.getValue(), grid, currentSpecialityList);
                refreshSpecialityCombobox(grid);
                specialityNameComboBox.setValue(null);
            });
            discardCancel.setEnabled(false);
            discardCancel.setText("Discard/Cancel");
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
            add(new Div(new H4("Manage specialities"), new HorizontalLayout(specialityNameComboBox, add, discardCancel)));
            add(grid);
        }
        add(new H4("Change password"));
        PasswordField password = new PasswordField();
        password.setLabel("New password");
        PasswordField passwordConfirmation = new PasswordField();
        passwordConfirmation.setLabel("Confirm new password");
        Button applyPassword = new Button("Apply");
        applyPassword.addClickListener(e -> {
            if(ValidatorUtils.isValidPassword(password.getValue())){
                if(password.getValue().equals(passwordConfirmation.getValue())) {
                    Credentials credentials = credentialsService.getSelf();
                    credentials.setPassword(password.getValue());
                    credentialsService.update(credentials, true);
                    UiUtils.generateSuccessNotification("You successfully changed your password").open();
                    password.clear();
                    passwordConfirmation.clear();
                    password.setInvalid(false);
                    passwordConfirmation.setInvalid(false);
                }
                else {
                    passwordConfirmation.clear();
                    passwordConfirmation.setErrorMessage("Passwords does not match");
                    passwordConfirmation.setInvalid(true);
                }
            }
            else {
                password.clear();
                passwordConfirmation.clear();
                password.setErrorMessage("Password must be" +
                        "8-20 length\n" +
                        ",contain at least one letter\n" +
                        ",not contain any white space.");
                password.setInvalid(true);
            }
        });
        add(password,passwordConfirmation,applyPassword);
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
        grid.addColumn(doctorSpeciality -> doctorSpeciality.getSpeciality()).setHeader("Speciality");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getInstitute).setHeader("Institute of Accreditation").setKey("institute");
        grid.addColumn(RepresentativeDoctorSpecialityPojo::getExperience).setHeader("Work Experience").setTextAlign(ColumnTextAlign.CENTER).setKey("experience");
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, speciality) -> {
                    setupEdit(grid, button, speciality);
                })).setHeader("Manage").setKey("manage");
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
                .asRequired("Institute must not be empty")
                .bind(RepresentativeDoctorSpecialityPojo::getInstitute, RepresentativeDoctorSpecialityPojo::setInstitute);
        grid.getColumnByKey("institute").setEditorComponent(instituteField);
        IntegerField integerField = new IntegerField();
        integerField.setValue(1);
        integerField.setHasControls(true);
        integerField.setMin(1);
        binder.forField(integerField)
                .bind(RepresentativeDoctorSpecialityPojo::getExperience, RepresentativeDoctorSpecialityPojo::setExperience);
        grid.getColumnByKey("experience").setEditorComponent(integerField);
        Button saveButton = new Button("Save", e -> {
            editor.save();
            discardCancel.setEnabled(false);
            MessageResponse response;
            if(newSpeciality) {
                response = doctorSpecialityService.saveDoctorSpecialitySelf(editReference.get());
                populateNotification(response, "Speciality is successfully saved");
            }
            else
            {
                response = doctorSpecialityService.updateDoctorSpecialitySelf(editReference.get());
                populateNotification(response, "Speciality is successfully updated");
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
            UiUtils.generateErrorNotification("Something went wrong, please contact administrator").open();
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
        button.setText("Edit");
    }

    public void addToDialog(ByteArrayOutputStream os, Dialog dialog, String mimeType) {
        Button cropButton = new Button("Crop");

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
        list.remove(pojo);
        MessageResponse response = doctorSpecialityService.deleteSelf(pojo);
        populateNotification(response, "Speciality successfully deleted");
        this.refreshGrid(list, grid);
        refreshSpecialityCombobox(grid);
    }

}