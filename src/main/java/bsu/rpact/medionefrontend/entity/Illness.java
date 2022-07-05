package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "illness", schema = "medione")
public class Illness {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "ill_from")
    private Date illFrom;
    @Basic
    @Column(name = "ill_to")
    private Date illTo;
    @Basic
    @Column(name = "description")
    private String description;

    public Illness() {
    }

    public Illness(Date illFrom, Date illTo, String description) {
        this.illFrom = illFrom;
        this.illTo = illTo;
        this.description = description;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "illness_medcard_id")
    @JsonBackReference(value = "medcard")
    private Medcard medcard;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getIllFrom() {
        return illFrom;
    }

    public void setIllFrom(Date illFrom) {
        this.illFrom = illFrom;
    }

    public Date getIllTo() {
        return illTo;
    }

    public void setIllTo(Date illTo) {
        this.illTo = illTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Medcard getMedcard() {
        return medcard;
    }

    public void setMedcard(Medcard medcard) {
        this.medcard = medcard;
    }

}
