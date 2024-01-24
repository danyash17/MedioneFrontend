package bsu.rpact.medionefrontend.vaadin.components;

import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.StreamResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RegistrationForm extends FormLayout implements LocaleChangeObserver {

    private H3 title;

    private TextField firstName;
    private TextField lastName;
    private TextField patronymic;

    private TextField phone;

    private ComboBox<String> roleComboBox;

    private TextField login;
    private PasswordField password;
    private PasswordField passwordConfirmation;

    private DatePicker datePicker;

    private Checkbox agreeWithRules;

    private final Button viewRulesButton;
    private Button submitButton;


    public RegistrationForm() {
        title = new H3(getTranslation("registration.title"));
        firstName = new TextField(getTranslation("registration.first_name"));
        lastName = new TextField(getTranslation("registration.last_name"));
        patronymic = new TextField(getTranslation("registration.patronymic"));
        phone = new TextField(getTranslation("registration.phone"));
        roleComboBox = new ComboBox<>(getTranslation("registration.role"));
        datePicker = new DatePicker(getTranslation("registration.birthdate"));
        datePicker.setPlaceholder(getTranslation("registration.birthdate.pattern"));
        List<String> list = new ArrayList<>();
        list.add("PATIENT");
        list.add("DOCTOR");
        roleComboBox.setItems(list);
        agreeWithRules = new Checkbox(getTranslation("registration.rules"));
        agreeWithRules.getStyle().set("margin-top", "10px");

        login = new TextField(getTranslation("registration.login"));
        password = new PasswordField(getTranslation("registration.password"));
        passwordConfirmation = new PasswordField(getTranslation("registration.password_confirm"));

        setRequiredIndicatorVisible(firstName, lastName, patronymic, phone, roleComboBox, datePicker, login, password,
                passwordConfirmation, agreeWithRules);

        viewRulesButton = new Button(getTranslation("registration.view_rules"));
        viewRulesButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("900px");
            PdfViewer pdfViewer = new PdfViewer();
            StreamResource resource = new StreamResource("Software-Usage-Policy.pdf",
                    () -> getClass().getResourceAsStream("../../../../../META-INF/resources/policy/Software-Usage-Policy.pdf"));
            pdfViewer.setSrc(resource);
            dialog.add(pdfViewer);
            dialog.setCloseOnEsc(true);
            dialog.open();
        });
        submitButton = new Button(getTranslation("registration.register_button"));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title, firstName, lastName, patronymic,
                login, password, passwordConfirmation, phone, datePicker, roleComboBox,
                agreeWithRules, viewRulesButton,
                submitButton);

        setMaxWidth("500px");
        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

        setColspan(title, 2);
        setColspan(phone, 2);
        setColspan(datePicker,2);
        setColspan(roleComboBox, 2);
        setColspan(submitButton, 2);
    }

    public PasswordField getPasswordField() { return password; }

    public PasswordField getPasswordConfirmField() { return passwordConfirmation; }

    public Button getSubmitButton() { return submitButton; }

    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }

    public ComboBox<String> getRoleComboBox() {
        return roleComboBox;
    }

    public void setRoleComboBox(ComboBox<String> roleComboBox) {
        this.roleComboBox = roleComboBox;
    }

    public TextField getPhone() {
        return phone;
    }

    public void setPhone(TextField phone) {
        this.phone = phone;
    }

    public TextField getLogin() {
        return login;
    }

    public void setLogin(TextField login) {
        this.login = login;
    }

    public TextField getFirstName() {
        return firstName;
    }

    public void setFirstName(TextField firstName) {
        this.firstName = firstName;
    }

    public TextField getLastName() {
        return lastName;
    }

    public void setLastName(TextField lastName) {
        this.lastName = lastName;
    }

    public TextField getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(TextField patronymic) {
        this.patronymic = patronymic;
    }

    public PasswordField getPassword() {
        return password;
    }

    public void setPassword(PasswordField password) {
        this.password = password;
    }

    public PasswordField getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(PasswordField passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        title.setText(getTranslation("registration.title"));
        firstName.setLabel(getTranslation("registration.first_name"));
        lastName.setLabel(getTranslation("registration.last_name"));
        patronymic.setLabel(getTranslation("registration.patronymic"));
        phone.setLabel(getTranslation("registration.phone"));
        roleComboBox.setLabel(getTranslation("registration.role"));
        datePicker.setLabel(getTranslation("registration.birthdate"));
        agreeWithRules.setLabel(getTranslation("registration.rules"));
        login.setLabel(getTranslation("registration.login"));
        password.setLabel(getTranslation("registration.password"));
        passwordConfirmation.setLabel(getTranslation("registration.password_confirm"));
        viewRulesButton.setText(getTranslation("registration.view_rules"));
    }
}