package bsu.rpact.medionefrontend.validation;

import bsu.rpact.medionefrontend.exception.LoginAlreadyExsistsException;
import bsu.rpact.medionefrontend.exception.PhoneAlreadyExsistsException;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import bsu.rpact.medionefrontend.pojo.authentication.RegisterRequest;
import bsu.rpact.medionefrontend.service.AuthService;
import bsu.rpact.medionefrontend.utils.UiUtils;
import bsu.rpact.medionefrontend.utils.ValidatorUtils;
import bsu.rpact.medionefrontend.vaadin.components.RegistrationForm;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RegistrationFormBinder implements LocaleChangeObserver {

    private final AuthService authService;

    private RegistrationForm registrationForm;

    private boolean enablePasswordValidation;

    private String mobilePhoneNumberAlreadyReserved;
    private String enterAnotherPhoneNumber;
    private String loginAlreadyReserved;

    public RegistrationFormBinder(AuthService authService, RegistrationForm registrationForm) {
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
        Binder<RegisterRequest> binderBirthdate = new Binder<>(RegisterRequest.class);
        binderPhone.bindInstanceFields(registrationForm);
        binderFirstName.bindInstanceFields(registrationForm);
        binderLastName.bindInstanceFields(registrationForm);
        binderPatronymic.bindInstanceFields(registrationForm);
        binderLogin.bindInstanceFields(registrationForm);
        binderPassword.bindInstanceFields(registrationForm);
        binderBirthdate.bindInstanceFields(registrationForm);
        binderRole.bindInstanceFields(registrationForm);
        binderPassword.forField(registrationForm.getPasswordField())
                .withValidator(this::passwordValidate).bind("password");
        binderLogin.forField(registrationForm.getLogin())
                .withValidator(this::loginValidate).bind("login");
        binderFirstName.forField(registrationForm.getFirstName())
                .withValidator(this::literalValidate).bind("firstName");
        binderLastName.forField(registrationForm.getLastName())
                .withValidator(this::literalValidate).bind("lastName");
        binderPatronymic.forField(registrationForm.getPatronymic())
                .withValidator(this::literalValidate).bind("patronymic");
        binderPhone.forField(registrationForm.getPhone())
                .withValidator(this::phoneValidate).bind("phone");
        binderRole.forField(registrationForm.getRoleComboBox()).bind("role");
        binderBirthdate.forField(registrationForm.getDatePicker())
                .bind(new ValueProvider<RegisterRequest, LocalDate>() {
                    @Override
                    public LocalDate apply(RegisterRequest registerRequest) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        return LocalDate.parse(registrationForm.getDatePicker().getValue().format(formatter));
                    }
                }, new Setter<RegisterRequest, LocalDate>() {
                    @Override
                    public void accept(RegisterRequest registerRequest, LocalDate localDate) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        registerRequest.setBirthDate(localDate.format(formatter));
                    }
                });

        mobilePhoneNumberAlreadyReserved = registrationForm.getTranslation("register.messages.mobile_phone_number_already_reserved");
        enterAnotherPhoneNumber = registrationForm.getTranslation("register.messages.enter_another_phone_number");
        loginAlreadyReserved = registrationForm.getTranslation("register.messages.login_already_reserved");

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
                binderBirthdate.writeBean(userBean);
                MessageResponse response = authService.register(userBean);
                String message = response.getMessage().toUpperCase(Locale.ROOT);
                if(message.equals("PATIENT CREATED") ||
                   message.equals("DOCTOR CREATED")) {
                    UiUtils.generateSuccessRegistrationNotification(userBean).open();
                }
                else if(message.contains("PHONE ALREADY EXISTS")){
                    UiUtils.generateErrorNotification(mobilePhoneNumberAlreadyReserved).open();
                    registrationForm.getPhone().setErrorMessage(enterAnotherPhoneNumber);
                    throw new PhoneAlreadyExsistsException(mobilePhoneNumberAlreadyReserved);
                }
                else if(message.contains("LOGIN ALREADY EXISTS")){
                    UiUtils.generateErrorNotification(loginAlreadyReserved).open();
                    registrationForm.getLogin().setErrorMessage(enterAnotherPhoneNumber);
                    throw new LoginAlreadyExsistsException(loginAlreadyReserved);
                }
            } catch (IllegalArgumentException exception) {
                UiUtils.generateErrorNotification(registrationForm.getTranslation("register.messages.registration_failed"));
            } catch (ValidationException e) {
                UiUtils.generateErrorNotification(String.valueOf(e.getValidationErrors()));
            }
        });
    }

    /**
     *
     * @param pass1
     * @param ctx
     * @return
     */
    private ValidationResult passwordValidate(String pass1, ValueContext ctx) {
        if (pass1 == null || !ValidatorUtils.isValidPassword(pass1)) {
            return ValidationResult.error(registrationForm.getTranslation("register.messages.password_pattern"));
        }
        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }
        String pass2 = registrationForm.getPasswordConfirmField().getValue();
        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }
        return ValidationResult.error(registrationForm.getTranslation("register.messages.passwords_do_not_match"));
    }

    private ValidationResult loginValidate(String login, ValueContext ctx) {
        if (login == null || !ValidatorUtils.isValidLogin(login)) {
            return ValidationResult.error(
                    registrationForm.getTranslation("register.messages.login_pattern"));
        }
        return ValidationResult.ok();
    }

    private ValidationResult literalValidate(String literal, ValueContext ctx) {
        if (literal == null || !ValidatorUtils.isValidLiteral(literal)) {
            return ValidationResult.error(registrationForm.getTranslation("register.messages.literal_must_contain_only_letters"));
        }
        return ValidationResult.ok();
    }

    private ValidationResult phoneValidate(String phone, ValueContext ctx) {
        if (phone == null || !ValidatorUtils.isValidPhone(phone)) {
            return ValidationResult.error(registrationForm.getTranslation("register.messages.phone_pattern"));
        }
        return ValidationResult.ok();
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        mobilePhoneNumberAlreadyReserved = registrationForm.getTranslation("register.messages.mobile_phone_number_already_reserved");
        enterAnotherPhoneNumber = registrationForm.getTranslation("register.messages.enter_another_phone_number");
        loginAlreadyReserved = registrationForm.getTranslation("register.messages.login_already_reserved");
    }
}