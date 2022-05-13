package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "note", schema = "medione")
public class Note {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "text")
    private String text;
    @Basic
    @Column(name = "name")
    private String name;

    public Note() {
    }

    public Note(String text, String name) {
        this.text = text;
        this.name = name;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "dashboard_id")
    @JsonBackReference
    private NoteDashboard noteDashboard;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NoteDashboard getNoteDashboard() {
        return noteDashboard;
    }

    public void setNoteDashboard(NoteDashboard noteDashboard) {
        this.noteDashboard = noteDashboard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PreRemove
    public void preRemove() {
        if (noteDashboard != null) {
            noteDashboard.getNoteList().remove(this);
            noteDashboard = null;
        }
    }
}
