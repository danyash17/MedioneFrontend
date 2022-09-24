package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.entity.Credentials;
import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.service.CredentialsService;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.session.SessionManager;
import bsu.rpact.medionefrontend.utils.ImageUtils;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
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

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends VerticalLayout {

    private final SessionManager sessionManager;
    private final CredentialsService credentialsService;
    private final DoctorService doctorService;
    private final AuthService authService;
    private final ImageUtils imageUtils;

    private final static String[] allowedAvatarExtensions = new String[]{"image/*"};
    private final static int allowedFileSize = 20 * 1024 * 1024;
    private final static ByteArrayOutputStream os = new ByteArrayOutputStream(allowedFileSize);

    private Image avatar = new Image();
    private String currentMimeType;
    private byte[] croppedAvatar = new byte[allowedFileSize];
    private boolean firstCall = true;

    private class FileReceiver implements Receiver {

        @Override
        public OutputStream receiveUpload(String fileName, String mimeType) {
            os.reset();
            return os;
        }
    }

    public ProfileView(SessionManager sessionManager, CredentialsService credentialsService, DoctorService doctorService, AuthService authService, ImageUtils imageUtils) {
        this.sessionManager = sessionManager;
        this.credentialsService = credentialsService;
        this.doctorService = doctorService;
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
            UI.getCurrent().getPage().reload();
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
                UI.getCurrent().getPage().reload();
            });
            add(new Div(new H4("Upload avatar"), upload));
            add(avatar);
            add(new HorizontalLayout(applyAvatar));
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
}