package bsu.rpact.medionefrontend.pojo;

public class DoctorSpecialityPojo {
    private String description;
    private Integer experience;
    private String institute;

    public DoctorSpecialityPojo(String description, Integer experience, String institute) {
        this.description = description;
        this.experience = experience;
        this.institute = institute;
    }

    public DoctorSpecialityPojo() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
