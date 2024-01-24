package bsu.rpact.medionefrontend.pojo;

public class RepresentativeDoctorSpecialityPojo {
    private String doctor;
    private String speciality;
    private Integer experience;
    private String institute;

    public RepresentativeDoctorSpecialityPojo(String doctor, String speciality, Integer experience, String institute) {
        this.doctor = doctor;
        this.speciality = speciality;
        this.experience = experience;
        this.institute = institute;
    }

    public RepresentativeDoctorSpecialityPojo() {
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }
}
