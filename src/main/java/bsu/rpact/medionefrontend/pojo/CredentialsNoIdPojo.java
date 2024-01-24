package bsu.rpact.medionefrontend.pojo;

import bsu.rpact.medionefrontend.enums.Role;

import java.sql.Date;

public class CredentialsNoIdPojo {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
    private boolean isEnabled2Fa;
    private String phone;
    private Role role;
    private Date birthDate;

    public CredentialsNoIdPojo(String login, String password, String firstName, String lastName, String patronymic,
                               boolean isEnabled2Fa, String phone, Role role, Date birthDate) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.isEnabled2Fa = isEnabled2Fa;
        this.phone = phone;
        this.role = role;
        this.birthDate = birthDate;
    }

    public CredentialsNoIdPojo() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled2Fa() {
        return isEnabled2Fa;
    }

    public void setEnabled2Fa(boolean enabled2Fa) {
        isEnabled2Fa = enabled2Fa;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
}
