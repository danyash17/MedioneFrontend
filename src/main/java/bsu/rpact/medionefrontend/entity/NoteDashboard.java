package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "note_dashboard", schema = "medione")
public class NoteDashboard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    public NoteDashboard() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "noteDashboard")
    @JsonManagedReference
    private List<Note> noteList;

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void addNoteToNoteDashboard(Note note){
        if(noteList == null){
            noteList = new ArrayList<>();
        }
        noteList.add(note);
        note.setNoteDashboard(this);
    }

}
