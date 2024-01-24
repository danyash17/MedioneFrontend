package bsu.rpact.medionefrontend.pojo.other;

import bsu.rpact.medionefrontend.entity.Doctor;
import com.vaadin.flow.component.html.Image;

public class DoctorPhotoUrlContainer {

    private Doctor doctor;
    private String photoUrl;

    public DoctorPhotoUrlContainer() {
    }

    public DoctorPhotoUrlContainer(Doctor doctor, String photoUrl) {
        this.doctor = doctor;
        this.photoUrl = photoUrl;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
