package bsu.rpact.medionefrontend.validation;

import bsu.rpact.medionefrontend.exception.LoginAlreadyExsistsException;
import bsu.rpact.medionefrontend.exception.PhoneAlreadyExsistsException;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.pojo.authentication.RegisterRequest;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import bsu.rpact.medionefrontend.vaadin.RegistrationForm;
import com.vaadin.flow.data.binder.*;

import java.util.Locale;

public class RegistrationFormBinder {

    private final UiUtils uiUtils;
    private final ValidatorUtils validatorUtils;
    private final AuthService authService;

    private RegistrationForm registrationForm;

    private boolean enablePasswordValidation;

    private String phoneErrorText = "This mobile phone number already reserved";
    private String enterAnotherPhoneNumber = "Enter another phone number";
    private String loginAlreadyReserved = "This login already reserved";

    public RegistrationFormBinder(UiUtils uiUtils, ValidatorUtils validatorUtils, AuthService authService, RegistrationForm registrationForm) {
        this.uiUtils = uiUtils;
        this.validatorUtils = validatorUtils;
        this.authService = authService;
        this.registrationForm = registrationForm;
    }

    public void addBindingAndValidation() {
        Binder<RegisterRequest> binderLogin= new Binder<>(RegisterRequest.class);
        Binder<RegisterRequest> binderPassword = new Binder<>(RegisterRequest.class);
        Binder<RegisterRequest> binderFirstName = new Binder<>(RegisterRequest.class);
        Binder<RegisterRequest> binderLastName = new Binder<>(RegisterRequest.class);
        Binder<RegisterRequest> binderPatronymic= new Binder<>(RegisterRequest.class);
        Binder<RegisterRequest> binderPhone = new Binder<>(RegisterRequest.class);
        Binder<RegisterRequest> binderRole = new Binder<>(RegisterRequest.class);
        binderPhone.bindInstanceFields(registrationForm);
        binderFirstName.bindInstanceFields(registrationForm);
        binderLastName.bindInstanceFields(registrationForm);
        binderPatronymic.bindInstanceFields(registrationForm);
        binderLogin.bindInstanceFields(registrationForm);
        binderPassword.bindInstanceFields(registrationForm);
        binderRole.bindInstanceFields(registrationForm);
        binderPassword.forField(registrationForm.getPasswordField())
                .withValidator(this::passwordValidator).bind("password");
        binderLogin.forField(registrationForm.getLogin())
                .withValidator(this::loginValidator).bind("login");
        binderFirstName.forField(registrationForm.getFirstName())
                .withValidator(this::literalValidator).bind("firstName");
        binderLastName.forField(registrationForm.getLastName())
                .withValidator(this::literalValidator).bind("lastName");
        binderPatronymic.forField(registrationForm.getPatronymic())
                .withValidator(this::literalValidator).bind("patronymic");
        binderPhone.forField(registrationForm.getPhone())
                .withValidator(this::phoneValidator).bind("phone");
        binderRole.forField(registrationForm.getComboBox()).bind("role");

        registrationForm.getPasswordField().addValueChangeListener(e ->{
            enablePasswordValidation = true;
            binderPassword.validate();
        });
        registrationForm.getPasswordConfirmField().addValueChangeListener(e -> {
            enablePasswordValidation = true;
            binderPassword.validate();
        });
        registrationForm.getLogin().addValueChangeListener(e -> {
            binderLogin.validate();
        });
        registrationForm.getFirstName().addValueChangeListener(e -> {
            binderFirstName.validate();
        });
        registrationForm.getLastName().addValueChangeListener(e -> {
            binderLastName.validate();
        });
        registrationForm.getPatronymic().addValueChangeListener(e -> {
            binderPatronymic.validate();
        });
        registrationForm.getPhone().addValueChangeListener(e -> {
            binderPhone.validate();
        });

        registrationForm.getSubmitButton().addClickListener(event -> {
            try {
                RegisterRequest userBean = new RegisterRequest();
                binderPhone.writeBean(userBean);
                binderFirstName.writeBean(userBean);
                binderLastName.writeBean(userBean);
                binderPatronymic.writeBean(userBean);
                binderLogin.writeBean(userBean);
                binderPassword.writeBean(userBean);
                binderRole.writeBean(userBean);
                MessageResponse response = authService.register(userBean);
                String message = response.getMessage().toUpperCase(Locale.ROOT);
                if(message.equals("PATIENT CREATED") ||
                   message.equals("DOCTOR CREATED")  ||
                   message.equals("ADMIN CREATED")) {
                    uiUtils.generateSuccessRegistrationNotification(userBean).open();
                }
                else if(message.contains("PHONE ALREADY EXISTS")){
                    uiUtils.generateErrorNotification(phoneErrorText).open();
                    registrationForm.getPhone().setErrorMessage(enterAnotherPhoneNumber);
                    throw new PhoneAlreadyExsistsException(phoneErrorText);
                }
                else if(message.contains("LOGIN ALREADY EXISTS")){
                    uiUtils.generateErrorNotification(loginAlreadyReserved).open();
                    registrationForm.getLogin().setErrorMessage(enterAnotherPhoneNumber);
                    throw new LoginAlreadyExsistsException(loginAlreadyReserved);
                }
            } catch (IllegalArgumentException exception) {
                uiUtils.generateErrorNotification("Registration failed :(");
            } catch (ValidationException e) {
                uiUtils.generateErrorNotification(String.valueOf(e.getValidationErrors()));
            }
        });
    }

    /**
     *
     * @param pass1
     * @param ctx
     * @return
     */
    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
        if (pass1 == null || !validatorUtils.isValidPassword(pass1)) {
            return ValidationResult.error("Password must be" +
                    " 8-20 length\n" +
                    ",contain at least one letter\n" +
                    ",not contain any white space.");
        }
        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }
        String pass2 = registrationForm.getPasswordConfirmField().getValue();
        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }
        return ValidationResult.error("Passwords do not match");
    }

    private ValidationResult loginValidator(String login, ValueContext ctx) {
        if (login == null || !validatorUtils.isValidLogin(login)) {
            return ValidationResult.error(
                    "The 3 logins parts are:\n" +
                    "Maybe some digits as a prefix,\n" +
                    ",but then definitely a letter\n" +
                    ",and then maybe some digits and letters at the end");
        }
        return ValidationResult.ok();
    }

    private ValidationResult literalValidator(String literal, ValueContext ctx) {
        if (literal == null || !validatorUtils.isValidLiteral(literal)) {
            return ValidationResult.error("Literal must contain only letters\n");
        }
        return ValidationResult.ok();
    }

    private ValidationResult phoneValidator(String phone, ValueContext ctx) {
        if (phone == null || !validatorUtils.isValidPhone(phone)) {
            return ValidationResult.error("Phone starts with" +
                    " 1-3 country code," +
                    "then area code and subscriber number " +
                    "between 8-11 digits");
        }
        return ValidationResult.ok();
    }
}