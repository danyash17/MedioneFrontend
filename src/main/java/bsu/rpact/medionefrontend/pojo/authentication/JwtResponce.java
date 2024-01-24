package bsu.rpact.medionefrontend.pojo.authentication;

import bsu.rpact.medionefrontend.enums.Role;

public class JwtResponce {
    private static final String TOKEN_TYPE = "BEARER";
    private String token;
    private Integer id;
    private String login;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String phone;
    private Role role;

    public JwtResponce(String token, Integer id, String login, String firstName, String lastName, String patronymic, String phone, Role role) {
        this.token = token;
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.role = role;
    }

    public JwtResponce() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
}
