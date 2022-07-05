package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "speciality", schema = "medione")
public class Speciality {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "description")
    private String description;

    public Speciality() {
    }

    public Speciality(String description) {
        this.description = description;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonBackReference(value = "doctorList")
    @JoinTable(
            name = "doctor_specialities",
            joinColumns = @JoinColumn(name = "ds_speciality_id"),
            inverseJoinColumns = @JoinColumn(name = "ds_doctor_id")
    )
    private List<Doctor> doctorList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    public void addDoctorToDoctorList(Doctor doctor){
        if(doctorList == null){
            doctorList = new ArrayList<>();
        }
        doctorList.add(doctor);
        doctor.getSpecialityList().add(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
