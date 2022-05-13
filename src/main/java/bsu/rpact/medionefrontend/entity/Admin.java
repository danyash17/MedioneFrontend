package bsu.rpact.medionefrontend.entity;

import bsu.rpact.medionefrontend.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "admin", schema = "medione")
public class Admin implements User {

    private static final transient Role role = Role.ADMIN;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Admin() {
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credential_id")
    @JsonManagedReference
    private Credentials credentials;

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
