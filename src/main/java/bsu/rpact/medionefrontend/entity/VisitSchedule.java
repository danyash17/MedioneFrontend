package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visit_schedule", schema = "medione")
public class VisitSchedule {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    public VisitSchedule() {
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name = "schedule_assert",
                joinColumns = @JoinColumn(name = "schedule_id"),
                inverseJoinColumns = @JoinColumn(name = "visit_id"))
    @JsonManagedReference
    private List<Visit> visitList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    public void addVisitToVisitSchedule(Visit visit){
        if(visitList == null){
            visitList = new ArrayList<>();
        }
        visitList.add(visit);
    }

    @PreRemove
    public void preRemove(){
        visitList.forEach(item -> {
            item.getDoctor().setVisitSchedule(null);
            item.getPatient().setVisitSchedule(null);
        });
    }

}
