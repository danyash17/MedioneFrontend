package bsu.rpact.medionefrontend.vaadin;

import bsu.rpact.medionefrontend.enums.Role;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegistrationForm extends FormLayout {

    private H3 title;

    private TextField firstName;
    private TextField lastName;
    private TextField patronymic;

    private TextField phone;

    private ComboBox<String> comboBox;

    private TextField login;
    private PasswordField password;
    private PasswordField passwordConfirmation;

    private Checkbox agreeWithRules;

    private Button submitButton;


    public RegistrationForm() {
        title = new H3("Signup form");
        firstName = new TextField("First name");
        lastName = new TextField("Last name");
        patronymic = new TextField("Patronymic");
        phone = new TextField("Phone");
        comboBox = new ComboBox<>("Role");
        comboBox.setItems(Arrays.asList(Role.values()).stream().map(item -> item.name()).collect(Collectors.toList()));
        agreeWithRules = new Checkbox("Agree with rules");
        agreeWithRules.getStyle().set("margin-top", "10px");

        login = new TextField("Login");
        password = new PasswordField("Password");
        passwordConfirmation = new PasswordField("Confirm password");

        setRequiredIndicatorVisible(firstName, lastName, patronymic, phone, comboBox, login, password,
                passwordConfirmation, agreeWithRules);

        submitButton = new Button("Register");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title, firstName, lastName, patronymic,
                login, password, passwordConfirmation, phone, comboBox,
                agreeWithRules,
                submitButton);

        setMaxWidth("500px");
        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

        setColspan(title, 2);
        setColspan(phone, 2);
        setColspan(comboBox, 2);
        setColspan(submitButton, 2);
    }

    public PasswordField getPasswordField() { return password; }

    public PasswordField getPasswordConfirmField() { return passwordConfirmation; }

    public Button getSubmitButton() { return submitButton; }

    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }

    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox<String> comboBox) {
        this.comboBox = comboBox;
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
}