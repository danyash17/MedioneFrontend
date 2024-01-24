package bsu.rpact.medionefrontend.entity;

import bsu.rpact.medionefrontend.enums.Role;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "credentials", schema = "medione",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "login"),
                @UniqueConstraint(columnNames = "phone")
        })
public class Credentials {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "login")
    private String login;
    @Basic
    @Column(name = "password")
    private String password;
    @Basic
    @Column(name = "first_name")
    private String firstName;
    @Basic
    @Column(name = "last_name")
    private String lastName;
    @Basic
    @Column(name = "patronymic")
    private String patronymic;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "twofact_auth")
    private boolean isEnabled2Fa;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @Basic
    @Column(name = "birth_date")
    private Date birthDate;

    public Credentials() {
    }

    public Credentials(String login, String password, String firstName,
                       String lastName, String patronymic, String phone,
                       boolean isEnabled2Fa, Role role, Date birthDate) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.isEnabled2Fa = isEnabled2Fa;
        this.role = role;
        this.birthDate = birthDate;
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
